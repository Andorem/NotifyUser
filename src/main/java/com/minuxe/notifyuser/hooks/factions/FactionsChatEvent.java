package com.minuxe.notifyuser.hooks.factions;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Set;


/*
    Implementation of Factions Chat player lists and chat message formatting retrieved from FactionsChatListener
    https://github.com/drtshock/Factions/blob/1.6.x/src/main/java/com/massivecraft/factions/listeners/FactionsChatListener.java
*/

public class FactionsChatEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private FactionsHook hook;
    private String chatMode;
    private String factionID, factionTag;
    private Player player;
    private String message;
    private String format;
    private Set<Player> recipients;
    private Set<Player> chatRecipients;
    private Set<Player> spyRecipients;

    private boolean cancelled = false;
    public FactionsChatEvent(FactionsHook hook, Player player, String message, String format) throws EventException {
        super(true);
        this.hook = hook;
        this.player = player;

        if (hook.isInPublicChat(player)) throw new EventException("WARN: Cannot call FactionsChatEvent via Public ChatMode.");

        this.format = hook.getChatFormat(player);
        if (this.format == null) {
            throw new EventException("ERR: Could not find Factions Chat formats! Is Factions installed?");
        }

        this.recipients = hook.getPlayersInChannelWith(player);
        this.chatRecipients = hook.getPlayersInChannelWith(player, false);
        this.spyRecipients = hook.getSpiesInChannelWith(player);
        this.factionTag = hook.getFactionTag(player);
        this.factionID = hook.getFactionID(player);
        this.chatMode = hook.getChatMode(player);
        this.message = formatMessage(message);
    }

    public Player getPlayer() { return player; };

    private String formatMessage(String message) {
        return String.format(format, factionTag + ": ", message);
    }

    public String getFactionID() {
        return factionID;
    }

    public String getMessage() {
        return message;
    }

    public String getChatMode() {
        return chatMode;
    }

    public String getFormat() {
        return this.format;
    }

    public Set<Player> getRecipients() {return this.recipients;};

    public Set<Player> getChatRecipients() {
        return chatRecipients;
    }

    public Set<Player> getSpyRecipients() {
        return spyRecipients;
    }

    public FactionsHook getHook() { return hook; }

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
