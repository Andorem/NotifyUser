package io.github.andorem.notifyuser.hooks.factions;

import io.github.andorem.notifyuser.main.NotifyUser;
import io.github.andorem.notifyuser.main.notifications.ChatNotification;
import org.bukkit.entity.Player;

public class FactionsChatNotification extends ChatNotification {
    FactionsChatEvent chatEvent;
    FactionsHook hook;
    public FactionsChatNotification(FactionsChatEvent chatEvent, String messageColor) {
        super(chatEvent.getPlayer(), chatEvent.getMessage(), chatEvent.getFormat(), messageColor, chatEvent.getRecipients());
        this.hook = chatEvent.getHook();
        this.chatEvent = chatEvent;
    }

    @Override
    protected void sendToAll() {
        for (Player player : chatEvent.getChatRecipients()) {
            if (highlightMuteType.equals("all") || !shouldBeHighlightedFor(player)) { // player has all chat notifications muted and/or no highlight permissions
                player.sendMessage(getMessage()); // send original message
            }
            else {
                String newMessage = getMessageWithHighlights(getMessage());
                if (chatEvent.getSpyRecipients().contains(player)) {
                    newMessage = getSpyTag() + newMessage;
                    player.sendMessage(newMessage); // send message as a spy
                }
                else {
                    player.sendMessage(newMessage); // send regular faction channel message with highlights
                }
                if (isReceiver(player)) playSound(player);
            }
        }
    }

    @Override
    protected void sendOnlyToReceiversAndOverrides() {
        NotifyUser.debug("sendOnly: chatEvent.getRecipients length = " + chatEvent.getRecipients().size());
        for (Player player : chatEvent.getRecipients()) {
            NotifyUser.debug("sendOnlyToReceiversAndOverrides: player = " + player.getName());
            if (!shouldBeHighlightedFor(player)) { // player has all chat notifications muted and/or no highlight permissions
                player.sendMessage(getMessage());
            }
            else {
                String newMessage = getMessageWithHighlights(player.getName(), getMessage());
                if (chatEvent.getSpyRecipients().contains(player)) {
                    newMessage = getSpyTag() + newMessage;
                }
                player.sendMessage(newMessage);
                if (isReceiver(player)) playSound(player);
            }
        }
    }

    private String getSpyTag() {
        String chatAbbreviation = chatEvent.getChatMode().substring(0, 1).toUpperCase();
        return "[" + chatAbbreviation + "C" + "spy]: ";
    }



}
