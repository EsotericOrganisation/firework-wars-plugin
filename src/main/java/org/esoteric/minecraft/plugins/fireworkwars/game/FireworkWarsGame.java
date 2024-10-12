package org.esoteric.minecraft.plugins.fireworkwars.game;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.data.TeamData;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.components.ChestLocation;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.structure.Arena;
import org.esoteric.minecraft.plugins.fireworkwars.events.game.GameEventListener;
import org.esoteric.minecraft.plugins.fireworkwars.game.runnables.GameCountdown;
import org.esoteric.minecraft.plugins.fireworkwars.game.runnables.GameTickHandler;
import org.esoteric.minecraft.plugins.fireworkwars.game.team.FireworkWarsTeam;
import org.esoteric.minecraft.plugins.fireworkwars.game.team.TeamPlayer;
import org.esoteric.minecraft.plugins.fireworkwars.items.AbstractItem;
import org.esoteric.minecraft.plugins.fireworkwars.language.Message;
import org.esoteric.minecraft.plugins.fireworkwars.util.Util;

import java.util.*;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class FireworkWarsGame {
    private final FireworkWarsPlugin plugin;
    private final Arena arena;

    private final GameEventListener eventListener;
    private GameTickHandler tickHandler;

    private GameState gameState = GameState.WAITING;
    private final Map<String, Boolean> worldLoadStates = new HashMap<>();

    private final List<FireworkWarsTeam> teams = new ArrayList<>();
    private final List<TeamPlayer> players = new ArrayList<>();

    public FireworkWarsPlugin getPlugin() {
        return plugin;
    }

    public Arena getArena() {
        return arena;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Map<String, Boolean> getWorldLoadStates() {
        return worldLoadStates;
    }

    public List<FireworkWarsTeam> getTeams() {
        return teams;
    }

    public List<TeamPlayer> getPlayers() {
        return players;
    }

    public boolean isPlaying() {
        return gameState == GameState.PLAYING;
    }

    public boolean isWaiting() {
        return gameState == GameState.WAITING;
    }

    public boolean isStarting() {
        return gameState == GameState.STARTING;
    }

    public boolean isResetting() {
        return gameState == GameState.RESETTING;
    }

    public boolean isAlive(Player player) {
        return containsPlayer(player) && player.getGameMode() != GameMode.SPECTATOR;
    }

    public boolean isSpectator(Player player) {
        return !isAlive(player);
    }

    public boolean containsPlayer(Player player) {
        TeamPlayer teamPlayer = TeamPlayer.from(player.getUniqueId());
        return players.contains(teamPlayer);
    }

    public boolean usesWorld(String worldName) {
        return arena.getWorlds().contains(worldName);
    }

    public boolean isTeamEliminated(FireworkWarsTeam team) {
        return team.getPlayers().stream()
                .map(TeamPlayer::getPlayer)
                .filter(Objects::nonNull)
                .allMatch(this::isSpectator);
    }

    public List<FireworkWarsTeam> getRemainingTeams() {
        return teams
                .stream()
                .filter(team -> !isTeamEliminated(team))
                .toList();
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public FireworkWarsGame(FireworkWarsPlugin plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;

        this.eventListener = new GameEventListener(plugin, this);

        for (String worldName : arena.getWorlds()) {
            worldLoadStates.put(worldName, true);
        }
    }

    public void addPlayer(Player player) {
        TeamPlayer teamPlayer = new TeamPlayer(player.getUniqueId(), this);

        players.add(teamPlayer);

        if (isWaiting() && players.size() >= arena.getMinimumPlayerCount()) {
            startCountdown();
        }

        teamPlayer.teleportToWaitingArea();
        player.setGameMode(GameMode.ADVENTURE);
    }

    public void sendMessage(Message message, Object... arguments) {
        for (TeamPlayer player : players) {
            plugin.getLanguageManager().sendMessage(message, player.getPlayer(), arguments);
        }
    }

    public void sendMessage(Component component) {
        for (TeamPlayer player : players) {
            player.getPlayer().sendMessage(component);
        }
    }

    public void playSound(Sound sound) {
        for (TeamPlayer player : players) {
            player.getPlayer().playSound(player.getPlayer().getLocation(), sound, 1.0f, 1.0f);
        }
    }


    private void startCountdown() {
        new GameCountdown(plugin, this);
    }

    public void startGame() {
        gameState = GameState.PLAYING;

        eventListener.register();

        tickHandler = new GameTickHandler(plugin, this);
        tickHandler.start();

        try {
            fillChests(1.0);
        } catch (Exception e) {
            plugin.getLogger().warning(e.getMessage());
        }

        for (TeamData teamData : arena.getTeamInformation()) {
            teams.add(new FireworkWarsTeam(teamData, this, plugin));
        }

        for (TeamPlayer player : getPlayers()) {
            plugin.getResetInventoryCommand().giveItems(player.getPlayer());
            plugin.getHealCommand().healPlayer(player.getPlayer());
        }

        distributePlayersAcrossTeams();
        players.forEach(TeamPlayer::showScoreboard);
    }

    public void preEndGame(FireworkWarsTeam winningTeam) {
        sendMessage(Message.TEAM_WON, winningTeam.getColoredTeamName());
        players.forEach(TeamPlayer::becomeSpectator);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> endGame(winningTeam), 20 * 10L);
    }

    public void endGame(FireworkWarsTeam winningTeam) {
        eventListener.unregister();
        tickHandler.cancel();

        players.forEach(TeamPlayer::teleportToLobby);
        players.forEach(player -> player.unregister(false));

        teams.clear();
        players.clear();

        gameState = GameState.RESETTING;
        plugin.getServer().getScheduler().runTaskLater(plugin, this::resetMap, 1L);
    }

    private void resetMap() {
        for (String worldName : arena.getWorlds()) {
            worldLoadStates.put(worldName, false);

            Bukkit.unloadWorld(worldName, false);
            World world = Bukkit.createWorld(new WorldCreator(worldName));

            assert world != null;
            world.setAutoSave(false);
        }
    }

    private void distributePlayersAcrossTeams() {
        for (int i = 0; i < players.size(); i++) {
            int teamIndex = i % teams.size();

            FireworkWarsTeam team = teams.get(teamIndex);
            players.get(i).joinTeam(team);
        }
    }

    public void fillChests(double valueFactor) {
        for (ChestLocation chestLocation : arena.getChestLocations()) {
            Chest chest = (Chest) chestLocation.getChestBlock().getState();

            int maxTotalValue = (int) (chestLocation.getMaxTotalValue() * valueFactor);
            int maxItemValue = (int) (chestLocation.getMaxValuePerItem() * valueFactor);

            List<ItemStack> itemsToAdd = new ArrayList<>();

            int i = 0;
            while (i < maxTotalValue) {
                AbstractItem item = plugin.getCustomItemManager().getWeightedRandomItem();

                if (item.getValue() > maxItemValue) {
                    continue;
                }

                itemsToAdd.add(item.getItem(null, item.getStackAmount()));
                i += item.getValue();
            }

            List<Integer> slots = Util.orderedNumberList(0, chest.getInventory().getSize() - 1);
            Collections.shuffle(slots);

            for (ItemStack item : itemsToAdd) {
                int slot = slots.remove(0);
                chest.getInventory().setItem(slot, item);
            }
        }
    }

    public void supplyDrop() {
        Location location = arena.getSupplyDropData().getRandomLocation().getBukkitLocation();
        StorageMinecart chestMinecart = (StorageMinecart) location.getWorld().spawnEntity(location, EntityType.CHEST_MINECART);

        chestMinecart.customName(Component.text("Supply Drop"));

        chestMinecart.getInventory().addItem(
                plugin.getCustomItemManager().getItem("rocket_launcher").getItem(null));

        sendMessage(Message.EVENT_SUPPLY_DROP, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public void startEndgame() {
        sendMessage(Message.EVENT_ENDGAME);
    }

    public void eliminateTeam(FireworkWarsTeam team) {
        sendMessage(Message.TEAM_ELIMINATED, team.getColoredTeamName());
    }

    public enum GameState {
        WAITING,
        STARTING,
        PLAYING,
        RESETTING
    }
}
