package io.github.andorem.notifyuser.hooks.factions.factionsuuid;


import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.ChatMode;
import io.github.andorem.notifyuser.hooks.factions.FactionsHook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

import static com.massivecraft.factions.struct.ChatMode.*;


public class FactionsUUIDHook extends FactionsHook {
    private MainConfig.Factions.Chat chatValues = FactionsPlugin.getInstance().conf().factions().chat();

    public FactionsUUIDHook(Plugin plugin, ConfigurationSection hooksConfig) {
        super(plugin, hooksConfig);
    }

    private FPlayer getFactionsPlayer(Player player) {
        return FPlayers.getInstance().getByPlayer(player);
    }

    @Override
    public Set<Player> getPlayersInChannelWith(Player player) {
        return getPlayersInChannelWith(player, false);
    }

    public Set<Player> getPlayersInChannelWith(Player player, boolean getSpies) {
        FPlayer fPlayer = getFactionsPlayer(player);
        ChatMode chatMode = getChatModeVal(player);
        Set<Player> checkForPlayersIn = (chatMode == MOD || chatMode == FACTION) ?
                new HashSet<>(fPlayer.getFaction().getOnlinePlayers()) : new HashSet<>(Bukkit.getOnlinePlayers());

        Set<Player> players = new HashSet<>();
        players.add(player);
        for (Player anotherPlayer : checkForPlayersIn) {
            if (areInSameChannel(player, anotherPlayer)) players.add(anotherPlayer);
            if (getSpies && isSpyingChannelOf(anotherPlayer, player)) players.add(anotherPlayer);
        }
        return players;
    }

    @Override
    public Set<Player> getSpiesInChannelWith(Player player) {
        Set<Player> spies = new HashSet<>();
        for (Player anotherPlayer : Bukkit.getOnlinePlayers()) {
            if (isSpyingChannelOf(anotherPlayer, player)) spies.add(anotherPlayer);
        }
        return spies;
    }

    @Override
    public boolean areInSameChannel(Player from, Player to) {
        FPlayer fromF = getFactionsPlayer(from);
        FPlayer toF = getFactionsPlayer(to);
        Faction factionPlayerFrom = fromF.getFaction();
        boolean channelStatus;
        switch(fromF.getChatMode()) {
            case MOD:
                channelStatus = areInSameFaction(fromF, toF) && toF.getRole().isAtLeast(Role.MODERATOR);
                break;
            case FACTION:
                channelStatus = areInSameFaction(fromF, toF);
                break;
            case ALLIANCE:
                channelStatus = areInSameFaction(fromF, toF) || (factionPlayerFrom.getRelationTo(toF) == Relation.ALLY && !toF.isIgnoreAllianceChat());
                break;
            case TRUCE:
                channelStatus = areInSameFaction(fromF, toF) || factionPlayerFrom.getRelationTo(toF) == Relation.TRUCE;
                break;
            default:
                channelStatus = false;
        }
        return channelStatus;
    }

    @Override
    public boolean isSpyingChannelOf(Player spy, Player target) {
        FPlayer fSpy = getFactionsPlayer(spy);
        FPlayer fTarget = getFactionsPlayer(target);
        return fSpy.isSpyingChat() && !areInSameFaction(fSpy, fTarget) && fSpy != fTarget;
    }

    @Override
    public boolean isInFactionsChat(Player player) {
        return isInFactionsChat(getFactionsPlayer(player));
    }
    private boolean isInFactionsChat(FPlayer fPlayer) {
        return fPlayer.getChatMode() != PUBLIC;
    }
    @Override
    public boolean isInPublicChat(Player player) {
        return getChatModeVal(player) == PUBLIC;
    }
    @Override
    public boolean isInModChannel(Player player) {
        return getChatModeVal(player) == MOD;
    }
    @Override
    public boolean isInFactionChannel(Player player) {
        return getChatModeVal(player) == FACTION;
    }
    @Override
    public boolean isInAllyChannel(Player player) {
        return getChatModeVal(player) == ALLIANCE;
    }
    @Override
    public boolean isInTruceChannel(Player player) {
        return getChatModeVal(player) == TRUCE;
    }

    private ChatMode getChatModeVal(Player player) {
        return getFactionsPlayer(player).getChatMode();
    }
    private String getChatModeFor(FPlayer fPlayer) {
        return fPlayer.getChatMode().name();
    }
    private String getChatModeFor(Player player) {
        return getChatModeFor(getFactionsPlayer(player));
    }

    @Override
    public String getChatFormat(Player player) {
        ChatMode chatMode = getChatModeVal(player);
        switch(chatMode) {
            case MOD:
                return getChatFormatMod();
            case FACTION:
                return getChatFormatFaction();
            case ALLIANCE:
                return getChatFormatAlly();
            case TRUCE:
                return getChatFormatTruce();
            default:
                return null;
        }
    }
    private String getChatModeName(ChatMode chatMode) {
        return chatMode.name();
    }
    @Override
    public String getChatMode(Player player) {
        return getChatModeFor(getFactionsPlayer(player));
    }
    @Override
    public String getChatFormatMod() {
        return chatValues.getModChatFormat();
    }
    @Override
    public String getChatFormatFaction() {
        return chatValues.getFactionChatFormat();
    }
    @Override
    public String getChatFormatAlly() {
        return chatValues.getAllianceChatFormat();
    }
    @Override
    public String getChatFormatTruce() {
        return chatValues.getTruceChatFormat();
    }

    private Faction getFaction(Player player) {
        return getFactionsPlayer(player).getFaction();
    }
    @Override
    public String getFactionID(Player player) {
        return getFaction(player).getId();
    }
    @Override
    public String getFactionTag(Player player) {
        FPlayer fPlayer = getFactionsPlayer(player);
        if (fPlayer.getChatMode() == FACTION) return fPlayer.describeTo(fPlayer.getFaction());
        else return fPlayer.getNameAndTag();
    }

    public boolean areInSameFaction(FPlayer a, FPlayer b) {
        return a.getFaction() == b.getFaction() && a != b;
    }
    @Override
    public boolean areInSameFaction(Player a, Player b) {
        return areInSameFaction(getFactionsPlayer(a), getFactionsPlayer(b));
    }
}
