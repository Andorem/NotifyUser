package com.minuxe.notifyuser.hooks.factions;

import com.massivecraft.factions.FPlayer;
import com.minuxe.notifyuser.hooks.factions.FactionsChatEvent;
import com.minuxe.notifyuser.hooks.factions.FactionsHook;
import com.minuxe.notifyuser.notifications.ChatNotification;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class FactionsChatNotification extends ChatNotification {
    FactionsChatEvent chatEvent;
    FactionsHook hook;
    public FactionsChatNotification(FactionsChatEvent chatEvent, String messageColor) {
        super(chatEvent.getPlayer(), chatEvent.getMessage(), chatEvent.getFormat(), messageColor, chatEvent.getRecipients());
        this.hook = chatEvent.getHook();
    }

    @Override
    protected void sendToAll() {
        for (Player player : chatEvent.getChatRecipients()) {
            if (highlightMuteType.equals("all") && !shouldBeHighlightedFor(player)) continue; // player has all chat notifications muted and/or no highlight permissions

            String newMessage = getMessageWithHighlights(getMessage());
            if (chatEvent.getSpyRecipients().contains(player)) {
                newMessage = getSpyTag() + newMessage;
            }
            player.sendMessage(newMessage);
            if (isReceiver(player)) playSound(player);
        }
    }

    @Override
    protected void sendOnlyToReceiversAndOverrides() {
        for (Player fPlayer : chatEvent.getRecipients()) {
            Player player = fPlayer.getPlayer();
            if (!shouldBeHighlightedFor(player)) continue; // player has all chat notifications muted and/or no highlight permissions

            String newMessage = getMessageWithHighlights(player.getName(), getMessage());
            if (chatEvent.getSpyRecipients().contains(fPlayer)) {
                newMessage = getSpyTag() + newMessage;
            }
            player.sendMessage(newMessage);
            if (isReceiver(player)) playSound(player);
        }
    }

    private String getSpyTag() {
        String chatAbbreviation = chatEvent.getChatMode().substring(0, 1).toUpperCase();
        return "[" + chatAbbreviation + "C" + "spy]: ";
    }



}
