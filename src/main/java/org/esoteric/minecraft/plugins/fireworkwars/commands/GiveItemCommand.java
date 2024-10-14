package org.esoteric.minecraft.plugins.fireworkwars.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.ServerOperator;
import org.esoteric.minecraft.plugins.fireworkwars.FireworkWarsPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.items.AbstractItem;
import org.esoteric.minecraft.plugins.fireworkwars.util.Util;

public class GiveItemCommand extends CommandAPICommand {
    private final FireworkWarsPlugin plugin;

    private final String itemNodeName = "item";
    private final String amountNodeName = "amount";

    public GiveItemCommand(FireworkWarsPlugin plugin) {
        super("giveItem");
        this.plugin = plugin;

        setRequirements(ServerOperator::isOp);
        withArguments(this.getItemArgument(), this.getAmountArgument());
        executesPlayer(this::onPlayerExecute);

        register(plugin);
    }

    private Argument<String> getItemArgument() {
        return new StringArgument(itemNodeName)
            .includeSuggestions((ArgumentSuggestions.strings((suggestionsInfo) ->
                plugin.getCustomItemManager()
                    .getItemRegistry()
                    .keySet()
                    .toArray(String[]::new)
            )));
    }

    private Argument<Integer> getAmountArgument() {
        return new IntegerArgument(amountNodeName)
            .includeSuggestions((ArgumentSuggestions.strings((suggestionsInfo) ->
                Util.orderedNumberList(1, 64).stream()
                    .map(Object::toString)
                    .toArray(String[]::new)
            )));
    }

    private void onPlayerExecute(Player player, CommandArguments args) {
        AbstractItem<? extends ItemMeta> item = plugin.getCustomItemManager()
            .getItem((String) args.get(itemNodeName));

        player.getInventory().addItem(
            item.getItem(player, (Integer) args.getOrDefault(amountNodeName, 1)));
    }
}
