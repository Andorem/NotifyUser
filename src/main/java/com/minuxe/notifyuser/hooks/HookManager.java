package com.minuxe.notifyuser.hooks;

import com.massivecraft.factions.Factions;
import com.minuxe.notifyuser.NotifyUser;
import com.minuxe.notifyuser.hooks.factions.FactionsHook;
import com.minuxe.notifyuser.hooks.factions.factionsuuid.FactionsUUIDHook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class HookManager {
    NotifyUser plugin;
    ConfigurationSection hooksConfig;
    ArrayList<Hook> hooks;

    public HookManager(NotifyUser plugin) {
        this.plugin = plugin;
        hooksConfig = plugin.getConfigHandler().getHooksConfig();
        NotifyUser.debug("HookManager: Initializing hooks...");
        if (NotifyUser.debugEnabled()) {
            NotifyUser.debug("hooksConfig values: ");
            for (String key : hooksConfig.getKeys(true)) {
                NotifyUser.debug(key + " = " + hooksConfig.getBoolean(key));
            }
        }
        initHooks();
    }

    private void initHooks() {
        hooks = new ArrayList<>();
        initFactionsHook();
    }

    private void initFactionsHook() {
        NotifyUser.debug("HookManager: Initializing Factions Hook...");
        FactionsHook factionsHook = new FactionsUUIDHook(this);
        hooks.add(factionsHook);
    }

    public void enableHooks() {
        NotifyUser.debug("HookManager: Enabling hooks...");
        for (Hook hook : hooks) {
            NotifyUser.debug("HookManager: Enabling " + hook.getName() + " hook...");
            boolean isEnabled = hook.enable();
            if (isEnabled) NotifyUser.log(hook.getName() + "Hook enabled!", "INFO");
        }
    }

    public ArrayList<Hook> getHooks() {
        return this.hooks;
    }
    public NotifyUser getPlugin() { return this.plugin; }

    public ConfigurationSection getHooksConfig() {
        return this.hooksConfig;
    }

}
