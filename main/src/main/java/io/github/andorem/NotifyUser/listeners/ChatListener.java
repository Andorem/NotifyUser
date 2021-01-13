package io.github.andorem.notifyuser.listeners;

import java.util.*;

import io.github.andorem.notifyuser.events.PlayerChatNotificationEvent;
import io.github.andorem.notifyuser.notifications.ChatNotification;
import io.github.andorem.notifyuser.NotifyUser;
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

   String pingSymbol;

   public ChatListener(NotifyUser plugin) {
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
      if (e instanceof PlayerChatNotificationEvent) return;
      Player thisPlayer = e.getPlayer();
      if (ChatNotification.canSend(thisPlayer, e.getMessage())) {
         String messageColor = ChatColor.getLastColors(e.getFormat());
         ChatNotification chatNotification = new ChatNotification(thisPlayer, e, messageColor);
         chatNotification.send();
//         if (notifyAll) e.setMessage(chatNotification.getMessage());
      }
   }
}
