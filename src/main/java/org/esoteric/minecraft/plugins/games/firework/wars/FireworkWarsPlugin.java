package org.esoteric.minecraft.plugins.games.firework.wars;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.esoteric.minecraft.plugins.games.firework.wars.arena.manager.ArenaManager;
import org.esoteric.minecraft.plugins.games.firework.wars.commands.*;
import org.esoteric.minecraft.plugins.games.firework.wars.event.listeners.global.ItemOwnerChangeListener;
import org.esoteric.minecraft.plugins.games.firework.wars.event.listeners.global.PlayerLoseHungerListener;
import org.esoteric.minecraft.plugins.games.firework.wars.file.FileManager;
import org.esoteric.minecraft.plugins.games.firework.wars.game.FireworkWarsGame;
import org.esoteric.minecraft.plugins.games.firework.wars.game.GameManager;
import org.esoteric.minecraft.plugins.games.firework.wars.items.CustomItemManager;
import org.esoteric.minecraft.plugins.games.firework.wars.language.LanguageManager;
import org.esoteric.minecraft.plugins.games.firework.wars.managers.PlayerVelocityManager;
import org.esoteric.minecraft.plugins.games.firework.wars.profile.PlayerDataManager;
import org.esoteric.minecraft.plugins.games.firework.wars.util.PersistentDataManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class FireworkWarsPlugin extends JavaPlugin implements Listener {
    private static FireworkWarsPlugin instance;
    private static Logger logger;

    private final boolean isFirstRun;

    private final FileManager fileManager;
    private final Path mapsDirectory = Paths.get("plugins/FireworkWarsPlugin/maps");
    private final Path rootDirectory = Paths.get("").toAbsolutePath();

    private final CustomItemManager customItemManager;
    private PlayerDataManager playerDataManager;
    private LanguageManager languageManager;
    private ArenaManager arenaManager;
    private GameManager gameManager;
    private PersistentDataManager pdcManager;
    private PlayerVelocityManager playerVelocityManager;

    private ResetInventoryCommand resetInventoryCommand;
    private HealCommand healCommand;

    public static FireworkWarsPlugin getInstance() {
        return instance;
    }

    public static Logger logger() {
        return logger;
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public ArenaManager getArenaManager() {
        return this.arenaManager;
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    public CustomItemManager getCustomItemManager() {
        return this.customItemManager;
    }

    public PersistentDataManager getPdcManager() {
        return this.pdcManager;
    }

    public PlayerVelocityManager getPlayerVelocityManager() {
        return this.playerVelocityManager;
    }

    public ResetInventoryCommand getResetInventoryCommand() {
        return this.resetInventoryCommand;
    }

    public HealCommand getHealCommand() {
        return this.healCommand;
    }

    public FireworkWarsPlugin(CustomItemManager customItemManager) {
        FireworkWarsPlugin.instance = this;
        FireworkWarsPlugin.logger = getLogger();

        isFirstRun = !getDataFolder().exists();

        if (isFirstRun) {
            getLogger().info("Starting plugin for the first time.");
        } else {
            getLogger().info("Starting plugin.");
        }

        if (!isFirstRun && Files.exists(Paths.get("maps/world"))) {
            throw new RuntimeException("'world' folder already exists on first run! Please delete the folder and run the plugin again!");
        }

        this.customItemManager = customItemManager;
        this.fileManager = new FileManager(this);

        try {
            saveMaps();
            moveMapsToRoot();
        } catch (IOException exception) {
            getLogger().severe(exception.getMessage() + Arrays.toString(exception.getStackTrace()));
        }
    }

    private void saveMaps() throws IOException {
        if (new File("barracks").exists()) {
            getLogger().info("Barracks map already exists, skipping saving procedure.");
        } else {
            fileManager.saveResourceFileFolder("maps/barracks");
            saveResource("maps/barracks/level.dat", true);
        }

        if (new File("world").exists()) {
            getLogger().info("Lobby already exists, skipping saving procedure.");
        } else {
            fileManager.saveResourceFileFolder("maps/world");
            saveResource("maps/world/level.dat", true);
        }
    }

    private void moveMapsToRoot() throws IOException {
        moveFolderToRoot(mapsDirectory);
    }

    private void moveFolderToRoot(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relativePath = mapsDirectory.relativize(file);
                    Path targetPath = rootDirectory.resolve(relativePath);

                    Files.createDirectories(targetPath.getParent());
                    Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });

            getLogger().info("All files moved successfully!");
        } else {
            getLogger().info("Directory does not exist.");
        }
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onEnable() {
        getDataFolder().mkdir();
        saveDefaultConfig();

        CommandAPIBukkitConfig commandAPIConfig = new CommandAPIBukkitConfig(this);

        CommandAPI.onLoad(commandAPIConfig);
        CommandAPI.onEnable();

        this.playerDataManager = new PlayerDataManager(this);
        this.languageManager = new LanguageManager(this);
        this.arenaManager = new ArenaManager(this);
        this.gameManager = new GameManager(this);
        this.pdcManager = new PersistentDataManager();
        this.playerVelocityManager = new PlayerVelocityManager(this);

        customItemManager.setPlugin(this);
        customItemManager.registerCustomItems();

        new SetLanguageCommand(this);
        new ArenaCommand(this);
        new GiveItemCommand(this);
        this.resetInventoryCommand = new ResetInventoryCommand(this);
        this.healCommand = new HealCommand(this);

        new ItemOwnerChangeListener(this).register();
        new PlayerLoseHungerListener(this).register();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.save();
        }
    }

    public BukkitTask runTaskLater(Runnable runnable, long delay) {
        return getServer().getScheduler().runTaskLater(this, runnable, delay);
    }

    public void runTaskTimer(Runnable runnable, long delay, long period) {
        getServer().getScheduler().runTaskTimer(this, runnable, delay, period);
    }

    public void logLoudly(String message) {
        this.getServer().broadcast(Component.text(message));
    }

    @EventHandler
    public void onDamage(@NotNull EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
           return;
        }

        FireworkWarsGame game = gameManager.getFireworkWarsGame(player);

        if (game == null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Player player = event.getPlayer();

        FireworkWarsGame game = gameManager.getFireworkWarsGame(player);

        if (game == null) {
            event.setCancelled(true);
        }
    }
}
