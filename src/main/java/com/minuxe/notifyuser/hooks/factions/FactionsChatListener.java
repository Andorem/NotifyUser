package com.minuxe.notifyuser.hooks.factions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.ChatMode;
import com.minuxe.notifyuser.NotifyUser;
import com.minuxe.notifyuser.notifications.ChatNotification;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
    private final void onFactionChat(AsyncPlayerChatEvent e) {
        Player thisPlayer = e.getPlayer();
        if (isInFactionChat(thisPlayer)) {
            NotifyUser.debug("FactionsChatListener: " + thisPlayer.getName() + " is in faction chat.");
            if (ChatNotification.canSend(thisPlayer, e.getMessage())) {
                NotifyUser.debug("FactionsChatListener: " + thisPlayer.getName() + " initializing chat notification. ");
                (new ChatNotification(thisPlayer, e, ChatColor.WHITE)).send();
            }
        }
    }

    private boolean isInFactionChat(Player player) {
        switch (hook.getPluginName()) {
            case "Factions":
                return isInFactionChat_Factions(player);
            default:
                return false;
        }
    }

    private boolean isInFactionChat_Factions(Player player) {
        FPlayer factionPlayer = FPlayers.getInstance().getByPlayer(player);
        ChatMode chatMode = factionPlayer.getChatMode();
        NotifyUser.debug("FactionsChatListener: " + player.getName() + " is in chat mode " + chatMode.name());
        return !(chatMode == ChatMode.PUBLIC);
    }
}
