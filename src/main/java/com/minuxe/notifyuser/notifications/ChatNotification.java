package com.minuxe.notifyuser.notifications;

import com.minuxe.notifyuser.NotifyUser;
import com.minuxe.notifyuser.handlers.ConfigurationHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class ChatNotification {
    Player sender;
    AsyncPlayerChatEvent chatEvent;
    String messageColor;
    ArrayList<String> receivers = new ArrayList<>();
    String messageWithAllReceiversHighlighted;

    static String pingSymbol;
    static int minNameLengthRequired;

    static String highlightColor, defaultMessageColor;
    static boolean highlightForAll, allowPartial, muteEnabledForHighlight;
    static String highlightMuteType;

    public ChatNotification(Player sender, AsyncPlayerChatEvent chatEvent, String messageColor) {
        this.sender = sender;
        this.chatEvent = chatEvent;
        this.messageColor = messageColor;
    }

    public ChatNotification(Player sender, AsyncPlayerChatEvent chatEvent, ChatColor colorCode) {
        this(sender, chatEvent, colorCode.toString());
    }

    public ChatNotification(Player sender, AsyncPlayerChatEvent chatEvent) {
        this(sender, chatEvent, ChatNotification.defaultMessageColor);
    }

    public static boolean canSend(Player sender, String message) {
        return sender.hasPermission("NotifyUser.player.send") && message.contains(pingSymbol);
    }

    private void parseReceivers() {
        String[] splitMessage = chatEvent.getMessage().split(" ");
        NotifyUser.debug("ChatNotification: format = " + chatEvent.getFormat());
        for (int i = 0; i < splitMessage.length; i++) {
            String word = splitMessage[i];
            String wordStripped = ChatColor.stripColor(word);
            if (containsMoreThanSingleSymbol(word)) {

                // Get receiver name
                String wordWithoutPunctuation = wordStripped.replaceAll("[^a-zA-Z0-9_]", "");
                String receiverName = wordWithoutPunctuation.split(pingSymbol)[0];

//                // Get word color
//                String wordColor = messageColor;
//                NotifyUser.debug("ChatNotification: wordColor =  " + wordColor);
//                if (i == 0 && splitMessage.length > 1) {
//                    NotifyUser.debug("ChatNotification: word is first and message has more than one word");
//                    String wordAfter = splitMessage[i + 1];
//                    NotifyUser.debug("ChatNotification: wordAfter =  " + wordAfter);
//                    wordColor = getColor(wordAfter);
//                    NotifyUser.debug("ChatNotification: now wordColor =  " + wordColor);
//                } else if (i != 0) {
//                    NotifyUser.debug("ChatNotification: word is not first");
//                    String wordBefore = splitMessage[i - 1];
//                    NotifyUser.debug("ChatNotification: wordBefore =  " + wordBefore);
//                    wordColor = getColor(wordBefore);
//                    NotifyUser.debug("ChatNotification: now wordColor =  " + wordColor);
//                }

                if (isReceiverNameValid(receiverName)) {
                    receivers.add(receiverName);
                    word = highlightName(word, receiverName);
                }
            }
            messageWithAllReceiversHighlighted += word + " ";
        }
    }
    private boolean containsMoreThanSingleSymbol(String word) {
        return word.startsWith(pingSymbol) && word.length() > (pingSymbol.length() + 1);
    }

    private String getMessageWithHighlights(String receiverName, String message) {
        return highlightName(message, receiverName);
    }

    private String getMessageWithHighlights(String message) {
       String newMessage = message;
        for (String name : receivers) {
           newMessage = highlightName(newMessage, name);
        }
        return newMessage;
    }

    private String highlightName(String str, String name) {
        return str.replace(pingSymbol + name,
                highlightColor + pingSymbol + name + messageColor);
    }

    public void send() {
        parseReceivers();
        if (highlightForAll) sendToAll();
        else sendOnlyToReceiversAndOverrides();
    }

    private void sendToAll() {
        for (final Player player : chatEvent.getRecipients()) {
            if (highlightMuteType.equals("all") && !shouldBeHighlightedFor(player)) break;
            chatEvent.getRecipients().remove(player);
            String newMessage = getMessageWithHighlights(chatEvent.getMessage());
            player.sendMessage(String.format(chatEvent.getFormat(), player.getDisplayName(), newMessage));
        }
    }
    {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (shouldBeHighlightedFor(player)) {
                String newMessage = getMessageWithHighlights(player.getName(), chatEvent.getMessage());

                if (!SoundNotification.isMutedFor(player) && chatEvent.getRecipients().contains(player)) {
                    (new SoundNotification(player)).send();
                }

                chatEvent.setMessage(newMessage);
                player.sendMessage("Sending test highlightForAll message: \n" + messageWithAllReceiversHighlighted);
                player.sendMessage("Sending test message with messageColor: \n" + messageColor + "Test");
                player.sendMessage("Sending test message with defaultMessageColor: \n" + defaultMessageColor + "Test");
            }
        }
    }

    private boolean doNamesMatch(String first, String second) {
        String firstLower = first.toLowerCase();
        String secondLower = second.toLowerCase();
        return (allowPartial ? firstLower.startsWith(secondLower) : firstLower.equals(secondLower));
    }

    private boolean isReceiver(Player player) {
        if (!player.hasPermission("NotifyUser.player.receive")) return false;
        String playerName = player.getName();
        if (receivers.contains(playerName)) return true;
        else if (allowPartial) {
            for (String receiverName : receivers) {
                if (doNamesMatch(playerName, receiverName)) return true;
            }
        }
        return false;
    }

    private boolean isReceiverNameValid(String receiverName) {
        Player receiver = Bukkit.getPlayer(receiverName);
        if (receiver == null) return false;

        return checkNameLength(receiverName, receiver);
    }

    private boolean checkNameLength(String receiverName, Player receiver) {
        return allowPartial ? (receiverName.length() >= minNameLengthRequired)
                : (receiverName.length() == receiver.getName().length());
    }

    private boolean hasHighlightPermission(Player player) {
        return player.hasPermission("NotifyUser.override.highlightall")
                || player.hasPermission("NotifyUser.player.highlight");
    }

    private boolean shouldBeHighlightedFor(Player player) {
        return !isMutedFor(player) && hasHighlightPermission(player) && isReceiver(player);
    }

    private static boolean isMutedFor(Player player) {
        return muteEnabledForHighlight && Notification.isMutedFor(player);
    }

    public static void setValues(ConfigurationHandler configHandler) {
        ConfigurationSection chatConfig = configHandler.getChatConfig();
        ConfigurationSection muteConfig = configHandler.getMuteConfig();

        pingSymbol = chatConfig.getString("symbol");
        minNameLengthRequired = chatConfig.getInt("min-name-length");
        highlightColor = ChatColor.translateAlternateColorCodes('&', chatConfig.getString("highlight-color"));
        defaultMessageColor = ChatColor.translateAlternateColorCodes('&', chatConfig.getString("message-color"));
        highlightForAll = chatConfig.getBoolean("highlight-for-all");
        allowPartial = chatConfig.getBoolean("allow-partial");
        highlightMuteType = muteConfig.getString("highlight").toLowerCase();
        muteEnabledForHighlight = highlightMuteType.equals("true")
                || highlightMuteType.equals("all");
    }

    public static String getColor(String str) {
        String color = ChatColor.getLastColors(str);
        if (color == null || color.isEmpty()) color = defaultMessageColor;
        return color;
    }

    public static int getMinNameLengthRequired() {
        return minNameLengthRequired;
    }
}
