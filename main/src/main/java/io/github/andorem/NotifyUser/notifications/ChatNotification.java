package io.github.andorem.notifyuser.notifications;

import io.github.andorem.notifyuser.handlers.ConfigurationHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class ChatNotification {
    private Player sender;
    private String message;
    private String format;
    private String messageColor;
    private final Set<Player> recipients;
    private ArrayList<String> receivers = new ArrayList<>();
    private AsyncPlayerChatEvent chatEvent = null;

    static String pingSymbol;
    static int minNameLengthRequired;

    static String highlightColor, defaultMessageColor;
    static boolean highlightForAll, allowPartial, muteEnabledForHighlight;
    protected static String highlightMuteType;

    public ChatNotification(Player sender, String message, String format, String messageColor, Set<Player> recipients) {
        this.sender = sender;
        this.messageColor = messageColor;
        this.message = message;
        this.format = format;
        this.recipients = recipients;
    }

    public ChatNotification(Player sender, AsyncPlayerChatEvent chatEvent, String messageColor, Set<Player> recipients) {
        this.sender = sender;
        this.chatEvent = chatEvent;
        this.message = chatEvent.getMessage();
        this.format = chatEvent.getFormat();
        this.messageColor = messageColor;
        this.recipients = recipients;
    }

    public ChatNotification(Player sender, String message, String format, ChatColor colorCode, Set<Player> recipients) {
        this(sender, message, format, colorCode.toString(), recipients);
    }

    public ChatNotification(Player sender, String message, String format, Set<Player> recipients) {
        this(sender, message, format, ChatNotification.defaultMessageColor, recipients);
    }

    public static boolean canSend(Player sender, String message) {
        return sender.hasPermission("NotifyUser.player.send") && message.contains(pingSymbol);
    }

    private void parseReceivers() {
        String[] splitMessage = message.split(" ");
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
        }
    }
    private boolean containsMoreThanSingleSymbol(String word) {
        return word.startsWith(pingSymbol) && word.length() > (pingSymbol.length() + 1);
    }

    protected String getMessageWithHighlights(String receiverName, String message) {
        return highlightName(message, receiverName);
    }

    protected String getMessageWithHighlights(String message) {
       String newMessage = message;
        for (String name : receivers) {
           newMessage = highlightName(newMessage, name);
        }
        return newMessage;
    }

    private String highlightName(String str, String name) {
        return str.replaceAll("(?i)" + pingSymbol + name,
                highlightColor + pingSymbol + name + messageColor);
    }

    public boolean send() {
        parseReceivers();
        if (highlightForAll) sendToAll();
        else sendOnlyToReceiversAndOverrides();

        return highlightForAll;
    }

    protected void sendToAll() {
        for (final Player player : recipients) {
            if (highlightMuteType.equals("all") && !shouldBeHighlightedFor(player)) continue; // player has all chat notifications muted and/or no highlight permissions

            message = getMessageWithHighlights(message);
            if (!(chatEvent == null)) chatEvent.setMessage(message);
            if (isReceiver(player)) playSound(player);
        }
    }

    protected void sendOnlyToReceiversAndOverrides() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (!shouldBeHighlightedFor(player)) continue;

            recipients.remove(player);
            String newMessage = getMessageWithHighlights(player.getName(), message);
            player.sendMessage(String.format(format, sender.getDisplayName(), newMessage));

            if (isReceiver(player)) playSound(player);
        }
    }

    protected void playSound(Player player) {
        if (!SoundNotification.isMutedFor(player)) {
            (new SoundNotification(player)).send();
        }
    }

    private boolean doNamesMatch(String first, String second) {
        String firstLower = first.toLowerCase();
        String secondLower = second.toLowerCase();
        return (allowPartial ? firstLower.startsWith(secondLower) : firstLower.equals(secondLower));
    }

    protected boolean isReceiver(Player player) {
        if (!player.hasPermission("NotifyUser.player.receive")) return false;
        String playerName = player.getName().toLowerCase();
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

    protected boolean shouldBeHighlightedFor(Player player) {
        boolean notMutedAndHasPermissions = !isMutedFor(player) && hasHighlightPermission(player);
        if (highlightForAll) return notMutedAndHasPermissions;       // chat highlights enabled for everyone
        else return notMutedAndHasPermissions && isReceiver(player); // chat highlights only enabled for the people being tagged (or has override)
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

    protected Player getSender() {
        return sender;
    }

    protected String getMessageColor() {
        return messageColor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    protected ArrayList<String> getReceivers() {
        return receivers;
    }

    protected static String getPingSymbol() {
        return pingSymbol;
    }

    protected static String getHighlightColor() {
        return highlightColor;
    }

    protected static String getDefaultMessageColor() {
        return defaultMessageColor;
    }

    protected static boolean isHighlightedForAll() {
        return highlightForAll;
    }

    public static boolean isAllowPartial() {
        return allowPartial;
    }

    public static boolean isMuteEnabledForHighlight() {
        return muteEnabledForHighlight;
    }

    public static String getHighlightMuteType() {
        return highlightMuteType;
    }
}
