package io.github.andorem.notifyuser.notifications;

import com.google.common.collect.Sets;
import io.github.andorem.notifyuser.NotifyUser;
import io.github.andorem.notifyuser.events.PlayerChatNotificationEvent;
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
    private ArrayList<String> receivers = new ArrayList<>();
    private AsyncPlayerChatEvent chatEvent = null;

    static String pingSymbol;
    static int minNameLengthRequired;

    static String highlightColor, defaultMessageColor;
    static boolean highlightForAll, allowPartial, muteEnabledForHighlight;
    protected static String highlightMuteType;

    public ChatNotification(Player sender, String message, String format, String messageColor) {
        this.sender = sender;
        this.messageColor = messageColor;
        this.message = message;
        this.format = format;
    }

    public ChatNotification(Player sender, AsyncPlayerChatEvent chatEvent, String messageColor) {
        this.sender = sender;
        this.chatEvent = chatEvent;
        this.message = chatEvent.getMessage();
        this.format = chatEvent.getFormat();
        this.messageColor = messageColor;
    }

    public ChatNotification(Player sender, String message, String format, ChatColor colorCode) {
        this(sender, message, format, colorCode.toString());
    }

    public ChatNotification(Player sender, String message, String format) {
        this(sender, message, format, ChatNotification.defaultMessageColor);
    }

    public static boolean canSend(Player sender, String message) {
        return sender.hasPermission("notifyuser.player.send") && message.contains(pingSymbol);
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

                if (isReceiverNameValid(receiverName)) {
                    receivers.add(receiverName);
                }
            }
        }
    }
    private boolean containsMoreThanSingleSymbol(String word) {
        return word.startsWith(pingSymbol) && word.length() > (pingSymbol.length() + 1);
    }

    protected String getMessageWithHighlight(String receiverName, String message) {
        return highlightName(message, getReceiverMatch(receiverName));
    }

    protected String getMessageWithHighlights(ArrayList<String> receiverNames, String message) {
       String newMessage = message;
        for (String name : receiverNames) {
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
        String newMessage = getMessageWithHighlights(receivers, message); // default to all names highlighted
        Set<Player> removeRecipients = new HashSet<>();
        for (final Player player : chatEvent.getRecipients()) {
            if (isReceiver(player)) playSound(player);

            if (!shouldBeHighlightedFor(player)) {
                removeRecipients.add(player);
                if (highlightMuteType.equals("all")) {
                    // Player has all highlights muted, send regular message
                    newMessage = getMessage();
                }
                else if (highlightMuteType.equals("true")) {
                    // Player has only themselves' muted, send custom message without player's name highlighted
                    ArrayList<String> newReceivers = receivers;
                    newReceivers.remove(getReceiverMatch(player.getName()));
                    newMessage = getMessageWithHighlights(newReceivers, message);
                }

                // Remove from list of recipients to send default message to
                removeRecipients.add(player);

                // Send custom chat event with individualized message
                AsyncPlayerChatEvent newChatEvent = new PlayerChatNotificationEvent(true, sender, newMessage, Sets.newHashSet(player), chatEvent.getFormat());
                Bukkit.getPluginManager().callEvent(newChatEvent);
            }
//            if (!(chatEvent == null)) chatEvent.setMessage(message);
        }
        if (chatEvent != null) {
            chatEvent.getRecipients().removeAll(removeRecipients); // modified original event excludes those muting highlights
            chatEvent.setMessage(getMessageWithHighlights(receivers, message));
        }
    }

    protected void sendOnlyToReceiversAndOverrides() {
        Set<Player> newRecipients = new HashSet<>();

        // Chat event messages must be individualized for each recipient (e.g. highlights only their name)
        for (Player player : chatEvent.getRecipients()) {
            if (isReceiver(player)) playSound(player);

            if (!shouldBeHighlightedFor(player)) continue;

            newRecipients.add(player);
            String newMessage = isOverride(player) ? getMessageWithHighlights(receivers, message) : getMessageWithHighlight(player.getName(), message); // overrides receive all notifications, regardless of name

            AsyncPlayerChatEvent newChatEvent = new PlayerChatNotificationEvent(true, sender, newMessage, Sets.newHashSet(player), chatEvent.getFormat());
            Bukkit.getPluginManager().callEvent(newChatEvent);
        }

        if (chatEvent != null) chatEvent.getRecipients().removeAll(newRecipients); // modified original event excludes those highlighted

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

    private String getReceiverMatch(String playerName) {
        if (receivers.contains(playerName)) return playerName;
        else if (allowPartial) {
            for (String receiverName : receivers) {
                if (doNamesMatch(playerName, receiverName)) return receiverName;
            }
        }
        return null;
    }

    protected boolean isReceiver(Player player) {
        if (!player.hasPermission("notifyuser.player.receive")) return false;
        String playerName = player.getName().toLowerCase();
        if (receivers.contains(playerName)) return true;
        else if (allowPartial) {
            for (String receiverName : receivers) {
                if (doNamesMatch(playerName, receiverName)) return true;
            }
        }
        return false;
    }

    protected boolean isOverride(Player player) {
        return player.hasPermission("notifyuser.override.highlightall");
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
        return player.hasPermission("notifyuser.override.highlightall")
                || player.hasPermission("notifyuser.player.highlight");
    }

    protected boolean shouldBeHighlightedFor(Player player) {
        if (!hasHighlightPermission(player)) return false;
        else if (highlightForAll) return !isMutedFor(player);  // muting disables all name highlights
        else return !isMutedFor(player) && isReceiver(player); // muting disables only this player's highlight
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
