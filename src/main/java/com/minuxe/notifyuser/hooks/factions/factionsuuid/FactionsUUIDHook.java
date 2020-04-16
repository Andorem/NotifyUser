package com.minuxe.notifyuser.hooks.factions.factionsuuid;

import com.massivecraft.factions.*;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.ChatMode;
import com.minuxe.notifyuser.NotifyUser;
import com.minuxe.notifyuser.hooks.HookManager;
import com.minuxe.notifyuser.hooks.factions.FactionsHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.massivecraft.factions.struct.ChatMode.*;


public class FactionsUUIDHook extends FactionsHook {
    private MainConfig.Factions.Chat chatValues = FactionsPlugin.getInstance().conf().factions().chat();

    public FactionsUUIDHook(HookManager hm) {
        super(hm);
    }

    @Override
    protected void set() {
        super.set();
    }

    private FPlayer getFactionsPlayer(Player player) {
        return FPlayers.getInstance().getByPlayer(player);
    }

    @Override
    protected boolean isInFactionsChat(Player player) {
        return isInFactionsChat(getFactionsPlayer(player));
    }

    private boolean isInFactionsChat(FPlayer fPlayer) {
       return fPlayer.getChatMode() != PUBLIC;
    }

    @Override
    protected boolean isInPublicChat(Player player) {
        return getChatModeVal(player) == PUBLIC;
    }
    @Override
    protected boolean isInModChannel(Player player) {
        return getChatModeVal(player) == MOD;
    }
    @Override
    protected boolean isInFactionChannel(Player player) {
        return getChatModeVal(player) == FACTION;
    }
    @Override
    protected boolean isInAllyChannel(Player player) {
        return getChatModeVal(player) == ALLIANCE;
    }
    @Override
    protected boolean isInTruceChannel(Player player) {
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

    private String getChatModeName(ChatMode chatMode) {
        return chatMode.name();
    }

    @Override
    protected String getChatMode(Player player) {
        return getChatModeFor(getFactionsPlayer(player));
    }

    @Override
    protected String getChatFormat(Player player) {
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

    @Override
    protected String getChatFormatMod() {
        return chatValues.getModChatFormat();
    }

    @Override
    protected String getChatFormatFaction() {
        return chatValues.getFactionChatFormat();
    }

    @Override
    protected String getChatFormatAlly() {
        return chatValues.getAllianceChatFormat();
    }

    @Override
    protected String getChatFormatTruce() {
        return chatValues.getTruceChatFormat();
    }

    @Override
    protected Set<Player> getPlayersInChannelWith(Player player) {
        return getPlayersInChannelWith(player, false);
    }

    protected Set<Player> getPlayersInChannelWith(Player player, boolean getSpies) {
        FPlayer fPlayer = getFactionsPlayer(player);
        ChatMode chatMode = getChatModeVal(player);
        Set<Player> checkForPlayersIn = (chatMode == MOD || chatMode == FACTION) ?
                new HashSet<>(fPlayer.getFaction().getOnlinePlayers()) : new HashSet<>(Bukkit.getOnlinePlayers());

        Set<Player> players = new HashSet<>();
        for (Player anotherPlayer : checkForPlayersIn) {
            if (areInSameChannel(player, anotherPlayer)) players.add(anotherPlayer);
            if (getSpies && isSpyingChannelOf(anotherPlayer, player)) players.add(anotherPlayer);
        }
        return players;
    }

    @Override
    protected Set<Player> getSpiesInChannelWith(Player player) {
        Set<Player> spies = new HashSet<>();
        for (Player anotherPlayer : Bukkit.getOnlinePlayers()) {
            if (isSpyingChannelOf(anotherPlayer, player)) spies.add(anotherPlayer);
        }
        return spies;
    }

    @Override
    protected boolean areInSameChannel(Player from, Player to) {
        FPlayer fromF = getFactionsPlayer(from);
        FPlayer toF = getFactionsPlayer(to);
        Faction factionPlayerFrom = fromF.getFaction();
        Faction factionPlayerTo = toF.getFaction();
        switch(fromF.getChatMode()) {
            case MOD:
                return areInSameFaction(fromF, toF) && toF.getRole().isAtLeast(Role.MODERATOR);
            case FACTION:
                return areInSameFaction(fromF, toF);
            case ALLIANCE:
                return (factionPlayerFrom.getRelationTo(toF) == Relation.ALLY) && !toF.isIgnoreAllianceChat();
            case TRUCE:
                return factionPlayerFrom.getRelationTo(toF) == Relation.TRUCE;
            default:
                return false;
        }
    }

    @Override
    protected boolean isSpyingChannelOf(Player spy, Player target) {
        FPlayer fSpy = getFactionsPlayer(spy);
        FPlayer fTarget = getFactionsPlayer(target);
        return fSpy.isSpyingChat() && !areInSameFaction(fSpy, fTarget) && fSpy != fTarget;
    }

    private Faction getFaction(Player player) {
        return getFactionsPlayer(player).getFaction();
    }

    @Override
    protected String getFactionID(Player player) {
        return getFaction(player).getId();
    }

    @Override
    protected String getFactionTag(Player player) {
        FPlayer fPlayer = getFactionsPlayer(player);
        if (fPlayer.getChatMode() == ChatMode.FACTION) return fPlayer.describeTo(fPlayer.getFaction());
        else return fPlayer.getNameAndTag();
    }

    protected boolean areInSameFaction(FPlayer a, FPlayer b) {
        return a.getFaction() == b.getFaction() && a != b;
    }

    @Override
    protected boolean areInSameFaction(Player a, Player b) {
        return areInSameFaction(getFactionsPlayer(a), getFactionsPlayer(b));
    }
}
