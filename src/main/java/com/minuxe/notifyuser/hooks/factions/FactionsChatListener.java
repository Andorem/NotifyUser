package com.minuxe.notifyuser.hooks.factions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.ChatMode;
import com.minuxe.notifyuser.NotifyUser;
import com.minuxe.notifyuser.notifications.ChatNotification;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class FactionsChatListener implements Listener {
    FactionsHook hook;

    public FactionsChatListener(FactionsHook hook) {
        this.hook = hook;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private final void onChat(AsyncPlayerChatEvent e) {
        NotifyUser.debug("FactionsChatListener low priority called");
        if (FactionsHook.isInFactionsChat(e.getPlayer()) && ChatNotification.canSend(e.getPlayer(), e.getMessage())) {
            try {
                Bukkit.getPluginManager().callEvent(new FactionsChatEvent(e.getPlayer(),
                        e.getMessage(), e.getFormat()));
            } catch (EventException ex) {
                NotifyUser.log("ERROR: Could not invoke FactionsChatEvent. Is Factions hooked and enabled?", "WARNING");
                ex.printStackTrace();
            }
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private final void onFactionChat(FactionsChatEvent e) {
        NotifyUser.debug("FactionsChatListener normal priority called");

        String messageColor = ChatColor.getLastColors(e.getFormat());
        FactionsChatNotification chatNotification = new FactionsChatNotification(e, messageColor);
        chatNotification.send();


//        Player thisPlayer = e.getPlayer();
//        FPlayer factionPlayer = FPlayers.getInstance().getByPlayer(thisPlayer);
//        if (FactionsChatNotification.canSend(factionPlayer, e.getMessage())) {
//            NotifyUser.debug("FactionsChatListener: " + thisPlayer.getName() + " initializing chat notification. ");
//            (new FactionsChatNotification(factionPlayer, e, ChatColor.WHITE)).send();
//        }
    }

}
