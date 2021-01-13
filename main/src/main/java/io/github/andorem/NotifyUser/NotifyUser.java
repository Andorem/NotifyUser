package io.github.andorem.notifyuser;

import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.andorem.notifyuser.handlers.ConfigurationHandler;
import io.github.andorem.notifyuser.hooks.HookManager;
import io.github.andorem.notifyuser.listeners.ChatListener;
import io.github.andorem.notifyuser.notifications.ChatNotification;
import io.github.andorem.notifyuser.notifications.Notification;
import io.github.andorem.notifyuser.notifications.SoundNotification;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
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
   static Logger log;
   FileConfiguration commandConfig = YamlConfiguration.loadConfiguration(getTextResource("commands.yml"));

   Notification notification;
   ChatListener chatListener;
   ConfigurationHandler configHandler;
   HookManager hookManager;

   boolean sendPingNotification;
   boolean isAnonymous;
   static boolean debugEnabled;
   static boolean isServerRunningPaper;

   String notificationType;
   String[] placeholders = { "sender", "receiver", "min", "toggle" };
   String noPermission;
   String chatHeader = ChatColor.AQUA + "\n======[" + ChatColor.WHITE + getName() + ChatColor.AQUA + "]======";


   // String generalCommands = getHelpCommands(commandConfig, "general");
   // String adminCommands = getHelpCommands(commandConfig, "admin");

   @Override
   public void onEnable() {
      DATA_FOLDER = getDataFolder();
      log = getLogger();

       try { if(!DATA_FOLDER.exists()) { DATA_FOLDER.mkdir();} }
       catch (Exception e) { e.printStackTrace();}

      notification = new Notification(this);
      chatListener = new ChatListener(this);
      configHandler = new ConfigurationHandler(this, DATA_FOLDER);

      configHandler.setupConfig();
      setValues();

       debugEnabled = getConfig().getBoolean("debug");
       debug("=====================================================");
       debug("Debug mode activated. Now reporting to console.");
       debug("To disable debugging, set 'debug' in 'config.yml' to false.");
       debug("=====================================================");

      getServer().getPluginManager().registerEvents(chatListener, this);

      isServerRunningPaper = checkIfRunningPaper();

      hookManager = new HookManager(this);
      hookManager.enableHooks();
   }

   @Override
   public void onDisable() {

   }

   @Override
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

      boolean senderIsPlayer = (sender instanceof Player);
      Player pSender = (senderIsPlayer ? (Player) sender : null);
      String senderName = sender.getName();

      noPermission = parseMessage(placeholders, configHandler.getString("messages.errors.no-perm"),
              senderName, senderName, Notification.isMutedFor(pSender));

      if (cmd.getName().equalsIgnoreCase("nu")) {
         if ((args.length < 1) || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            sender.sendMessage(chatHeader);
            if (sender.hasPermission("notifyuser.player.send")) {
               sender.sendMessage(ChatColor.WHITE
                     + "To ping a player, type @ and their username into the chat. This is not case-sensitive.\n"
                     + "You must type at least " + ChatColor.RED + ChatNotification.getMinNameLengthRequired() + ChatColor.WHITE
                     + " characters of the username in order to ping them. \n" + ChatColor.GOLD
                     + "E.g. To ping MaryAnn32, type @MaryAnn32, @MaryAnn, or @mary.\n");
            }
            sender.sendMessage(getHelpCommands(commandConfig, sender));
            // if (sender.hasPermission("notifyuser.admin.set"))
            // sender.sendMessage(adminCommands);
            return true;
         }

         else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("mute")) {
               if (sender.hasPermission("notifyuser.player.mute")) {
                  if (!senderIsPlayer) {
                     sender.sendMessage("You must be a player to use that command.");
                  } else {
                     Notification.toggleMute(pSender);
                     sender.sendMessage(parseMessage(placeholders, configHandler.getString("messages.mute.toggle"),
                           senderName, senderName, Notification.isMutedFor(pSender)));
                  }
               } else {
                  sender.sendMessage(noPermission);
               }
               return true;
            }

            else if (args[0].equalsIgnoreCase("reload")) {
               if (sender.hasPermission("notifyuser.admin.reload")) {
                  boolean reloadedSuccessfully = configHandler.reloadConfig();
                  if (reloadedSuccessfully) {
                     setValues();
                     hookManager.reloadHooks();
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
               if ((sender.hasPermission("notifyuser.player.send"))) {
                  Player receiver = getServer().getPlayer(args[0]);
                  String receiverName = (receiver == null ? null : receiver.getName());

                  String fmt = "";
                  if (args[0].length() < ChatNotification.getMinNameLengthRequired())
                     fmt = "errors.not-min";
                  else if (receiverName == null) {
                     fmt = "errors.not-found";
                     receiverName = args[0];
                  } 
                  else if (notification.isMutedFor(receiver))
                     fmt = "mute.alert";
                  else if (receiver.hasPermission("notifyuser.player.receive")) {
                     fmt = "ping.from";
                     ping(sender, receiver);
                  }
                  sender.sendMessage(parseMessage(placeholders, configHandler.getString("messages." + fmt), senderName,
                        receiverName, notification.isMutedFor(receiver)));
                  return true;
               } else {
                  sender.sendMessage(noPermission);
               }
               return true;
            }
         } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
               if (sender.hasPermission("notifyuser.admin.set")) {
                  if (SoundNotification.setSound(args[1].toUpperCase(), sender)) {
                     configHandler.set("notifications.sound-effect", args[1].toUpperCase());
                     configHandler.saveConfig();
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
      (new SoundNotification(receiver)).send();
      if (sendPingNotification || receiver.hasPermission("notifyuser.override.notify")) {
         boolean thisAnonymous = (receiver.hasPermission("notifyuser.anonymous.receive") ? false :
            (sender.hasPermission("notifyuser.anonymous.send") ? true : isAnonymous));
         String format = configHandler.getString("messages.ping." + (thisAnonymous ? "anon" : "to"));
         receiver.sendMessage(parseMessage(placeholders, format, sender.getName(), receiver.getName(), Notification.isMutedFor(receiver)));
      }
   }

   private void setValues() {
      notificationType = configHandler.getString("chat.notify");
      sendPingNotification = (!notificationType.equals("false"));
      isAnonymous = (notificationType.equals("anonymous"));
      noPermission =  translateFormat(configHandler.getString("messages.errors.no-perm"));
       debugEnabled = configHandler.getBoolean("debug");
      SoundNotification.setValues(configHandler);
      ChatNotification.setValues(configHandler);
   }

   String getHelpCommands(FileConfiguration config, CommandSender sender) {
      String helpCommands = "";
      Set<String> groups = config.getKeys(false);
      for (String groupName : groups) {
         ConfigurationSection groupSection = config.getConfigurationSection(groupName);
         Set<String> groupSectionNames = groupSection.getKeys(false);
         String groupHeader = ChatColor.RED + StringUtils.capitalize(groupName) + " Commands: \n";
         String groupCommands = "";

         for (String sectionName : groupSectionNames) {
            if (sender.hasPermission("notifyuser." + groupName + "." + sectionName) || sectionName == "help") {
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
         debug("groupCommands = " + groupCommands);
         if (!groupCommands.isEmpty()) {
            helpCommands += groupHeader + groupCommands;
         }
      }
      return helpCommands;
   }

   private String translateFormat(String fmt) {
      String newFmt = ChatColor.translateAlternateColorCodes('&', fmt);
      return newFmt;
   }

   private String parseMessage(String[] placeholders, String messageFormat, String senderName, String receiverName, boolean isMuted) {
      int min = ChatNotification.getMinNameLengthRequired();
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

   public ConfigurationHandler getConfigHandler() {
      return this.configHandler;
   }

   // Server Version check provided by @Zombie_Striker
   private static final String SERVER_VERSION;
   static {
      String name = Bukkit.getServer().getClass().getName();
      name = name.substring(name.indexOf("craftbukkit.") + "craftbukkit.".length());
      name = name.substring(0, name.indexOf("."));
      SERVER_VERSION = name;
   }

   public static boolean isVersionHigherThan(int mainVersion, int secondVersion) {
      String firstChar = SERVER_VERSION.substring(1, 2);
      int fInt = Integer.parseInt(firstChar);
      if (fInt < mainVersion)
         return false;
      StringBuilder secondChar = new StringBuilder();
      for (int i = 3; i < 10; i++) {
         if (SERVER_VERSION.charAt(i) == '_' || SERVER_VERSION.charAt(i) == '.')
            break;
         secondChar.append(SERVER_VERSION.charAt(i));
      }

      int sInt = Integer.parseInt(secondChar.toString());
      if (sInt <= secondVersion)
         return false;
      return true;
   }

   public static boolean checkIfRunningPaper() {
      try {
         boolean getPaper = Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData") != null;
         return true;
      }
      catch (ClassNotFoundException e) {
         return false;
      }
   }

    public static void log(String message, String levelName) {
       Level level;
       try {
          level = Level.parse(levelName.toUpperCase());
       } catch (IllegalArgumentException e) {
          level = Level.INFO;
       }
       log.log(level, message);
    }

    public static void debug(String debugMessage) { if (debugEnabled) { log.log(Level.INFO,
    "[DEBUG] " + debugMessage); } }

   public static boolean debugEnabled() {
      return debugEnabled;
   }
}