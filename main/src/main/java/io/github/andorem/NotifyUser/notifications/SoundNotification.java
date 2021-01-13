package io.github.andorem.notifyuser.notifications;

import java.util.UUID;

import io.github.andorem.notifyuser.NotifyUser;
import io.github.andorem.notifyuser.handlers.ConfigurationHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SoundNotification {
   static String DEFAULT_SOUND;
   static Sound sound;
   static int pitch;
   static int volume;
   static boolean muteEnabledForSound;

   Player receiver;

   public SoundNotification(Player receiver) {
      this.receiver = receiver;
   }

   public void send() {
      if (isMutedFor(receiver)) return;
      if (receiver != null) {
         Location receiverLocation = receiver.getLocation();
         receiver.playSound(receiverLocation, sound, volume, pitch);
      }
   }

   public static boolean isMutedFor(UUID playerUUID) {
      return muteEnabledForSound && Notification.isMutedFor(playerUUID);
   }

   public static boolean isMutedFor(Player player) {
      if (player == null) return true;
      return isMutedFor(player.getUniqueId());
   }

   public static boolean setSound(String soundName, CommandSender sender) {
      String soundNameChecked = "";
      try {
         soundNameChecked = soundName.toUpperCase();
      } catch (Exception NullPointerException) {
         soundNameChecked = DEFAULT_SOUND;
      }

      Sound newSound;
      try {
         newSound = Sound.valueOf(soundNameChecked);
      } catch (Exception e) {
         sender.sendMessage(ChatColor.RED + "That is not a valid sound name!");
         return false;
      }

      sound = newSound;
      sender.sendMessage("Notification sound set to " + ChatColor.GREEN + soundNameChecked);
      return true;
   }

   public static Sound getSound() {
      return sound;
   }

   public static void setDefaultSound() {
      DEFAULT_SOUND = NotifyUser.isVersionHigherThan(1, 12) ?
              "BLOCK_NOTE_BLOCK_PLING" : "BLOCK_NOTE_PLING";
   }

   public static void setValues(ConfigurationHandler configHandler) {
      ConfigurationSection soundConfig = configHandler.getSoundConfig();
      ConfigurationSection muteConfig = configHandler.getMuteConfig();
      String soundName = soundConfig.getString("sound-effect").toUpperCase();
      sound = Sound.valueOf(soundName);
      volume = soundConfig.getInt("volume");
      pitch = soundConfig.getInt("pitch");
      muteEnabledForSound = muteConfig.getBoolean("sound");
   }
}
