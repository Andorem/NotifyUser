package com.minuxe.notifyuser;

import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

public class ChatListener implements Listener {

   NotifyUser plugin;
   Notification notification;
   String pingSymbol;
   String highlightColor;
   String msgColor;
   int minNameLen;
   boolean highlightAll;
   boolean allowPartial;

   public ChatListener(Notification notification, NotifyUser plugin) {
      this.notification = notification;
      this.plugin = plugin;
   }

   @EventHandler
   private final void onPlayerChatTabComplete(final PlayerChatTabCompleteEvent event) {
      final String token = event.getLastToken();
      if (token.startsWith(pingSymbol)) {
         final Collection<String> pingCompletions = event.getTabCompletions();
         pingCompletions.clear();
         final String tabStart = token.replaceAll(pingSymbol, "").toLowerCase();
         for (final Player player : Bukkit.getOnlinePlayers()) {
            final String playerName = player.getName();
            if (playerName.toLowerCase().startsWith(tabStart)) {
               pingCompletions.add(pingSymbol + playerName);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void onChat(AsyncPlayerChatEvent e) {
      for (Player player : plugin.getServer().getOnlinePlayers()) {
         // String pingFrom = ChatColor.GRAY + "[Anonymous]";
         if (player.hasPermission("NotifyUser.player.send")) {
            String sentMessage = e.getMessage();
            // String senderName = e.getPlayer().getName();

            // String playerName = player.getName();
            // plugin.debug("Chat event indicated with message: " +
            // sentMessage);

            /*
             * String playerTag = pingSymbol + playerName; if
             * (sentMessage.toLowerCase().contains(playerTag.toLowerCase())) {
             * plugin.debug("Sender " + senderName + " pinged " + playerName);
             * e.setMessage(sentMessage.replaceAll(playerTag, highlightColor +
             * playerTag)); }
             */

            if (sentMessage.contains(pingSymbol)) {
               // plugin.debug("Chat event has ping symbol '" + pingSymbol +
               // "'");
               String newMessage = "";
               // String playerTag = "";
               String[] splitMessage = sentMessage.split(" ");
               // plugin.debug("Splitting chat message: " + splitMessage);
               for (String word : splitMessage) {
                  // plugin.debug("Looping through chat message: word = " +
                  // word);

                  if (word.startsWith(pingSymbol) && word.length() > (pingSymbol.length() + 1)) {

                     // plugin.debug("Word starts with ping symbol and is " +
                     // word.length() + " characters long.");
                     String strippedWord = word.split(pingSymbol)[1];
                     // plugin.debug("Ping symbol stripped from word: " +
                     // strippedWord);

                     String[] wordByChars = strippedWord.split("");
                     String[] wordByPunctuation = strippedWord.split("[^a-zA-Z0-9_]");

                     String textAfter = "";
                     for (int i = wordByPunctuation[0].length(); i < wordByChars.length; i++) {
                        textAfter += wordByChars[i];
                     }
                     // if (textAfter != "") { plugin.debug("Text after name
                     // found: " + textAfter);}

                     String receiverName = wordByPunctuation[0];
                     // plugin.debug("Final receiver name parsed: " +
                     // receiverName);
                     Player receiver = Bukkit.getPlayer(receiverName);
                     // plugin.debug("Matched receiver name to player.");
                     boolean correctLen = (allowPartial ? (receiverName.length() >= minNameLen)
                           : (receiverName.length() == receiver.getName().length()));
                     if (receiver != null && correctLen) {
                        // pingFrom = e.getPlayer().get
                        // plugin.debug("Receiver not null and name used in ping
                        // of length " + receiverName.length());
                        boolean namesMatch = compareNames(player.getName(), receiverName, allowPartial);
                        boolean muteHighlight = (!notification.mutesHighlight() ? false
                              : (!notification.isMutedFor(player) ? false
                                    : (notification.getHighlightMuteType().equals("all") ? true
                                          : (namesMatch ? true : false))));
                        if (!muteHighlight) {
                           if (highlightAll || (namesMatch && player.hasPermission("NotifyUser.player.highlight"))
                                 || player.hasPermission("NotifyUser.override.highlightall")) {
                              word = highlightColor + pingSymbol + receiverName + msgColor + textAfter;
                           }
                        }
                        // playerTag = pingSymbol + receiverName;
                        // plugin.debug("Sent new ping from " + senderName + "
                        // via chat: '" + word + "'");
                        if (receiver.hasPermission("NotifyUser.player.receive")) {
                           notification.toPlayer(receiver);
                        }
                     }
                  }
                  newMessage += word + " ";
               }
               // e.setMessage(sentMessage.replaceAll(playerTag, highlightColor
               // + playerTag));
               newMessage = String.format(e.getFormat(), e.getPlayer().getDisplayName(), newMessage); // put
                                                                                                      // in
                                                                                                      // chat
                                                                                                      // message
                                                                                                      // format
               player.sendMessage(newMessage);
               Bukkit.getLogger().info(ChatColor.stripColor(newMessage));

               e.setCancelled(true);
            }
         }
      }
   }

   private boolean compareNames(String first, String second, boolean allowPartial) {
      String firstLower = first.toLowerCase();
      String secondLower = second.toLowerCase();
      return (allowPartial ? firstLower.startsWith(secondLower) : firstLower.equals(secondLower));
   }

   int getMinNameLen() {
      return minNameLen;
   }

   void setValues(FileConfiguration config) {
      pingSymbol = config.getString("chat.symbol");
      minNameLen = config.getInt("chat.min-name-length");
      highlightColor = ChatColor.translateAlternateColorCodes('&', config.getString("chat.highlight-color"));
      msgColor = ChatColor.translateAlternateColorCodes('&', config.getString("chat.message-color"));
      highlightAll = config.getBoolean("chat.highlight-for-all");
      allowPartial = config.getBoolean("chat.allow-partial");
   }
}
