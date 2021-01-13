package io.github.andorem.notifyuser.hooks.factions;

import io.github.andorem.notifyuser.hooks.Hook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Set;


public abstract class FactionsHook extends Hook {
    private ArrayList<String> FACTION_PLUGINS = new ArrayList<>();
    private Listener listener;
    protected static String CHAT_FORMAT_MOD = "CHAT_FORMAT_MOD",
                        CHAT_FORMAT_FACTION = "CHAT_FORMAT_FACTION",
                        CHAT_FORMAT_ALLIANCE = "CHAT_FORMAT_ALLY",
                        CHAT_FORMAT_TRUCE = "CHAT_FORMAT_TRUCE",
                        CHAT_FORMAT_PUBLIC = "CHAT_FORMAT_PUBLIC";

    public FactionsHook(Plugin plugin, ConfigurationSection hooksConfig) {
        super(plugin, "Factions", hooksConfig);
    }

    public void set() {
        if (listener == null) {
            disable();
            return;
        }
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void disable() {
        super.disable();
        if (listener != null) HandlerList.unregisterAll(listener);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }



    abstract public boolean isInFactionsChat(Player player);
    abstract public boolean isInPublicChat(Player player);
    abstract public boolean isInModChannel(Player player);
    abstract public boolean isInFactionChannel(Player player);
    abstract public boolean isInAllyChannel(Player player);
    abstract public boolean isInTruceChannel(Player player);

    abstract public String getChatMode(Player player);
    abstract public String getChatFormat(Player player);
    abstract public String getChatFormatMod();
    abstract public String getChatFormatFaction();
    abstract public String getChatFormatAlly();
    abstract public String getChatFormatTruce();

    abstract public String getFactionID(Player player);
    abstract public String getFactionTag(Player player);

    abstract public Set<Player> getPlayersInChannelWith(Player player);
    abstract public Set<Player> getPlayersInChannelWith(Player player, boolean getSpies);
    abstract public Set<Player> getSpiesInChannelWith(Player player);
    abstract public boolean isSpyingChannelOf(Player spy, Player target);

    abstract public boolean areInSameChannel(Player from, Player to);
    abstract public boolean areInSameFaction(Player a, Player b);


//    // Wrapper enum for faction chat modes
//    public enum ChatMode {
//        MOD("CHAT_MODE_MOD"),
//        FACTION("CHAT_MODE_FACTION"),
//        ALLIANCE("CHAT_MODE_ALLIANCE"),
//        TRUCE("CHAT_MODE_TRUCE"),
//        PUBLIC("CHAT_MODE_PUBLIC");
//
//        private String mode;
//        ChatMode(final String mode) {
//            this.mode = mode;
//        }
//    }

}
