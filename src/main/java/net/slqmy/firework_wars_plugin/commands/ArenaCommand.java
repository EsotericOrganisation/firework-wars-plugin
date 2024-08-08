package net.slqmy.firework_wars_plugin.commands;

import java.util.ArrayList;
import java.util.List;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.arena.Arena;
import net.slqmy.firework_wars_plugin.arena.ArenaInformation;
import net.slqmy.firework_wars_plugin.arena.ArenaManager;
import net.slqmy.firework_wars_plugin.game.FireworkWarsGame;
import net.slqmy.firework_wars_plugin.game.GameManager;

public class ArenaCommand extends CommandAPICommand {

  public ArenaCommand(FireworkWarsPlugin plugin) {
    super("arena");

    ArenaManager arenaManager = plugin.getArenaManager();
    ArenaInformation arenaInformation = arenaManager.getArenaInformation();
    Arena[] arenas = arenaInformation.getArenas();

    GameManager gameManager = plugin.getGameManager();

    String arenaNumberNodeName = "arena-number";

    withSubcommand(
      new CommandAPICommand("join")
        .withArguments(new IntegerArgument(arenaNumberNodeName).includeSuggestions(ArgumentSuggestions.strings((suggestionsInfo) -> {
          List<String> suggestions = new ArrayList<>();

          for (int i = 1; i <= arenas.length; i++) {
            suggestions.add(String.valueOf(i));
          }

          return suggestions.toArray(String[]::new);
        })))
        .executesPlayer((info) -> {
          int arenaNumber = (int) info.args().get(arenaNumberNodeName);

          Arena arena = arenas[arenaNumber - 1];
          FireworkWarsGame game = gameManager.getFireworkWarsGame(arena);
          game.addPlayer(info.sender());
        })
    );

    register(plugin);
  }
}
