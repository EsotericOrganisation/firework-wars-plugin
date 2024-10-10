package org.esoteric_organisation.firework_wars_plugin.file;

import java.io.File;
import java.io.IOException;

import org.esoteric_organisation.firework_wars_plugin.FireworkWarsPlugin;

public class FileManager {

    private final FireworkWarsPlugin plugin;

    public FileManager(FireworkWarsPlugin plugin) {
        this.plugin = plugin;
    }

    public File saveResourceFileFolder(String resourceFileFolderPath, boolean shouldReplaceExistingFiles) {
        try {
            FileUtil.getResourceFileFolderResourceFilePathsRecursively(resourceFileFolderPath).forEach((resourceFilePath) -> {
                plugin.saveResource(resourceFilePath, shouldReplaceExistingFiles);
            });
            return new File(plugin.getDataPath() + File.separator + resourceFileFolderPath);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public File saveResourceFileFolder(String resourceFileFolderPath) {
        return saveResourceFileFolder(resourceFileFolderPath, true);
    }
}