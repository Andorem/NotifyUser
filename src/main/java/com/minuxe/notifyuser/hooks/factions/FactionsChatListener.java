package com.minuxe.notifyuser.hooks.factions;

import com.minuxe.notifyuser.NotifyUser;
import com.minuxe.notifyuser.notifications.ChatNotification;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class FactionsChatListener implements Listener {
    FactionsHook hook;

    public FactionsChatListener(FactionsHook hook) {
        this.hook = hook;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private final void onChat(AsyncPlayerChatEvent e) {
        NotifyUser.debug("FactionsChatListener low priority called");
        if (hook.isInFactionsChat(e.getPlayer()) && ChatNotification.canSend(e.getPlayer(), e.getMessage())) {
            hook.callEvent(e.getPlayer(), e.getMessage(), e.getFormat());
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private final void onFactionChat(FactionsChatEvent e) {
        NotifyUser.debug("FactionsChatListener normal priority called");

        String messageColor = ChatColor.getLastColors(e.getFormat());
        FactionsChatNotification factionsChatNotification = new FactionsChatNotification(e, messageColor);
        factionsChatNotification.send();

    }

}
