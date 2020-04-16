package io.github.andorem.notifyuser.hooks;

import io.github.andorem.notifyuser.main.NotifyUser;
import io.github.andorem.notifyuser.hooks.factions.FactionsHook;
import io.github.andorem.notifyuser.hooks.factions.factionsuuid.FactionsUUIDHook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class HookManager {
    NotifyUser plugin;
    ArrayList<Hook> hooks;

    public HookManager(NotifyUser plugin) {
        this.plugin = plugin;
        if (NotifyUser.debugEnabled()) {
            for (String key : getHooksConfig().getKeys(true)) {
                NotifyUser.debug(key + " = " + getHooksConfig().getBoolean(key));
            }
        }
        initHooks();
    }

    private void initHooks() {
        hooks = new ArrayList<>();
        initFactionsHook();
    }

    private void initFactionsHook() {
        Plugin factionsPlugin = plugin.getServer().getPluginManager().getPlugin("Factions");

        if (factionsPlugin != null) {
            String factionsVersion = factionsPlugin.getDescription().getVersion();
            FactionsHook factionsHook = null;

            // FactionsUUID
            if (factionsVersion.split("-")[1].charAt(0) == 'U') { // Check for FactionsUUID version format, e.g. '1.6.9.5-U0.5.10'
                factionsHook = new FactionsUUIDHook(this);
            }

            if (factionsHook == null) {
                if (getHooksConfig().getBoolean("factions")) {
                    NotifyUser.log("Can't hook into Factions! Is your plugin supported and up to date?", "WARNING");
                }
            }
            else {
                hooks.add(factionsHook);
            }
        }

    }

    public void enableHooks() {
        for (Hook hook : hooks) {
            boolean wasEnabled = hook.isEnabled();
            boolean isEnabled = hook.enable();
            if (isEnabled) NotifyUser.log(hook.getName() + " Hook enabled!", "INFO");
            else if (wasEnabled && !isEnabled) NotifyUser.log(hook.getName() + " Hook disabled!", "INFO");
        }
    }

    public void reloadHooks() {
        initHooks();
        enableHooks();
    }

    public ArrayList<Hook> getHooks() {
        return this.hooks;
    }
    public NotifyUser getPlugin() { return this.plugin; }

    public ConfigurationSection getHooksConfig() {
        return plugin.getConfigHandler().getHooksConfig();
    }

}
