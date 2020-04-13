package com.minuxe.notifyuser.hooks.factions;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.ChatMode;
import com.minuxe.notifyuser.notifications.ChatNotification;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class FactionsChatNotification extends ChatNotification {
    FactionsChatEvent chatEvent;
    public FactionsChatNotification(FactionsChatEvent chatEvent, String messageColor) {
        super(chatEvent.getPlayer(), chatEvent.getMessage(), chatEvent.getFormat(), messageColor, chatEvent.getRecipientsAsPlayers());
    }

    public static boolean canSend(Player player, String message) {
        boolean canSend = ChatNotification.canSend(player, message);
        return canSend && FactionsHook.isInFactionsChat(player);
    }



    private void sendToAll() {
        for (final FPlayer fPlayer : chatEvent.getRecipients()) {
            Player player = fPlayer.getPlayer();
            if (highlightMuteType.equals("all") && !shouldBeHighlightedFor(player)) continue; // player has all chat notifications muted and/or no highlight permissions

            String newMessage = getMessageWithHighlights(getMessage());
            if (chatEvent.getSpyRecipients().contains(fPlayer)) {
                newMessage = getSpyTag() + newMessage;
            }
            player.sendMessage(newMessage);
            if (isReceiver(player)) playSound(player);
        }
    }

    private void sendOnlyToReceiversAndOverrides() {
        for (final FPlayer fPlayer : chatEvent.getRecipients()) {
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
        String chatAbbreviation = chatEvent.getChatMode().name().substring(0, 1).toUpperCase();
        return "[" + chatAbbreviation + "C" + "spy]: ";
    }



}
