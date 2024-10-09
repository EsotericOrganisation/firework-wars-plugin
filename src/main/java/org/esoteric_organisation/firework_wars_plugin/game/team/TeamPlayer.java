package org.esoteric_organisation.firework_wars_plugin.game.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;
import org.esoteric_organisation.firework_wars_plugin.game.FireworkWarsGame;
import org.esoteric_organisation.firework_wars_plugin.language.LanguageManager;
import org.esoteric_organisation.firework_wars_plugin.language.Message;
import org.esoteric_organisation.firework_wars_plugin.scoreboard.api.FastBoard;
import org.esoteric_organisation.firework_wars_plugin.scoreboard.wrapper.FireworkWarsScoreboard;
import org.esoteric_organisation.firework_wars_plugin.util.Pair;

import java.util.*;

public class TeamPlayer {
    private final static Map<UUID, TeamPlayer> activePlayers = new HashMap<>();

    private final UUID uuid;
    private final FireworkWarsGame game;

    private final FireworkWarsPlugin plugin;
    private final LanguageManager languageManager;

    private FireworkWarsTeam team;
    private FireworkWarsScoreboard scoreboard;

    private int kills;
    private int damage;

    public static TeamPlayer from(UUID uuid) {
        return activePlayers.get(uuid);
    }

    public static TeamPlayer from(Player player) {
        return from(player.getUniqueId());
    }

    public FireworkWarsScoreboard getScoreboard() {
        return scoreboard;
    }

    public TeamPlayer(UUID uuid, FireworkWarsGame game) {
        this.uuid = uuid;
        this.game = game;

        this.plugin = game.getPlugin();
        this.languageManager = plugin.getLanguageManager();

        register();
    }

    private void register() {
        activePlayers.forEach((uuid, player) -> {
            if (uuid.equals(this.uuid)) {
                player.unregister(true);
            }
        });

        activePlayers.put(uuid, this);
    }

    public void unregister(boolean removeFromGame) {
        activePlayers.remove(uuid);

        scoreboard.delete();

        if (removeFromGame) {
            game.getPlayers().remove(this);
        }
    }

    public void joinTeam(FireworkWarsTeam team) {
        team.addPlayer(this);
        this.team = team;

        Player player = getPlayer();
        player.teleport(team.getTeamData().getSpawnLocation().getBukkitLocation());

        player.sendTitlePart(TitlePart.TITLE, plugin.getLanguageManager().getMessage(Message.YOU_ARE_ON_TEAM, player));
        player.sendTitlePart(TitlePart.SUBTITLE, team.getColoredTeamName());
    }

    public void showScoreboard() {
        Player player = getPlayer();
        FastBoard board = new FastBoard(player);

        List<Component> lines = new ArrayList<>(List.of(
            languageManager.getMessage(Message.SB_SEPARATOR, player),
            languageManager.getMessage(Message.SB_EVENT_SUPPLY_DROP, player, "%"),
            Component.empty(),
            Component.empty(),
            languageManager.getMessage(Message.SB_KILL_COUNT, player, "%"),
            languageManager.getMessage(Message.SB_DAMAGE_DEALT, player, "%"),
            languageManager.getMessage(Message.SB_SEPARATOR, player)
        ));

        List<Component> teamList = game.getTeams().stream()
            .map(team -> {
                boolean isOwnTeam = team.equals(this.team);

                if (isOwnTeam) {
                    return languageManager.getMessage(
                        Message.SB_TEAM, player, team.getColoredTeamName(), team.getPlayers().size());
                } else {
                    return languageManager.getMessage(
                        Message.SB_OWN_TEAM, player, team.getColoredTeamName(), team.getPlayers().size());
                }
            })
            .toList();

        lines.addAll(3, teamList);

        board.updateTitle(languageManager.getMessage(Message.SB_TITLE, player));
        board.updateLines(lines);

        this.scoreboard = new FireworkWarsScoreboard(board)
            .updateLine(4, Pair.of("%", kills + ""))
            .updateLine(5, Pair.of("%", damage + ""));
        scoreboard.update();
    }

    public FireworkWarsTeam getTeam() {
        return team;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getOfflinePlayer(uuid).getPlayer();
    }

    public Component getColoredName() {
        return getPlayer().displayName().color(team.getTeamColor());
    }

    public void sendMessage(Component message) {
        getPlayer().sendMessage(message);
    }

    public void teleportToWaitingArea() {
        getPlayer().teleport(game.getArena().getWaitingAreaLocation().getBukkitLocation());
    }

    public void teleportToLobby() {
        Location location = plugin.getArenaManager().getLobbies().get(0).getSpawnLocation().getBukkitLocation();
        getPlayer().teleport(location);
    }

    public void becomeSpectator() {
        getPlayer().setGameMode(GameMode.SPECTATOR);
    }

    public void incrementKills() {
        getScoreboard()
            .updateLine(4, Pair.of("%", ++this.kills + ""))
            .update();
    }

    public void addDamage(int damage) {
        this.damage += damage;
        getScoreboard()
            .updateLine(5, Pair.of("%", this.damage + ""))
            .update();
    }
}
