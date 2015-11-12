package io.github.andorem.notifyuser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

 
public class NotifyUser extends JavaPlugin {
	
	public static File DATA_FOLDER;
	Logger log = getLogger();
	Notification notification;
	ChatListener chatListener;
	boolean sendPingNotification;
	FileConfiguration commandConfig = YamlConfiguration.loadConfiguration(getTextResource("commands.yml"));

	String noPermission = ChatColor.DARK_RED + "You don't have permission to perform this command.";
	String chatHeader = ChatColor.AQUA + "\n======[" + ChatColor.WHITE + getName() + ChatColor.AQUA + "]======";
	String generalCommands = getHelpCommands(commandConfig, "general");
	String adminCommands = getHelpCommands(commandConfig, "admin");
	
	@Override
    public void onEnable() {
		DATA_FOLDER = getDataFolder();

        try {
    		if(!DATA_FOLDER.exists()) {
    		    DATA_FOLDER.mkdir();
    		}
    		ensureConfigExists();
            updateConfig();
        } 
        catch (Exception e) {
            e.printStackTrace();

        }

		notification = new Notification();
		chatListener = new ChatListener(notification);
	    loadFromConfig();
	    
	    getServer().getPluginManager().registerEvents(chatListener, this);
	    
    }
    @Override
    public void onDisable() {
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	boolean senderIsPlayer = (sender instanceof Player);
    	Player pSender = (senderIsPlayer ? (Player) sender : null);
    	
    	if (cmd.getName().equalsIgnoreCase("nu")) {
    		if ((args.length < 1) || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
    		sender.sendMessage(chatHeader);
    		sender.sendMessage(ChatColor.WHITE + "To ping a player, type @ and their username into the chat. This is not case-sensitive.\n"
    		+ "You must type at least " + ChatColor.RED + chatListener.getMinNameLen()
			+ ChatColor.WHITE + " characters of the username in order to ping them. \n"
			+ ChatColor.GOLD + "E.g. To ping MaryAnn32, type @MaryAnn32, @MaryAnn, or @mary.\n");
    		sender.sendMessage(generalCommands);
    		if (sender.hasPermission("NotifyUser.admin.set")) sender.sendMessage(adminCommands);
    		return true;
    		}
    		
    		else if (args.length == 1) {
    			if (args[0].equalsIgnoreCase("mute")) {
    				if (sender.hasPermission("NotifyUser.player.mute")) {
    					if (!senderIsPlayer) {
    						sender.sendMessage("You must be a player to use that command.");
    					}
    					else {
    						boolean isMuted = notification.isMutedFor(pSender.getUniqueId());
    						notification.toggleMute(pSender);
    						pSender.sendMessage("Incoming notifications are now " + ( isMuted ? "un" : "") + "muted.");
    					}
    				}
    				else {
    					sender.sendMessage(noPermission);
    				}
    				return true;
    			}
    			
    			else if (args[0].equalsIgnoreCase("reload")) {
    				if (sender.hasPermission("NotifyUser.admin.reload")) {
    					ensureConfigExists();
    					if (isConfigurationValid(new File(DATA_FOLDER, "config.yml"))) {
    						reloadConfig();
    						updateConfig();
    						loadFromConfig();
        					sender.sendMessage(ChatColor.GREEN + "Config reloaded.");
    					}
    					else {
    						sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Failed to reload config. (Is it formatted correctly?)");
    					}
    				}
    				else {
    					sender.sendMessage(noPermission);
    				}
					return true;
    			}
    			
    			else {
    				if ((sender.hasPermission("NotifyUser.player.send"))) {
    					Player receiver = getServer().getPlayer(args[0]);
    					if (args[0].length() < chatListener.getMinNameLen()) {
    						sender.sendMessage(ChatColor.RED + "You must type at least " + chatListener.getMinNameLen() + " characters to ping!");
    					}
    					else if (receiver == null) {
    						sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.DARK_RED + "Player not found.");
    					}
    					else if (notification.isMutedFor(receiver.getUniqueId())) {
    						sender.sendMessage(ChatColor.RED + receiver.getName() + " has muted notifications.");
    					}
    					else if (receiver.hasPermission("NotifyUser.player.receive")){
    						notification.toPlayer(receiver);
    						sender.sendMessage("Notification sent to " + receiver.getName() + ".");
    						if (sendPingNotification) {
    							receiver.sendMessage(ChatColor.GREEN + (senderIsPlayer ? sender.getName() : "The server") + " has pinged you!");
    						}
    					}
    				}
    				else {
    					sender.sendMessage(noPermission);
    				}
    				return true;
    			
    			}
    		}
    		else if (args.length == 2) {
    			if (args[0].equalsIgnoreCase("set")) {
    				if (sender.hasPermission("NotifyUser.admin.set")) {
    					if (notification.setSound(args[1].toUpperCase(), sender)) {
    						getConfig().set("notifications.sound-effect", args[1].toUpperCase());
    						saveConfig();
    					}
    				}
    				else {
    					sender.sendMessage(noPermission);
    				}
    				return true;
    			}
    		}
    	}
    return false;
    }
    
    
    private void loadFromConfig() {
        FileConfiguration config = getConfig();
        sendPingNotification = config.getBoolean("chat.notify");
        notification.setValuesFrom(config);
        chatListener.setValuesFrom(config);
    }
    
    private void updateConfig() {
    	HashMap<String, Object> missingValues = new HashMap<String, Object>();
		missingValues = getMissingDefaults();

        if (!missingValues.isEmpty()) {
        	log.warning("It looks like your config.yml may be out of date.\n" 
        				 + "Adding the defaults for missing values:");
        	for (Entry<String, Object> key : missingValues.entrySet()) {
        		log.warning("  - " + key.getKey());
        		getConfig().set(key.getKey(), key.getValue());
        	}
        	saveConfig();
        	log.info("Your config.yml should now be fixed and updated.\n"
        			  + "If it is not, try deleting it then generate a new one with /nu reload.");
        }
    	
    }
    private void ensureConfigExists() {
    	File file = new File(DATA_FOLDER, "config.yml");
        if (!file.exists()) {
           log.info("No config.yml found. Generating default one.");  
           saveDefaultConfig();
           reloadConfig();
        }
    }
    
    private HashMap<String, Object> getMissingDefaults() {
    	Configuration defaultConfig = getConfig().getDefaults();
    	HashMap<String, Object> foundDefaults = new HashMap<String, Object>();
    	
        for (String key : defaultConfig.getKeys(true)) {
        	if (!getConfig().getKeys(true).contains(key)) {
        		foundDefaults.put(key, defaultConfig.get(key));
        	}
        }
        return foundDefaults;
        
    }
    
    //Modification of Bukkit's YamlConfiguration.loadConfiguration();
    public boolean isConfigurationValid(File file) {
        Validate.notNull(file, "File cannot be null");

        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
            return true;
        } 
        catch (FileNotFoundException ex) {
        } 
        catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        } 
        catch (InvalidConfigurationException ex) {
            getLogger().log(Level.SEVERE, "Cannot load " + file , ex);
        }
        return false;
    }
    
    String getHelpCommands(FileConfiguration config, String groupName) {
    	ConfigurationSection groupSection = config.getConfigurationSection(groupName);
    	Set<String> groupCommands = groupSection.getKeys(false);
    	String helpHeader = ChatColor.RED + StringUtils.capitalize(groupName) + " Commands: \n";
    	String helpCommands = helpHeader;
    	
    	for (String commandName : groupCommands) {
    		String helpCommand = "";
    		ConfigurationSection commandSection = groupSection.getConfigurationSection(commandName);
    		Set<String> commandInfo = commandSection.getKeys(false);
    		
    		for (String commandKey : commandInfo) {
    			String commandValue = commandSection.getString(commandKey);
    			if (commandKey.equals("usage")) {
    				helpCommand += ChatColor.GREEN + commandValue;
    			}
    			else if (commandKey.equals("description")) {
    				helpCommand += ChatColor.WHITE + " - " + commandValue + "\n";
    			}
    		}
    		
    		helpCommands += helpCommand;
    	}
    	
    	return helpCommands;
    }
    
}