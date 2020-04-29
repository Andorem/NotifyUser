package io.github.andorem.notifyuser.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import io.github.andorem.notifyuser.NotifyUser;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigurationHandler {
   JavaPlugin plugin;
   FileConfiguration config;
   final File DATA_FOLDER;

   public ConfigurationHandler(JavaPlugin plugin, File folder) {
      this.plugin = plugin;
      DATA_FOLDER = folder;
   }

   public void setupConfig() {
      try {
         if (!DATA_FOLDER.exists()) {
            DATA_FOLDER.mkdir();
         }
         ensureConfigExists();
         checkConfig();
      } catch (Exception e) {
         e.printStackTrace();
      }
      updateConfig();
   }

   public void updateConfig() {
      config = plugin.getConfig();
   }

   public void checkConfig() {
      if (!NotifyUser.isVersionHigherThan(1, 12)) {
         config.addDefault("notifications.sound-effect", "BLOCK_NOTE_PLING");
      }

      HashMap<String, Object> missingValues = new HashMap<String, Object>();
      missingValues = getMissingDefaults();

      if (!missingValues.isEmpty()) {
         plugin.getLogger().warning("It looks like your config.yml may be out of date.");
         plugin.getLogger().warning("Adding the defaults for missing values:");
         for (Entry<String, Object> key : missingValues.entrySet()) {
            plugin.getLogger().warning("  - " + key.getKey());
            plugin.getConfig().set(key.getKey(), key.getValue());
         }
         plugin.saveConfig();
         plugin.getLogger().info("Your config.yml should now be fixed and updated.");
         plugin.getLogger().info("If it is not, try deleting it then generate a new one with /NotifyUser reload.");
      }
   }

   public boolean reloadConfig() {
      ensureConfigExists();
      if (isConfigurationValid(new File(NotifyUser.DATA_FOLDER, "config.yml"))) {
         plugin.reloadConfig();
         checkConfig();
         updateConfig();
         return true;
      }
      return false;
   }

   // Modification of Bukkit's YamlConfiguration.loadConfiguration();
   public boolean isConfigurationValid(File file) {
      Validate.notNull(file, "File cannot be null");
      YamlConfiguration config = new YamlConfiguration();
      try {
         config.load(file);
         return true;
      } catch (FileNotFoundException ex) {
      } catch (IOException | InvalidConfigurationException ex) {
         plugin.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
      }
      return false;
   }

   private void ensureConfigExists() {
      File file = new File(NotifyUser.DATA_FOLDER, "config.yml");
      if (!file.exists()) {
         plugin.getLogger().info("No config.yml found. Generating default one.");
         plugin.saveDefaultConfig();
         plugin.reloadConfig();
      }
   }

   private HashMap<String, Object> getMissingDefaults() {
      Configuration defaultConfig = plugin.getConfig().getDefaults();
      HashMap<String, Object> foundDefaults = new HashMap<String, Object>();

      for (String key : defaultConfig.getKeys(true)) {
         if (!plugin.getConfig().getKeys(true).contains(key)) {
            foundDefaults.put(key, defaultConfig.get(key));
         }
      }
      return foundDefaults;
   }

   public void saveConfig() {
      plugin.saveConfig();
      updateConfig();
   }

   public void set(String path, Object value) {
      config.set(path, value);
      saveConfig();
   }

   public String getString(String path) {
      return plugin.getConfig().getString(path);
   }

   public boolean getBoolean(String path) {
      return plugin.getConfig().getBoolean(path);
   }

   public FileConfiguration getConfig() {
      return plugin.getConfig();
   }

   public ConfigurationSection getHooksConfig() {
      return plugin.getConfig().getConfigurationSection("hooks");
   }

   public ConfigurationSection getChatConfig() {
      return plugin.getConfig().getConfigurationSection("chat");
   }

   public ConfigurationSection getSoundConfig() {
      return plugin.getConfig().getConfigurationSection("notifications");
   }

   public ConfigurationSection getMuteConfig() {
      return plugin.getConfig().getConfigurationSection("mute");
   }

   public ConfigurationSection getMessagesConfig() {
      return plugin.getConfig().getConfigurationSection("messages");
   }
}