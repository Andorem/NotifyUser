package io.github.andorem.notifyuser.hooks.factions;

import io.github.andorem.notifyuser.main.NotifyUser;
import io.github.andorem.notifyuser.hooks.Hook;
import io.github.andorem.notifyuser.hooks.HookManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Set;


public abstract class FactionsHook extends Hook {
    private ArrayList<String> FACTION_PLUGINS = new ArrayList<>();
    private FactionsChatListener listener;
    protected static String CHAT_FORMAT_MOD = "CHAT_FORMAT_MOD",
                        CHAT_FORMAT_FACTION = "CHAT_FORMAT_FACTION",
                        CHAT_FORMAT_ALLIANCE = "CHAT_FORMAT_ALLY",
                        CHAT_FORMAT_TRUCE = "CHAT_FORMAT_TRUCE",
                        CHAT_FORMAT_PUBLIC = "CHAT_FORMAT_PUBLIC";

    public FactionsHook(HookManager hm) {
        super(hm, "Factions");
    }

    protected void set() {
        setListener();
    }

    protected void disable() {
        super.disable();
        HandlerList.unregisterAll(listener);
    }

    private void setListener() {
        this.listener = new FactionsChatListener(this);
        hm.getPlugin().getServer().getPluginManager().registerEvents(listener, hm.getPlugin());
    }

    public void callEvent(Player player, String message, String format) {
        try {
            Bukkit.getPluginManager().callEvent(new FactionsChatEvent(this, player,
                    message, format));
        } catch (EventException | IllegalStateException e) {
            NotifyUser.log("ERROR: Could not invoke FactionsChatEvent. Is Factions installed and enabled?", "WARNING");
            e.printStackTrace();
        }
    }

    abstract protected boolean isInFactionsChat(Player player);
    abstract protected boolean isInPublicChat(Player player);
    abstract protected boolean isInModChannel(Player player);
    abstract protected boolean isInFactionChannel(Player player);
    abstract protected boolean isInAllyChannel(Player player);
    abstract protected boolean isInTruceChannel(Player player);

    abstract protected String getChatMode(Player player);
    abstract protected String getChatFormat(Player player);
    abstract protected String getChatFormatMod();
    abstract protected String getChatFormatFaction();
    abstract protected String getChatFormatAlly();
    abstract protected String getChatFormatTruce();

    abstract protected String getFactionID(Player player);
    abstract protected String getFactionTag(Player player);

    abstract protected Set<Player> getPlayersInChannelWith(Player player);
    abstract protected Set<Player> getPlayersInChannelWith(Player player, boolean getSpies);
    abstract protected Set<Player> getSpiesInChannelWith(Player player);
    abstract protected boolean isSpyingChannelOf(Player spy, Player target);

    abstract protected boolean areInSameChannel(Player from, Player to);
    abstract protected boolean areInSameFaction(Player a, Player b);


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
