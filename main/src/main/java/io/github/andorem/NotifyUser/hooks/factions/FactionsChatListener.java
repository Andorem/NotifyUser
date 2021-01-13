package io.github.andorem.notifyuser.hooks.factions;

import io.github.andorem.notifyuser.NotifyUser;
import io.github.andorem.notifyuser.notifications.ChatNotification;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.logging.Level;


public class FactionsChatListener implements Listener {
    FactionsHook hook;

    public FactionsChatListener(FactionsHook hook) {
        this.hook = hook;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private final void onChat(AsyncPlayerChatEvent e) {
        if (hook.isInFactionsChat(e.getPlayer()) && ChatNotification.canSend(e.getPlayer(), e.getMessage())) {
            callEvent(e.getPlayer(), e.getMessage(), e.getFormat());
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private final void onFactionChat(FactionsChatEvent e) {
        String messageColor = ChatColor.getLastColors(e.getFormat());
        FactionsChatNotification factionsChatNotification = new FactionsChatNotification(e, messageColor);
        factionsChatNotification.send();
    }

    public void callEvent(Player player, String message, String format) {
        try {
            Bukkit.getPluginManager().callEvent(new FactionsChatEvent(hook, player, message, format));
        } catch (EventException | IllegalStateException e) {
            hook.log(Level.WARNING, "ERROR: Could not invoke FactionsChatEvent. Is Factions installed and enabled?");
            e.printStackTrace();
        }
    }
}
