package org.esoteric.minecraft.plugins.fireworkwars.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.arena.json.structure.Arena;
import org.esoteric.minecraft.plugins.fireworkwars.arena.manager.ArenaManager;
import org.esoteric.minecraft.plugins.fireworkwars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.fireworkwars.game.GameManager;
import org.esoteric.minecraft.plugins.fireworkwars.language.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ArenaCommand extends CommandAPICommand {

    public ArenaCommand(FireworkWarsPlugin plugin) {
        super("arena");

        ArenaManager arenaManager = plugin.getArenaManager();
        List<Arena> arenas = arenaManager.getArenas();

        GameManager gameManager = plugin.getGameManager();

        String arenaNumberNodeName = "arena-number";

        withSubcommand(
                new CommandAPICommand("join")
                        .withArguments(new IntegerArgument(arenaNumberNodeName).includeSuggestions(ArgumentSuggestions.strings((suggestionsInfo) -> {
                            List<String> suggestions = new ArrayList<>();

                            Predicate<Arena> hasOngoingGame = gameManager::hasOngoingGame;
                            Function<Integer, Integer> increment = i -> ++i;

                            arenas.stream()
                                    .filter(hasOngoingGame.negate())
                                    .map(arenas::indexOf).map(increment)
                                    .map(String::valueOf)
                                    .forEach(suggestions::add);

                            return suggestions.toArray(String[]::new);
                        })))
                        .executesPlayer((info) -> {
                            if (!arenaManager.isLobby(info.sender().getWorld())) {
                                plugin.getLanguageManager().sendMessage(Message.NOT_IN_LOBBY, info.sender());
                                return;
                            }

                            Integer arenaNumber = (Integer) info.args().get(arenaNumberNodeName);

                            if (arenaNumber == null || arenaNumber <= 0 || arenaNumber - 1 >= arenas.size()) {
                                plugin.getLanguageManager().sendMessage(Message.INVALID_ARENA, info.sender());
                                return;
                            }

                            Arena arena = arenas.get(arenaNumber - 1);
                            FireworkWarsGame game = gameManager.getFireworkWarsGame(arena);

                            if (game.isPlaying()) {
                                plugin.getLanguageManager().sendMessage(Message.GAME_ALREADY_PLAYING, info.sender());
                                return;
                            }

                            if (game.containsPlayer(info.sender())) {
                                plugin.getLanguageManager().sendMessage(Message.GAME_ALREADY_CONTAINS_PLAYER, info.sender());
                                return;
                            }

                            if (game.isResetting()) {
                                plugin.getLanguageManager().sendMessage(Message.GAME_RELOADING, info.sender());
                                return;
                            }

                            game.addPlayer(info.sender());
                        })
        );

        register(plugin);
    }
}
