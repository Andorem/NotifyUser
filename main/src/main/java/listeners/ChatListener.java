package io.github.andorem.notifyuser.main.listeners;

import java.util.*;

import io.github.andorem.notifyuser.main.notifications.ChatNotification;
import io.github.andorem.notifyuser.main.notifications.SoundNotification;
import io.github.andorem.notifyuser.main.NotifyUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

public class ChatListener implements Listener {

   NotifyUser plugin;
   SoundNotification notification;

   String pingSymbol;
   String highlightColor;
   String msgColor;

   boolean highlightForAll;
   String highlightMuteType;

   int minNameLengthRequired;
   boolean allowPartial;

   public ChatListener(NotifyUser plugin) {
      this.notification = notification;
      this.plugin = plugin;
   }

   // PlayerChatTabCompleteEvent deprecated and is not fired in server versions >= 1.13
   @EventHandler
   private final void onPlayerChatTabComplete(final PlayerChatTabCompleteEvent event) {
      if (NotifyUser.isVersionHigherThan(1, 12)) return;

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

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   private final void onChat(AsyncPlayerChatEvent e) {
      Player thisPlayer = e.getPlayer();
      if (ChatNotification.canSend(thisPlayer, e.getMessage())) {
         String messageColor = ChatColor.getLastColors(e.getFormat());
         ChatNotification chatNotification = new ChatNotification(thisPlayer, e, messageColor, e.getRecipients());
         chatNotification.send();
//         if (notifyAll) e.setMessage(chatNotification.getMessage());
      }
   }

//   @EventHandler
//   private final void onTab(TabCompleteEvent e) {
//      NotifyUser.debug("fired");
//      if (!NotifyUser.isVersionHigherThan(1, 12)) return;
//      String[] tabMessage = e.getBuffer().split(" ");
//      NotifyUser.debug(Arrays.toString(tabMessage));
//      if (e.getBuffer().contains(pingSymbol)) {
//         NotifyUser.debug("Has ping symbol");
//         NotifyUser.debug("tabMessage[tabMessage.length - 1] = " + tabMessage[tabMessage.length - 1]);
//         if (tabMessage.length > 0 && tabMessage[tabMessage.length - 1].startsWith(pingSymbol)) {
//            Collection<String> pingCompletions = e.getCompletions();
//            pingCompletions.clear();
//            String tabPing = tabMessage[tabMessage.length - 1];
//            NotifyUser.debug("tabPing = " + tabPing);
//            final String tabStart = tabPing.replaceAll(pingSymbol, "").toLowerCase();
//            NotifyUser.debug("tabStart = " + tabStart);
//            for (final Player player : Bukkit.getOnlinePlayers()) {
//               final String playerName = player.getName();
//               NotifyUser.debug("Cycling thru player names: " + playerName);
//               if (playerName.toLowerCase().startsWith(tabStart)) {
//                  pingCompletions.add(pingSymbol + playerName);
//                  NotifyUser.debug("Adding to pingCompletions");
//               }
//            }
//         }
//      }
//   }

//   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
//   public void onChat(AsyncPlayerChatEvent e) {
//      NotifyUser.debug("ChatListener: highest priority");
//      NotifyUser.debug("ChatListener: old eMessage = " + e.getMessage());
//      NotifyUser.debug("ChatListener: old format = " + e.getFormat());
//      for (final Player player : Bukkit.getOnlinePlayers()) {
//         if (player.hasPermission("NotifyUser.player.send") && e.getMessage().contains(pingSymbol)) {
//            String newMessage = "";
//            String lastColor = msgColor;
//            Set<String> receiverNames = new HashSet<>();
//            String[] splitMessage = e.getMessage().split(" ");
//            NotifyUser.debug("ChatListener: splitMessage = " + Arrays.toString(splitMessage));
//            for (int i = 0; i < splitMessage.length; i++) {
//               String word = splitMessage[i];
//               NotifyUser.debug("ChatListener: word = " + word);
//               if (i != 0) {
//                  String wordBefore = splitMessage[i - 1];
//                  NotifyUser.debug("ChatListener: word before = " + wordBefore);
//                  lastColor = ChatColor.getLastColors(wordBefore);
//               }
//               NotifyUser.debug("ChatListener: lastColor = " + ChatColor.getByChar(lastColor));
//               word = ChatColor.stripColor(word);
//               if (word.startsWith(pingSymbol) && word.length() > (pingSymbol.length() + 1)) {
//                  //                  String strippedWord = word.split(pingSymbol)[1];
//                  //                  String[] wordByChars = strippedWord.split("");
//                  //                  String[] wordByPunctuation = strippedWord.split("[^a-zA-Z0-9_]");
//                  //
//                  //                  String receiverName = wordByPunctuation[0];
//                  //                  Player receiver = Bukkit.getPlayer(receiverName);
//                  String wordWithoutPunctuation = word.replaceAll("[^a-zA-Z0-9_]", "");
//                  String receiverName = wordWithoutPunctuation.split(pingSymbol)[0];
//                  NotifyUser.debug("ChatListener: wordWithoutPunctuation = " + wordWithoutPunctuation);
//                  NotifyUser.debug("ChatListener: receiverName = " + receiverName);
//                  if (isReceiverNameValid(receiverName)) receiverNames.add(receiverName);
//               }
//            }
//
//            for (String receiverName : receiverNames) {
//               NotifyUser.debug("ChatListener: receiverName loop = " + receiverName);
//               Player receiver = Bukkit.getPlayer(receiverName);
//               // Chat notification
//               if (!chatNotificationsAreMutedFor(player) &&
//                       chatMessageShouldBeHighlightedFor(player, receiverName)) {
//                  NotifyUser.debug("ChatListener: highlight format after = " + e.getFormat());
//                  newMessage = newMessage.replace(pingSymbol + receiverName,
//                          highlightColor + pingSymbol + receiverName + lastColor);
//                  NotifyUser.debug("ChatListener: new message = " + newMessage);
//                  //                        word = highlightName(receiverName, lastColors, wordByChars, wordByPunctuation);
//               }
//
//               // Sound notification
//               if (receiver.hasPermission("NotifyUser.player.receive")
//                       && e.getRecipients().contains(receiver)) {
//                  for (Player recipient : e.getRecipients()) {
//                     NotifyUser.debug("ChatListener: recipient = " + recipient.getName());
//                  }
//                  (new SoundNotification(receiver)).send();
//               }
//            }
//            e.setMessage(newMessage);
//            NotifyUser.debug("ChatListener: new eMessage = " + e.getMessage());
//         }
//      }
//   }

//   @EventHandler(priority = EventPriority.HIGHEST)
//   public void onChatPost(AsyncPlayerChatEvent e) {
//      NotifyUser.debug("Highest priority chat format: " + e.getFormat());
//   }

//   @EventHandler(priority = EventPriority.HIGHEST)
//   public void onChat(AsyncPlayerChatEvent e) {
//      Player player = e.getPlayer();
//      if (player.hasPermission("NotifyUser.player.send") && e.getMessage().contains(pingSymbol)) {
//         String newMessage = e.getMessage();
//         Set<String> receiverNames = new HashSet<>();
//         String[] splitMessage = e.getMessage().split(" ");
//         for (int i = 0; i < splitMessage.length; i++) {
//            String word = splitMessage[i];
//            if (i !=0String wordBefore = splitMessage[i - 1];
//            String lastColor = ChatColor.getLastColors(wordBefore);
//            NotifyUser.debug("ChatListener: word = " + word);
//            if (word.startsWith(pingSymbol) && word.length() > (pingSymbol.length() + 1)) {
//                  String strippedWord = word.split(pingSymbol)[1];
//                  String[] wordByChars = strippedWord.split("");
//                  String[] wordByPunctuation = strippedWord.split("[^a-zA-Z0-9_]");
//
//                  String receiverName = wordByPunctuation[0];
////                  Player receiver = Bukkit.getPlayer(receiverName);
////               String wordWithoutPunctuation = word.replaceAll("[^a-zA-Z0-9_]", "");
////               String receiverName = wordWithoutPunctuation.split(pingSymbol)[0];
//               if (isReceiverNameValid(receiverName)) {
//                  receiverNames.add(receiverName);
//                  Player receiver = Bukkit.getPlayer(receiverName);
//                  // Chat notification
//                  if (!chatNotificationsAreMutedFor(player) &&
//                          chatMessageShouldBeHighlightedFor(player, receiverName)) {
////                     wordWithoutPunctuation = newMessage.replace(pingSymbol + receiverName,
////                             highlightColor + pingSymbol + receiverName + lastColor);
//                        word = highlightName(receiverName, lastColor, wordByChars, wordByPunctuation);
//                  }
//
//                  // Sound notification
//                  if (receiver.hasPermission("NotifyUser.player.receive")
//                          && e.getRecipients().contains(receiver)
//                          && !receiverNames.contains(receiverName)) {
//                     notification.toPlayer(receiver);
//                  }
//               }
//            }
//         }

//         for (String receiverName : receiverNames) {
//
////         }
//         e.setMessage(newMessage);
//      }
//   }

//   private String highlightName(String receiverName, String lastColors, String[] byChars, String[] byPunctuation) {
//      String textAfter = "";
//      for (int i = byPunctuation[0].length(); i < byChars.length; i++) {
//         textAfter += byChars[i];
//      }
//      String withNameHighlighted = highlightColor + pingSymbol + receiverName + lastColors + textAfter;
//      return withNameHighlighted;
//   }
//
//   private boolean compareNames(String first, String second) {
//      String firstLower = first.toLowerCase();
//      String secondLower = second.toLowerCase();
//      return (allowPartial ? firstLower.startsWith(secondLower) : firstLower.equals(secondLower));
//   }
//
//   private boolean isReceiverNameValid(String receiverName) {
//      Player receiver = Bukkit.getPlayer(receiverName);
//      if (receiver == null) return false;
//
//      return checkNameLength(receiverName, receiver);
//   }
//
//   private boolean checkNameLength(String receiverName, Player receiver) {
//      return allowPartial ? (receiverName.length() >= minNameLengthRequired)
//              : (receiverName.length() == receiver.getName().length());
//   }
//
//   private boolean isHighlightMuted() {
//      NotifyUser.debug("ChatListener: highlightMuteType = " + highlightMuteType);
//      return highlightMuteType.equalsIgnoreCase("true") || highlightMuteType.equalsIgnoreCase("all");
//   }
//
//   private boolean chatNotificationsAreMutedFor(Player player) {
//      NotifyUser.debug("ChatListener: isHighlightMuted = " + isHighlightMuted() + "; isMutedFor(" + player.getName() + ") = " +
//              notification.isMutedFor(player));
//      return isHighlightMuted() && notification.isMutedFor(player);
//   }
//
//   private boolean chatMessageShouldBeHighlightedFor(Player player, String receiverName) {
//      boolean playerAndReceiverNameMatch = compareNames(player.getName(), receiverName);
//      boolean highlightForPlayer = playerAndReceiverNameMatch && player.hasPermission("NotifyUser.player.highlight");
//      boolean overrideHighlight = player.hasPermission("NotifyUser.override.highlightall");
//      return highlightForAll || highlightForPlayer || overrideHighlight;
//   }
//
//   public int getMinNameLengthRequired() {
//      return minNameLengthRequired;
//   }
//
//   public void setValues(FileConfiguration config) {
//      pingSymbol = config.getString("chat.symbol");
//      minNameLengthRequired = config.getInt("chat.min-name-length");
//      highlightColor = ChatColor.translateAlternateColorCodes('&', config.getString("chat.highlight-color"));
//      msgColor = ChatColor.translateAlternateColorCodes('&', config.getString("chat.message-color"));
//      highlightForAll = config.getBoolean("chat.highlight-for-all");
//      allowPartial = config.getBoolean("chat.allow-partial");
//      highlightMuteType = config.getString("mute.highlight");
//   }
}
