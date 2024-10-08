package org.esoteric_organisation.firework_wars_plugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.registry.event.RegistryEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.esoteric_organisation.firework_wars_plugin.items.manager.CustomItemManager;
import org.esoteric_organisation.firework_wars_plugin.util.ReflectUtil;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class FireworkWarsPluginBootstrapper implements PluginBootstrap {
    private final ReflectUtil reflectUtil = new ReflectUtil();
    private final CustomItemManager customItemManager = new CustomItemManager(reflectUtil);

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        reflectUtil.useLogger(context.getLogger());

        context.getLifecycleManager().registerEventHandler(RegistryEvents.GAME_EVENT.freeze(), event -> {
            customItemManager.registerNMSItems();
        });
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new FireworkWarsPlugin(customItemManager);
    }
}
