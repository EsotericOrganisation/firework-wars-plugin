package net.slqmy.template_paper_plugin.language;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.template_paper_plugin.TemplatePaperPlugin;
import net.slqmy.template_paper_plugin.data.player.PlayerProfile;
import net.slqmy.template_paper_plugin.util.FileUtil;

import java.util.UUID;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class LanguageManager {

  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  private final TemplatePaperPlugin plugin;

  private final String languagesFolderName = "languages";
  private final String languagesFolderPath;
  private final File languagesFolder;

  private final String defaultLanguage;

  private final Map<String, Map<Message, String>> languages = new HashMap<>();

  public String getDefaultLanguage() {
    return defaultLanguage;
  }

  public Set<String> getLanguages() {
    return languages.keySet();
  }

  public LanguageManager(TemplatePaperPlugin plugin) {
    this.plugin = plugin;

    File dataFolder = plugin.getDataFolder();
    languagesFolderPath = dataFolder.getPath() + File.separator + languagesFolderName;
    languagesFolder = new File(languagesFolderPath);

    saveLanguageFiles();
    loadLanguageMessages();

    defaultLanguage = plugin.getConfig().getString("default-language");
  }

  private void saveLanguageFiles() {
    String languagesResourceFolderName = languagesFolderName + "/";
    List<String> languageResourceFileNames = FileUtil.getResourceFolderResourceFileNames(languagesResourceFolderName);

    languageResourceFileNames.forEach((fileName) -> plugin.saveResource(languagesFolderName + File.separator + fileName, false));
  }

  private void loadLanguageMessages() {
    for (File languageMessagesFile : languagesFolder.listFiles()) {
      String languageName = languageMessagesFile.getName().split("\\.", 2)[0];

      String languageMessagesResourcePath = languagesFolderName + File.separator + languageName + ".yaml";
      plugin.saveResource(languageMessagesResourcePath, false);

      YamlConfiguration messagesConfiguration = YamlConfiguration.loadConfiguration(languageMessagesFile);
      Map<Message, String> messages = new HashMap<>();

      for (Message message : Message.values()) {
        String mappedResult = messagesConfiguration.getString(message.name());

        if (mappedResult != null) {
          messages.put(message, mappedResult);
        }
      }

      languages.put(languageName, messages);
    }
  }

  public String getLanguage(CommandSender commandSender) {
    String language = getProfileLanguage(commandSender);

    if (language == null) {
      language = getLocale(commandSender);
    }

    return language;
  }

  public String getLanguage(UUID uuid) {
    String language = getProfileLanguage(uuid);

    if (language == null) {
      language = getLocale(uuid);
    }

    return language;
  }

  public String getLanguage(PlayerProfile profile) {
    return getLanguage(profile.getUuid());
  }

  public void setLanguage(PlayerProfile profile, String language) {
    profile.setLanguage(language);
  }

  public void setLanguage(UUID uuid, String language) {
    setLanguage(plugin.getPlayerDataManager().getPlayerProfile(uuid), language);
  }

  public void setLanguage(Player player, String language) {
    setLanguage(player.getUniqueId(), language);
  }

  public String getRawMessageString(Message message, String language, boolean fallbackOnDefaultLanguage) {
    Map<Message, String> languageMessageMap = languages.get(language);
    String miniMessageString = languageMessageMap.get(message);

    if (miniMessageString == null) {
      return fallbackOnDefaultLanguage ? getRawMessageString(message, defaultLanguage, false) : null;
    }

    return miniMessageString;
  }

  public String getRawMessageString(Message message, String language) {
    return getRawMessageString(message, language, true);
  }

  public String getFormattedMessageString(Message message, String language, boolean fallbackOnDefaultLanguage, Object... arguments) {
    return String.format(getRawMessageString(message, language, fallbackOnDefaultLanguage), arguments);
  }

  public String getFormattedMessageString(Message message, String language, Object... arguments) {
    return getFormattedMessageString(message, language, true, arguments);
  }

  public Component getMessage(Message message, String language, boolean fallbackOnDefaultLanguage, Object... arguments) {
    return miniMessage.deserialize(getFormattedMessageString(message, language, fallbackOnDefaultLanguage, arguments));
  }

  public Component getMessage(Message message, String language, Object... arguments) {
    return getMessage(message, language, true, arguments);
  }

  public Component getMessage(Message message, CommandSender commandSender, boolean fallbackOnDefaultLanguage, Object... arguments) {
    return getMessage(message, getLanguage(commandSender), fallbackOnDefaultLanguage, arguments);
  }

  public Component getMessage(Message message, CommandSender commandSender, Object... arguments) {
    return getMessage(message, commandSender, true, arguments);
  }

  public Component getMessage(Message message, UUID uuid, boolean fallbackOnDefaultLanguage, Object... arguments) {
    return getMessage(message, getLanguage(uuid), fallbackOnDefaultLanguage, arguments);
  }

  public Component getMessage(Message message, UUID uuid, Object... arguments) {
    return getMessage(message, uuid, true, arguments);
  }

  private String getLocale(CommandSender commandSender) {
    if (!(commandSender instanceof Player player)) {
      return defaultLanguage;
    }

    Locale playerLocale = player.locale();
    String localeDisplayName = playerLocale.getDisplayName();

    if (!getLanguages().contains(localeDisplayName)) {
      return defaultLanguage;
    }

    return localeDisplayName;
  }

  private String getLocale(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    return getLocale(player);
  }

  private String getLocale(PlayerProfile profile) {
    return getLocale(profile.getUuid());
  }

  private String getProfileLanguage(PlayerProfile profile) {
    if (profile == null) {
      return null;
    }

    return profile.getLanguage();
  }

  private String getProfileLanguage(UUID uuid) {
    return getProfileLanguage(plugin.getPlayerDataManager().getPlayerProfile(uuid));
  }

  private String getProfileLanguage(CommandSender commandSender) {
    if (commandSender == null) {
      return null;
    } else if (commandSender instanceof Player player) {
      return getProfileLanguage(player.getUniqueId());
    } else {
      return defaultLanguage;
    }
  }
}
