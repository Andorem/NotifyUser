package com.minuxe.notifyuser;

import java.io.File;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NotifyUser extends JavaPlugin {

   public static File DATA_FOLDER;
   Logger log = getLogger();
   FileConfiguration commandConfig = YamlConfiguration.loadConfiguration(getTextResource("commands.yml"));

   Notification notification;
   ChatListener chatListener;
   ConfigurationHandler configH;

   boolean sendPingNotification;
   boolean isAnonymous;
   // boolean debugEnabled = false;

   String notificationType;
   String[] placeholders = { "sender", "receiver", "min", "toggle" };
   String noPermission;
   // String noPermission = ChatColor.DARK_RED + "You don't have permission to
   // perform this command.";
   String chatHeader = ChatColor.AQUA + "\n======[" + ChatColor.WHITE + getName() + ChatColor.AQUA + "]======";
   // String generalCommands = getHelpCommands(commandConfig, "general");
   // String adminCommands = getHelpCommands(commandConfig, "admin");

   @Override
   public void onEnable() {
      DATA_FOLDER = getDataFolder();

       try { if(!DATA_FOLDER.exists()) { DATA_FOLDER.mkdir();} }
       catch (Exception e) { e.printStackTrace();}

      notification = new Notification(this);
      chatListener = new ChatListener(notification, this);
      configH = new ConfigurationHandler(this, DATA_FOLDER);

      configH.setupConfig();
      setValues();

      // loadFromConfig();

      // debugEnabled = getConfig().getBoolean("debug");
      // debug("==========================================================");
      // debug("Debug mode activated. Now reporting to console.");
      // debug("To disable debugging, set 'debug' in 'config.yml' to false.");
      // debug("==========================================================");

      getServer().getPluginManager().registerEvents(chatListener, this);
      // debug("Chat Listener enabled.");

   }

   @Override
   public void onDisable() {

   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

      boolean senderIsPlayer = (sender instanceof Player);
      Player pSender = (senderIsPlayer ? (Player) sender : null);
      String senderName = sender.getName();

      if (cmd.getName().equalsIgnoreCase("nu")) {
         if ((args.length < 1) || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            sender.sendMessage(chatHeader);
            if (sender.hasPermission("NotifyUser.player.send")) {
               sender.sendMessage(ChatColor.WHITE
                     + "To ping a player, type @ and their username into the chat. This is not case-sensitive.\n"
                     + "You must type at least " + ChatColor.RED + chatListener.getMinNameLen() + ChatColor.WHITE
                     + " characters of the username in order to ping them. \n" + ChatColor.GOLD
                     + "E.g. To ping MaryAnn32, type @MaryAnn32, @MaryAnn, or @mary.\n");
            }
            sender.sendMessage(getHelpCommands(commandConfig, sender));
            // if (sender.hasPermission("NotifyUser.admin.set"))
            // sender.sendMessage(adminCommands);
            return true;
         }

         else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("mute")) {
               if (sender.hasPermission("NotifyUser.player.mute")) {
                  if (!senderIsPlayer) {
                     sender.sendMessage("You must be a player to use that command.");
                  } else {
                     notification.toggleMute(pSender);
                     // debug("Notifications " + ( isMuted ? "un" : "") + "muted
                     // for " + pSender.getName());
                     // configH.getString("messages.mute-toggle")));
                     // pSender.sendMessage("Incoming notifications are now " +
                     // (isMuted ? "un" : "") + "muted.");
                     sender.sendMessage(parseMessage(placeholders, configH.getString("messages.mute.toggle"),
                           senderName, senderName, chatListener.getMinNameLen(), notification.isMutedFor(pSender)));
                  }
               } else {
                  sender.sendMessage(noPermission);
               }
               return true;
            }

            else if (args[0].equalsIgnoreCase("reload")) {
               if (sender.hasPermission("NotifyUser.admin.reload")) {
                  /*
                   * ensureConfigExists(); if (isConfigurationValid(new
                   * File(DATA_FOLDER, "config.yml"))) { reloadConfig();
                   * updateConfig(); loadFromConfig();
                   * sender.sendMessage(ChatColor.GREEN + "Config reloaded."); }
                   * else { sender.sendMessage(ChatColor.RED + "Error: " +
                   * ChatColor.DARK_RED +
                   * "Failed to reload config. (Is it formatted correctly?)"); }
                   */
                  boolean reloadedSuccessfully = configH.reloadConfig();
                  if (reloadedSuccessfully) {
                     setValues();
                     sender.sendMessage(ChatColor.GREEN + "Config reloaded.");
                     return true;
                  } else {
                     sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED
                           + "Failed to reload config. (Is it formatted correctly?)");
                  }
               } else {
                  sender.sendMessage(noPermission);
               }
               return true;
            }

            else {
               if ((sender.hasPermission("NotifyUser.player.send"))) {
                  Player receiver = getServer().getPlayer(args[0]);
                  String receiverName = (receiver == null ? null : receiver.getName());

                  String fmt = "";
                  if (args[0].length() < chatListener.getMinNameLen())
                     fmt = "errors.not-min";
                  else if (receiverName == null) {
                     fmt = "errors.not-found";
                     receiverName = args[0];
                  } 
                  else if (notification.isMutedFor(receiver))
                     fmt = "mute.alert";
                  else if (receiver.hasPermission("NotifyUser.player.receive")) {
                     fmt = "ping.from";
                     ping(sender, receiver);
                  }
                  sender.sendMessage(parseMessage(placeholders, configH.getString("messages." + fmt), senderName,
                        receiverName, chatListener.getMinNameLen(), notification.isMutedFor(receiver)));
                  return true;
                  // sender.sendMessage(ChatColor.RED + "You must type at least
                  // " + chatListener.getMinNameLen() + " characters to ping!");
                  // sender.sendMessage(parseMessage(placeholders,
                  // configH.getString("messages.not-min")));
                  /*
                   * } else if (receiver == null) {
                   * //sender.sendMessage(ChatColor.RED + "Error: " +
                   * ChatColor.DARK_RED + "Player not found.");
                   * sender.sendMessage(parseMessage(placeholders,
                   * configH.getString("messages.not-found"))); } else if
                   * (notification.isMutedFor(receiver.getUniqueId())) {
                   * //sender.sendMessage(ChatColor.RED + receiver.getName() +
                   * " has muted notifications.");
                   * sender.sendMessage(parseMessage(placeholders,
                   * configH.getString("messages.mute-alert"))); }
                   */

               } else {
                  sender.sendMessage(noPermission);
               }
               return true;
            }
         } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
               if (sender.hasPermission("NotifyUser.admin.set")) {
                  if (notification.setSound(args[1].toUpperCase(), sender)) {
                     configH.set("notifications.sound-effect", args[1].toUpperCase());
                     configH.saveConfig();
                  }
               } else {
                  sender.sendMessage(noPermission);
               }
               return true;
            }
         }
      }
      return false;
   }

   private void ping(CommandSender sender, Player receiver) {
      notification.toPlayer(receiver);
      // sender.sendMessage("Notification sent to " +
      // receiver.getName() + ".");
      // sender.sendMessage(parseMessage(placeholders,
      // configH.getString("messages.ping-from")));
      if (sendPingNotification || receiver.hasPermission("NotifyUser.override.notify")) {
         // receiver.sendMessage(ChatColor.GREEN +
         // (senderIsPlayer ? sender.getName() : "The server") +
         // " has pinged you!");
         // String displayedSender = (isAnonymous ? "Someone" : ((sender ==
         // null) ? "The server" : receiver.getName()
         boolean thisAnonymous = (receiver.hasPermission("NotifyUser.anonymous.receive") ? false :
            (sender.hasPermission("NotifyUser.anonymous.send") ? true : isAnonymous));
         String format = configH.getString("messages.ping." + (thisAnonymous ? "anon" : "to"));
         receiver.sendMessage(parseMessage(placeholders, format, sender.getName(), receiver.getName(),
               chatListener.getMinNameLen(), notification.isMutedFor(receiver)));
      }
   }

   private void setValues() {
      // FileConfiguration config = getConfig();
      notificationType = configH.getString("chat.notify");
      sendPingNotification = (notificationType.equals("false") ? false : true);
      isAnonymous = (notificationType.equals("anonymous") ? true : false);
      noPermission = configH.getString("messages.no-perm");
      // debugEnabled = config.getBoolean("debug");
      notification.setValues(configH.getConfig());
      chatListener.setValues(configH.getConfig());
   }

   /*
    * private void updateConfig() { HashMap<String, Object> missingValues = new
    * HashMap<String, Object>(); missingValues = getMissingDefaults();
    * 
    * if (!missingValues.isEmpty()) { log.warning(
    * "It looks like your config.yml may be out of date.\n" +
    * "Adding the defaults for missing values:"); for (Entry<String, Object> key
    * : missingValues.entrySet()) { log.warning("  - " + key.getKey());
    * getConfig().set(key.getKey(), key.getValue()); } saveConfig(); log.info(
    * "Your config.yml should now be fixed and updated.\n" +
    * "If it is not, try deleting it then generate a new one with /nu reload.");
    * }
    * 
    * } private void ensureConfigExists() { File file = new File(DATA_FOLDER,
    * "config.yml"); if (!file.exists()) { log.info(
    * "No config.yml found. Generating default one."); saveDefaultConfig();
    * reloadConfig(); } }
    * 
    * private HashMap<String, Object> getMissingDefaults() { Configuration
    * defaultConfig = getConfig().getDefaults(); HashMap<String, Object>
    * foundDefaults = new HashMap<String, Object>();
    * 
    * for (String key : defaultConfig.getKeys(true)) { if
    * (!getConfig().getKeys(true).contains(key)) { foundDefaults.put(key,
    * defaultConfig.get(key)); } } return foundDefaults;
    * 
    * }
    * 
    * /Modification of Bukkit's YamlConfiguration.loadConfiguration(); public
    * boolean isConfigurationValid(File file) { Validate.notNull(file,
    * "File cannot be null");
    * 
    * YamlConfiguration config = new YamlConfiguration();
    * 
    * try { config.load(file); return true; } catch (FileNotFoundException ex) {
    * } catch (IOException ex) { getLogger().log(Level.SEVERE, "Cannot load " +
    * file, ex); } catch (InvalidConfigurationException ex) {
    * getLogger().log(Level.SEVERE, "Cannot load " + file , ex); } return false;
    * }
    */
   /*
    * String getHelpCommands(FileConfiguration config, String groupName) {
    * ConfigurationSection groupSection =
    * config.getConfigurationSection(groupName); Set<String> groupCommands =
    * groupSection.getKeys(false); String helpHeader = ChatColor.RED +
    * StringUtils.capitalize(groupName) + " Commands: \n"; String helpCommands =
    * helpHeader;
    * 
    * for (String commandName : groupCommands) { String helpCommand = "";
    * ConfigurationSection commandSection =
    * groupSection.getConfigurationSection(commandName); Set<String> commandInfo
    * = commandSection.getKeys(false);
    * 
    * for (String commandKey : commandInfo) { String commandValue =
    * commandSection.getString(commandKey); if (commandKey.equals("usage")) {
    * helpCommand += ChatColor.GREEN + commandValue; } else if
    * (commandKey.equals("description")) { helpCommand += ChatColor.WHITE +
    * " - " + commandValue + "\n"; } } helpCommands += helpCommand; } return
    * helpCommands; }
    */
   String getHelpCommands(FileConfiguration config, CommandSender sender) {
      // ConfigurationSection groupSection =
      // config.getConfigurationSection(groupName);
      String helpCommands = "";
      Set<String> groups = config.getKeys(false);
      for (String groupName : groups) {
         ConfigurationSection groupSection = config.getConfigurationSection(groupName);
         Set<String> groupSectionNames = groupSection.getKeys(false);
         String groupHeader = ChatColor.RED + StringUtils.capitalize(groupName) + " Commands: \n";
         String groupCommands = groupHeader;

         for (String sectionName : groupSectionNames) {
            if (sender.hasPermission("NotifyUser." + groupName + "." + sectionName) || sectionName == "help") {
               String helpCommand = "";
               ConfigurationSection commandSection = groupSection.getConfigurationSection(sectionName);
               Set<String> commandInfo = commandSection.getKeys(false);

               for (String commandKey : commandInfo) {
                  String commandValue = commandSection.getString(commandKey);
                  if (commandKey.equals("usage")) {
                     helpCommand += ChatColor.GREEN + commandValue;
                  } else if (commandKey.equals("description")) {
                     helpCommand += ChatColor.WHITE + " - " + commandValue + "\n";
                  }
               }
               groupCommands += helpCommand;
            }
         }
         helpCommands += groupCommands;
      }
      return helpCommands;
   }

   private String translateFormat(String fmt) {
      String newFmt = ChatColor.translateAlternateColorCodes('&', fmt);
      return newFmt;
   }

   private String parseMessage(String[] placeholders, String messageFormat, String senderName, String receiverName,
         int min, boolean isMuted) {
      String message = translateFormat(messageFormat);
      for (String label : placeholders) {
         String placeholder = "\\{(?i)" + label + "\\}";
         String replacement = "";
         switch (label) {
         case "sender":
            replacement = senderName;
            break;
         case "receiver":
            replacement = receiverName;
            break;
         case "min":
            replacement = Integer.toString(min);
            break;
         case "toggle":
            replacement = (isMuted ? "" : "un") + "muted";
            break;
         }
         message = message.replaceAll(placeholder, replacement);
      }
      return message;
   }

   /*
    * void debug(String debugMessage, String levelName) { Level level =
    * Level.parse(levelName.toUpperCase()); if (debugEnabled) { log.log(level,
    * "[DEBUG] " + debugMessage); } }
    * 
    * void debug(String debugMessage) { if (debugEnabled) { log.log(Level.INFO,
    * "[DEBUG] " + debugMessage); } }
    */
}