package io.github.andorem.notifyuser.main.notifications;

import io.github.andorem.notifyuser.main.NotifyUser;
import io.github.andorem.notifyuser.main.handlers.DataHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Notification {
    static DataHandler<ArrayList<UUID>> muteHandler;
    NotifyUser plugin;

    public Notification(NotifyUser plugin) {
        this.plugin = plugin;
        muteHandler = new DataHandler<>(new ArrayList<UUID>(), "muted.dat", NotifyUser.DATA_FOLDER);
        SoundNotification.setDefaultSound();
    }

    public static void toggleMute(Player player) {
        UUID playerUUID = player.getUniqueId();
        if (muteHandler.get().contains(playerUUID)) {
            muteHandler.get().remove(playerUUID);
        } else {
            muteHandler.get().add(playerUUID);
        }
        muteHandler.save();
    }

    public static boolean isMutedFor(UUID playerUUID) {
        return getMuteHandler().get().contains(playerUUID);
    }

    public static boolean isMutedFor(Player player) {
        if (player == null) return true;
        return isMutedFor(player.getUniqueId());
    }

    public static DataHandler<ArrayList<UUID>> getMuteHandler() {
        return muteHandler;
    }

}
