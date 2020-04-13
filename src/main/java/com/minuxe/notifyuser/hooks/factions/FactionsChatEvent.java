package com.minuxe.notifyuser.hooks.factions;


import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.ChatMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;


/*
    Implementation of Factions Chat player lists and chat message formatting retrieved from FactionsChatListener
    https://github.com/drtshock/Factions/blob/1.6.x/src/main/java/com/massivecraft/factions/listeners/FactionsChatListener.java
*/

public class FactionsChatEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private static String MOD_CHAT_FORMAT, FACTION_CHAT_FORMAT, ALLY_CHAT_FORMAT, TRUCE_CHAT_FORMAT;

    static {
        try {
            MOD_CHAT_FORMAT = FactionsPlugin.getInstance().conf().factions().chat().getModChatFormat();
            FACTION_CHAT_FORMAT = FactionsPlugin.getInstance().conf().factions().chat().getFactionChatFormat();
            ALLY_CHAT_FORMAT = FactionsPlugin.getInstance().conf().factions().chat().getAllianceChatFormat();
            TRUCE_CHAT_FORMAT = FactionsPlugin.getInstance().conf().factions().chat().getTruceChatFormat();
        }
        catch (NoSuchMethodError e) {
            MOD_CHAT_FORMAT = Conf.modChatFormat;
            FACTION_CHAT_FORMAT = Conf.factionChatFormat;
            ALLY_CHAT_FORMAT = Conf.allianceChatFormat;
            TRUCE_CHAT_FORMAT = Conf.truceChatFormat;
        }
    }


    private Faction faction;
    private FPlayer fPlayer;
    private ChatMode chatMode;
    private String factionDisplay;
    private Player player;
    private String message;
    private String format;
    private Set<FPlayer> recipients = new HashSet<>();
    private Set<FPlayer> chatRecipients = new HashSet<>();;
    private Set<FPlayer> spyRecipients = new HashSet<>();;

    private boolean cancelled = false;
    public FactionsChatEvent(Player player, String message, String format) throws EventException {
        this.fPlayer = FPlayers.getInstance().getByPlayer(player);
        this.chatMode = fPlayer.getChatMode();
        if (chatMode == ChatMode.PUBLIC) throw new EventException("Cannot call FactionsChatEvent via Public ChatMode.");

        this.player = player;
        this.fPlayer = FPlayers.getInstance().getByPlayer(player);
        this.faction = fPlayer.getFaction();

        this.recipients = findRecipients();
        this.factionDisplay = findFactionDisplay();
        this.format = findFormat(format);
        this.message = formatMessage(message);
    }

    private String formatMessage(String message) {
        String newMessage = String.format(format, factionDisplay + ": ", message);
        return newMessage;
    }

    private String findFormat(String defaultFormat) {
        switch(chatMode) {
            case MOD:
                return MOD_CHAT_FORMAT;
            case FACTION:
                return FACTION_CHAT_FORMAT;
            case ALLIANCE:
                return ALLY_CHAT_FORMAT;
            case TRUCE:
                return TRUCE_CHAT_FORMAT;
            default:
                return defaultFormat;
        }
    }

    private String findFactionDisplay() {
        if (chatMode == ChatMode.FACTION) return fPlayer.describeTo(faction);
        else return fPlayer.getNameAndTag();
    }

    private Set<FPlayer> findRecipients() {
        if (chatMode == ChatMode.FACTION) chatRecipients = faction.getFPlayersWhereOnline(true);

        Set<FPlayer> recipients = new HashSet<>();
        for (FPlayer anotherFPlayer : FPlayers.getInstance().getOnlinePlayers()) {
            if (isPlayerInChat(anotherFPlayer)) chatRecipients.add(anotherFPlayer);
            if (isPlayerSpy(anotherFPlayer)) spyRecipients.add(anotherFPlayer);
        }
        recipients.addAll(chatRecipients);
        recipients.addAll(spyRecipients);
        return recipients;
    }

    private boolean isPlayerInChat(FPlayer fp) {
        switch(chatMode) {
            case MOD:
                return faction == fp.getFaction() && fp.getRole().isAtLeast(Role.MODERATOR);
            case FACTION:
                return faction == fp.getFaction();
            case ALLIANCE:
                return (faction.getRelationTo(fp) == Relation.ALLY) && !fp.isIgnoreAllianceChat();
            case TRUCE:
                return faction.getRelationTo(fp) == Relation.TRUCE;
            default:
                return false;
        }
    }

    private boolean isPlayerSpy(FPlayer fp) {
        return fp.isSpyingChat() && fp.getFaction() != faction && this.fPlayer != fp;
    }

    public Faction getFaction() {
        return this.faction;
    }

    public FPlayer getFactionPlayer() {
        return fPlayer;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public ChatMode getChatMode() {
        return chatMode;
    }

    public String getFormat() {
        return this.format;
    }

    public Set<Player> getRecipientsAsPlayers() {
        Set<Player> players = new HashSet<>();
        for (FPlayer recipient : recipients) {
            players.add(recipient.getPlayer());
        }
        return players;
    }

    public Set<FPlayer> getRecipients() {
        return recipients;
    }

    public Set<FPlayer> getChatRecipients() {
        return chatRecipients;
    }

    public Set<FPlayer> getSpyRecipients() {
        return spyRecipients;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
