package com.minuxe.notifyuser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Notification {
   NotifyUser plugin;
   ArrayList<UUID> hasMuted;
   Sound sound;
   int pitch;
   int volume;
   boolean muteSound;
   String muteHighlight;
   String muteType;
   String mutePath;

   public Notification(NotifyUser plugin) {
      this.plugin = plugin;
      mutePath = NotifyUser.DATA_FOLDER + File.separator + "muted.dat";
      File file = new File(mutePath);
      if (file.exists()) {
         hasMuted = (ArrayList<UUID>) loadData(mutePath);
      } else {
         hasMuted = new ArrayList<UUID>();
         saveData(hasMuted, mutePath);
      }
   }

   void toPlayer(Player receiver) {
      if (muteSound && isMutedFor(receiver)) return;
      if (receiver != null) {
         Location receiverLocation = receiver.getLocation();
         receiver.playSound(receiverLocation, sound, volume, pitch);
         // plugin.debug(receiver.getName() + " has been notified. Location is
         // (" + receiverLocation.getX() + ", "
         // + receiverLocation.getY() + ", " + receiverLocation.getZ() + ")");
      }
   }

   void toggleMute(Player player) {
      UUID playerUUID = player.getUniqueId();
      if (hasMuted.contains(playerUUID)) {
         hasMuted.remove(playerUUID);
      } else {
         hasMuted.add(playerUUID);
      }
      saveData(hasMuted, mutePath);
   }

   boolean isMutedFor(UUID playerUUID) {
      return hasMuted.contains(playerUUID);
   }

   boolean isMutedFor(Player player) {
      if (player == null) return true;
      return isMutedFor(player.getUniqueId());
   }

   boolean setSound(String soundName, CommandSender sender) {
      String soundNameChecked = "";
      try {
         soundNameChecked = soundName.toUpperCase();
      } catch (Exception NullPointerException) {
         soundNameChecked = "ENTITY_CHICKEN_EGG";
         // plugin.debug("Sond effect is null. Switching to default
         // (ENTITY_CHICKEN_EGG)");
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

   Sound getSound() {
      return sound;
   }

   Object loadData(String path) {
      try {
         ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
         Object result = ois.readObject();
         ois.close();
         return result;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   void saveData(Object object, String path) {
      try {
         ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
         oos.writeObject(object);
         oos.flush();
         oos.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   String getHighlightMuteType() {
      return muteHighlight;
   }

   boolean mutesHighlight() {
      return (muteHighlight.equals("false") ? false : true);
   }

   boolean mutesSound() {
      return muteSound;
   }

   void setValues(FileConfiguration config) {
      String soundName = "";
      soundName = config.getString("notifications.sound-effect").toUpperCase();
      // plugin.debug("Sound effect name: " + soundName);
      sound = Sound.valueOf(soundName);
      volume = config.getInt("notifications.volume");
      pitch = config.getInt("notifications.pitch");

      muteSound = config.getBoolean("mute.sound");
      // muteType = config.getString("mute.highlight").toLowerCase();
      muteHighlight = config.getString("mute.highlight");
      // plugin.debug("Sound set to " + soundName + " from config.");
   }
}
