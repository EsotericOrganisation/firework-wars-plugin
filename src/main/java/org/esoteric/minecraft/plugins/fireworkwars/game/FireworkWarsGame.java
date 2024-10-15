package org.esoteric.minecraft.plugins.fireworkwars.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.data.TeamData;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.structure.Arena;
import org.esoteric.minecraft.plugins.fireworkwars.events.game.GameEventListener;
import org.esoteric.minecraft.plugins.fireworkwars.game.chests.ChestManager;
import org.esoteric.minecraft.plugins.fireworkwars.game.runnables.GameCountdown;
import org.esoteric.minecraft.plugins.fireworkwars.game.runnables.GameTickHandler;
import org.esoteric.minecraft.plugins.fireworkwars.game.team.FireworkWarsTeam;
import org.esoteric.minecraft.plugins.fireworkwars.game.team.TeamPlayer;
import org.esoteric.minecraft.plugins.fireworkwars.items.CustomItemManager;
import org.esoteric.minecraft.plugins.fireworkwars.language.LanguageManager;
import org.esoteric.minecraft.plugins.fireworkwars.language.Message;
import org.esoteric.minecraft.plugins.fireworkwars.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.kyori.adventure.title.Title.title;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class FireworkWarsGame {
    private final FireworkWarsPlugin plugin;
    private final LanguageManager languageManager;
    private final CustomItemManager customItemManager;

    private final Arena arena;
    private final ChestManager chestManager;

    private final GameEventListener eventListener;
    private GameTickHandler tickHandler;

    private @Nullable GameCountdown countdown;

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
        return gameState == GameState.PLAYING && containsPlayer(player) && player.getGameMode() != GameMode.SPECTATOR;
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
        this.languageManager = plugin.getLanguageManager();
        this.customItemManager = plugin.getCustomItemManager();

        this.arena = arena;
        this.chestManager = new ChestManager(plugin, this);

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

        sendMessage(Message.ARENA_JOIN, player.displayName(), players.size(), arena.getMaximumPlayerCount());
    }

    public void removePlayer(@NotNull TeamPlayer player) {
        player.unregister(true);

        if (players.size() < arena.getMinimumPlayerCount()) {
            if (countdown != null) {
                countdown.cancel();
                gameState = GameState.WAITING;
            }
        }

        player.teleportToLobby();
        sendMessage(Message.ARENA_LEAVE, player.getPlayer().displayName(), players.size(), arena.getMaximumPlayerCount());
    }

    public void sendMessage(Message message, Object... arguments) {
        for (TeamPlayer player : players) {
            languageManager.sendMessage(message, player.getPlayer(), arguments);
        }
    }

    public void playSound(Sound sound) {
        players.forEach(player -> player.playSound(sound));
    }

    private void startCountdown() {
        countdown = new GameCountdown(plugin, this);
        countdown.start();
    }

    public void startGame() {
        gameState = GameState.PLAYING;

        eventListener.register();

        tickHandler = new GameTickHandler(plugin, this);
        tickHandler.start();

        try {
            chestManager.refillChests(1.0D);
        } catch (Exception e) {
            e.printStackTrace();
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
        players.forEach(teamPlayer -> teamPlayer.getPlayer().getInventory().clear());

        for (TeamPlayer teamPlayer : getPlayers()) {
            Player player = teamPlayer.getPlayer();
            Title title;

            if (teamPlayer.getTeam().equals(winningTeam)) {
                title = title(languageManager.getMessage(Message.YOU_WIN, player), Component.empty());
                teamPlayer.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            } else {
                title = title(languageManager.getMessage(Message.YOU_LOSE, player), Component.empty());
            }

            player.sendTitlePart(TitlePart.TITLE, title.title());
        }

        plugin.runTaskLater(this::endGame, 20 * 10L);
    }

    public void endGame() {
        eventListener.unregister();
        tickHandler.cancel();

        players.forEach(TeamPlayer::teleportToLobby);
        players.forEach(player -> player.unregister(false));

        teams.clear();
        players.clear();

        gameState = GameState.RESETTING;
        plugin.runTaskLater(this::resetMap, 1L);
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

    public void supplyDrop() {
        Location location = arena.getSupplyDropData().getRandomLocation().getBukkitLocation();
        StorageMinecart chestMinecart = (StorageMinecart) location.getWorld().spawnEntity(location, EntityType.CHEST_MINECART);

        chestMinecart.customName(Component.text("Supply Drop"));

        chestMinecart.getInventory().setItem(
            Util.randomInt(0, chestMinecart.getInventory().getSize() - 1),
            customItemManager.getItem("rocket_launcher").getItem(null));

        sendMessage(Message.EVENT_SUPPLY_DROP, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public void startEndgame() {
        sendMessage(Message.EVENT_ENDGAME);
        playSound(Sound.ENTITY_ENDER_DRAGON_GROWL);

        chestManager.refillChests(1.5D);
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
