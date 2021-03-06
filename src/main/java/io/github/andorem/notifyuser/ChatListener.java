package io.github.andorem.notifyuser;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
	
	Notification notification;
	String pingSymbol;
	String highlightColor;
	int minNameLen;
	
	public ChatListener(Notification notification) {
		this.notification = notification;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.getPlayer().hasPermission("NotifyUser.player.send")) {
			String sentMessage = e.getMessage();
			if (sentMessage.contains(pingSymbol)) {
				String newMessage = "";
				String[] splitMessage = sentMessage.split(" ");
				for (String word : splitMessage) {
					if (word.startsWith(pingSymbol) && word.length() > (pingSymbol.length() + 1)) {
						String strippedWord = word.split(pingSymbol)[1];
						String punctuation = strippedWord.replaceAll("[a-zA-Z0-9_]", "");
						String receiverName = strippedWord.replaceAll("[^a-zA-Z0-9_]", "");
					
						Player receiver = Bukkit.getPlayer(receiverName);
					
						if (receiver != null && receiverName.length() >= minNameLen) {
							word = highlightColor + pingSymbol + receiverName + ChatColor.RESET + punctuation;
							if (receiver.hasPermission("NotifyUser.player.receive")) {
								notification.toPlayer(receiver);
							}
						}
					}
					newMessage += word + " ";
				}
				e.setMessage(newMessage);
			}
		}
	}
	
	int getMinNameLen() {
		return minNameLen;
	}
	
	void setValuesFrom(FileConfiguration config) {
        pingSymbol = config.getString("chat.symbol");
        minNameLen = config.getInt("chat.min-name-length");
        highlightColor = ChatColor.translateAlternateColorCodes('&', config.getString("chat.highlight-color"));
	}
}
