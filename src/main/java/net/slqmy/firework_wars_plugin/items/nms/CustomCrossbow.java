package net.slqmy.firework_wars_plugin.items.nms;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.slqmy.firework_wars_plugin.items.manager.AbstractItem;

import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
public class CustomCrossbow extends CrossbowItem {
    private final AbstractItem ammoItem;

    public CustomCrossbow(AbstractItem ammoItem) {
        super(new Item.Properties()
            .stacksTo(1)
            .durability(465)
            .component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY));

        this.ammoItem = ammoItem;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (item) -> ammoItem.isValidCustomItem(item.asBukkitCopy());
    }

    @Override
    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return getAllSupportedProjectiles();
    }
}
