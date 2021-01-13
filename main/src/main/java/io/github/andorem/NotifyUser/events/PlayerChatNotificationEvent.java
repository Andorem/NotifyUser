package io.github.andorem.notifyuser.events;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

public class PlayerChatNotificationEvent extends AsyncPlayerChatEvent {
    public PlayerChatNotificationEvent(boolean async, Player who, String message, Set<Player> players, String format) {
        super(async, who, message, players);
        this.setFormat(format);
    }
    public PlayerChatNotificationEvent(boolean async, Player who, String message, Set<Player> players) {
        super(async, who, message, players);
    }
}
