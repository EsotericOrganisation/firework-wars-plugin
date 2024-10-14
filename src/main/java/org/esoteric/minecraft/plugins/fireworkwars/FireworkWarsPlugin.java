package org.esoteric.minecraft.plugins.fireworkwars;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.esoteric.minecraft.plugins.fireworkwars.arena.manager.ArenaManager;
import org.esoteric.minecraft.plugins.fireworkwars.commands.*;
import org.esoteric.minecraft.plugins.fireworkwars.events.global.ItemOwnerChangeListener;
import org.esoteric.minecraft.plugins.fireworkwars.events.global.PlayerLoseHungerListener;
import org.esoteric.minecraft.plugins.fireworkwars.file.FileManager;
import org.esoteric.minecraft.plugins.fireworkwars.game.GameManager;
import org.esoteric.minecraft.plugins.fireworkwars.items.CustomItemManager;
import org.esoteric.minecraft.plugins.fireworkwars.language.LanguageManager;
import org.esoteric.minecraft.plugins.fireworkwars.manager.PlayerVelocityManager;
import org.esoteric.minecraft.plugins.fireworkwars.profile.PlayerDataManager;
import org.esoteric.minecraft.plugins.fireworkwars.util.PersistentDataManager;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class FireworkWarsPlugin extends JavaPlugin {
    private static FireworkWarsPlugin instance;
    private static Logger logger;

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
        fileManager.saveResourceFileFolder("maps/barracks");
        saveResource("maps/barracks/level.dat", true);
    }

    private void moveMapsToRoot() throws IOException {
        if (Files.exists(mapsDirectory)) {
            Files.walkFileTree(mapsDirectory, new SimpleFileVisitor<>() {
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

            getLogger().info("All maps moved successfully!");
        } else {
            getLogger().info("Maps directory does not exist.");
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
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.save();
        }
    }

    public void runTaskLater(Runnable runnable, long delay) {
        getServer().getScheduler().runTaskLater(this, runnable, delay);
    }

    public void runTaskTimer(Runnable runnable, long delay, long period) {
        getServer().getScheduler().runTaskTimer(this, runnable, delay, period);
    }

    public void logLoudly(String message) {
        this.getServer().broadcast(Component.text(message));
    }
}
