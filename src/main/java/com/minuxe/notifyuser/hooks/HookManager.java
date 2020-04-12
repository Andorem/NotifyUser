package com.minuxe.notifyuser.hooks;

import com.minuxe.notifyuser.NotifyUser;
import com.minuxe.notifyuser.hooks.factions.FactionsHook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class HookManager {
    NotifyUser plugin;
    ConfigurationSection hooksConfig;
    ArrayList<Hook> hooks = new ArrayList<>();

    public HookManager(NotifyUser plugin) {
        this.plugin = plugin;
        hooksConfig = plugin.getConfigHandler().getHooksConfig();
        initHooks();
    }

    private void initHooks() {
        hooks.add(new FactionsHook(this));
        enableHooks();
    }

     public void enableHooks() {
        for (Hook hook : hooks) {
            if (hook.isEnabled()) {
                hook.set();
                NotifyUser.log("[Hook Enabled] " + hook.getPluginName() + " detected, enabled hook!", "INFO");
            }
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
