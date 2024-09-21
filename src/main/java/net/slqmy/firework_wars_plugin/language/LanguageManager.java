package net.slqmy.firework_wars_plugin.language;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;
import net.slqmy.firework_wars_plugin.data.player.PlayerProfile;
import net.slqmy.firework_wars_plugin.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LanguageManager {

  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  private final FireworkWarsPlugin plugin;

  private final Pattern placeholderPattern = Pattern.compile("\\{(\\d+)}");

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

  public LanguageManager(FireworkWarsPlugin plugin) {
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
      plugin.saveResource(languageMessagesResourcePath, true);

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

  public String getLocale(CommandSender commandSender) {
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

  public String getLocale(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    return getLocale(player);
  }

  public String getLocale(PlayerProfile profile) {
    return getLocale(profile.getUuid());
  }

  public String getProfileLanguage(PlayerProfile profile) {
    if (profile == null) {
      return null;
    }

    return profile.getLanguage();
  }

  public String getProfileLanguage(UUID uuid) {
    return getProfileLanguage(plugin.getPlayerDataManager().getPlayerProfile(uuid));
  }

  public String getProfileLanguage(CommandSender commandSender) {
    if (commandSender == null) {
      return null;
    } else if (commandSender instanceof Player player) {
      return getProfileLanguage(player.getUniqueId());
    } else {
      return defaultLanguage;
    }
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

  public Component getMessage(Message message, String language, boolean fallbackOnDefaultLanguage, Component... arguments) {
    String miniMessageString = getRawMessageString(message, language, fallbackOnDefaultLanguage);    

    Matcher matcher = placeholderPattern.matcher(miniMessageString);

    String[] parts = placeholderPattern.split(miniMessageString);
    List<Integer> argumentIndexes = new ArrayList<>();

    while (matcher.find()) {
      argumentIndexes.add(Integer.parseUnsignedInt(matcher.group(1)));
    }

    Component output = miniMessage.deserialize(parts[0]);

    for (int i = 1; i < parts.length; i++) {
      int argumentIndex = argumentIndexes.get(i - 1);
      output = output.append(arguments[argumentIndex]);

      String part = parts[i];
      Component component = miniMessage.deserialize(part);
      output = output.append(component);
    }

    return output;
  }

  public Component getMessage(Message message, String language, Component... arguments) {
    return getMessage(message, language, true, arguments);
  }

  public Component getMessage(Message message, String language, boolean fallbackOnDefaultLanguage, Object... arguments) {
    return getMessage(message, language, fallbackOnDefaultLanguage, toComponents(arguments));
  }

  public Component getMessage(Message message, String language, Object... arguments) {
    return getMessage(message, language, true, arguments);
  }

  public Component getMessage(Message message, CommandSender commandSender, boolean fallbackOnDefaultLanguage, Component... arguments) {
    return getMessage(message, getLanguage(commandSender), fallbackOnDefaultLanguage, arguments);
  }

  public Component getMessage(Message message, CommandSender commandSender, Component... arguments) {
    return getMessage(message, commandSender, true, arguments);
  }

  public Component getMessage(Message message, CommandSender commandSender, boolean fallbackOnDefaultLanguage, Object... arguments) {
    return getMessage(message, commandSender, fallbackOnDefaultLanguage, toComponents(arguments));
  }

  public Component getMessage(Message message, CommandSender commandSender, Object... arguments) {
    return getMessage(message, commandSender, true, arguments);
  }

  public Component getMessage(Message message, UUID uuid, boolean fallbackOnDefaultLanguage, Component... arguments) {
    return getMessage(message, getLanguage(uuid), fallbackOnDefaultLanguage, arguments);
  }

  public Component getMessage(Message message, UUID uuid, Component... arguments) {
    return getMessage(message, uuid, true, arguments);
  }

  public Component getMessage(Message message, UUID uuid, boolean fallbackOnDefaultLanguage, Object... arguments) {
    return getMessage(message, uuid, fallbackOnDefaultLanguage, toComponents(arguments));
  }

  public Component getMessage(Message message, UUID uuid, Object... arguments) {
    return getMessage(message, uuid, true, arguments);
  }

  public Component getMessage(Message message, PlayerProfile playerProfile, boolean fallbackOnDefaultLanguage, Component... arguments) {
    return getMessage(message, getLanguage(playerProfile), fallbackOnDefaultLanguage, arguments);
  }

  public Component getMessage(Message message, PlayerProfile playerProfile, Component... arguments) {
    return getMessage(message, playerProfile, true, arguments);
  }

  public Component getMessage(Message message, PlayerProfile playerProfile, boolean fallbackOnDefaultLanguage, Object... arguments) {
    return getMessage(message, playerProfile, fallbackOnDefaultLanguage, toComponents(arguments));
  }

  public Component getMessage(Message message, PlayerProfile playerProfile, Object... arguments) {
    return getMessage(message, playerProfile, true, arguments);
  }

  // rolyPolyVole start

  public Component[] getMessages(Message message, CommandSender commandSender) {
    return Arrays
        .stream(getRawMessageString(message, getLanguage(commandSender), true)
        .split("\n"))
        .map(miniMessage::deserialize)
        .toArray(Component[]::new);
  }

  // rolyPolyVole end

  public void sendMessage(Message message, CommandSender commandSender, boolean fallbackOnDefaultLanguage, Component... arguments) {
    commandSender.sendMessage(getMessage(message, getLanguage(commandSender), fallbackOnDefaultLanguage, arguments));
  }

  public void sendMessage(Message message, CommandSender commandSender, Component... arguments) {
    commandSender.sendMessage(getMessage(message, commandSender, true, arguments));
  }

  public void sendMessage(Message message, CommandSender commandSender, boolean fallbackOnDefaultLanguage, Object... arguments) {
    commandSender.sendMessage(getMessage(message, commandSender, fallbackOnDefaultLanguage, toComponents(arguments)));
  }

  public void sendMessage(Message message, CommandSender commandSender, Object... arguments) {
    commandSender.sendMessage(getMessage(message, commandSender, true, arguments));
  }

  public void sendMessage(Message message, UUID uuid, boolean fallbackOnDefaultLanguage, Component... arguments) {
    Bukkit.getPlayer(uuid).sendMessage(getMessage(message, getLanguage(uuid), fallbackOnDefaultLanguage, arguments));
  }

  public void sendMessage(Message message, UUID uuid, Component... arguments) {
    Bukkit.getPlayer(uuid).sendMessage(getMessage(message, uuid, true, arguments));
  }

  public void sendMessage(Message message, UUID uuid, boolean fallbackOnDefaultLanguage, Object... arguments) {
    Bukkit.getPlayer(uuid).sendMessage(getMessage(message, uuid, fallbackOnDefaultLanguage, toComponents(arguments)));
  }

  public void sendMessage(Message message, UUID uuid, Object... arguments) {
    Bukkit.getPlayer(uuid).sendMessage(getMessage(message, uuid, true, arguments));
  }

  public void sendMessage(Message message, PlayerProfile playerProfile, boolean fallbackOnDefaultLanguage, Component... arguments) {
    Bukkit.getPlayer(playerProfile.getUuid()).sendMessage(getMessage(message, getLanguage(playerProfile), fallbackOnDefaultLanguage, arguments));
  }

  public void sendMessage(Message message, PlayerProfile playerProfile, Component... arguments) {
    Bukkit.getPlayer(playerProfile.getUuid()).sendMessage(getMessage(message, playerProfile, true, arguments));
  }

  public void sendMessage(Message message, PlayerProfile playerProfile, boolean fallbackOnDefaultLanguage, Object... arguments) {
    Bukkit.getPlayer(playerProfile.getUuid()).sendMessage(getMessage(message, playerProfile, fallbackOnDefaultLanguage, toComponents(arguments)));
  }

  public void sendMessage(Message message, PlayerProfile playerProfile, Object... arguments) {
    Bukkit.getPlayer(playerProfile.getUuid()).sendMessage(getMessage(message, playerProfile, true, arguments));
  }

  public Component[] toComponents(Object ...objects) {
    return Stream.of(objects).map((object) -> toComponent(object)).toArray(Component[]::new);
  }

  public Component toComponent(Object object) {
    if (object instanceof Component component) {
      return component;
    }

    return Component.text(String.valueOf(object));
  }
}
