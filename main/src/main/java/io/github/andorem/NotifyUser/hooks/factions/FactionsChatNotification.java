package io.github.andorem.notifyuser.hooks.factions;

import io.github.andorem.notifyuser.NotifyUser;
import io.github.andorem.notifyuser.notifications.ChatNotification;
import org.bukkit.entity.Player;

public class FactionsChatNotification extends ChatNotification {
    FactionsChatEvent chatEvent;
    FactionsHook hook;
    public FactionsChatNotification(FactionsChatEvent chatEvent, String messageColor) {
        super(chatEvent.getPlayer(), chatEvent.getMessage(), chatEvent.getFormat(), messageColor);
        this.hook = chatEvent.getHook();
        this.chatEvent = chatEvent;
    }

    @Override
    protected void sendToAll() {
        String newMessage = getMessageWithHighlights(getReceivers(), getMessage());
        String originalMessage = getMessage();

        for (Player player : chatEvent.getRecipients()) {
            boolean isSpy = chatEvent.getSpyRecipients().contains(player);
            String prefix = (isSpy ? getSpyTag() : "");
            if (ChatNotification.highlightMuteType.equals("all") || !shouldBeHighlightedFor(player)) { // player has all chat notifications muted and/or no highlight permissions
                player.sendMessage(prefix + originalMessage); // send original message
            }
            else {
                player.sendMessage(prefix + newMessage); // send new faction channel message with highlights
                if (isReceiver(player)) playSound(player);
            }
        }
//        for (Player player : chatEvent.getChatRecipients()) {
//            if (ChatNotification.highlightMuteType.equals("all") || !shouldBeHighlightedFor(player)) { // player has all chat notifications muted and/or no highlight permissions
//                player.sendMessage(getMessage()); // send original message
//            }
//            else {
//                String newMessage = getMessageWithHighlights(getReceivers(), getMessage());
//                if (chatEvent.getSpyRecipients().contains(player)) {
//                    newMessage = getSpyTag() + newMessage;
//                    player.sendMessage(newMessage); // send message as a spy
//                }
//                else {
//                    player.sendMessage(newMessage); // send regular faction channel message with highlights
//                }
//                if (isReceiver(player)) playSound(player);
//            }
//        }
    }

    @Override
    protected void sendOnlyToReceiversAndOverrides() {
        String originalMessage = getMessage();

        NotifyUser.debug("sendOnly: chatEvent.getRecipients length = " + chatEvent.getRecipients().size());
        for (Player player : chatEvent.getRecipients()) {
            NotifyUser.debug("sendOnlyToReceiversAndOverrides: player = " + player.getName());
            boolean isSpy = chatEvent.getSpyRecipients().contains(player);
            String prefix = (isSpy ? getSpyTag() : "");
            if (!shouldBeHighlightedFor(player)) { // player has all chat notifications muted and/or no highlight permissions
                 player.sendMessage(prefix + originalMessage);
            }
            else {
                String newMessage = getMessageWithHighlight(player.getName(), getMessage());
                player.sendMessage(prefix + newMessage);
                if (isReceiver(player)) playSound(player);
            }
        }
    }

    private String getSpyTag() {
        String chatAbbreviation = chatEvent.getChatMode().substring(0, 1).toUpperCase();
        return "[" + chatAbbreviation + "C" + "spy]: ";
    }



}
