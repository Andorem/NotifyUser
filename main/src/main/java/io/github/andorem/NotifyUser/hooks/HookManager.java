package io.github.andorem.notifyuser.hooks;

import io.github.andorem.notifyuser.hooks.factions.FactionsChatListener;
import io.github.andorem.notifyuser.hooks.factions.FactionsHook;
import io.github.andorem.notifyuser.NotifyUser;
import io.github.andorem.notifyuser.hooks.factions.factionsuuid.FactionsUUIDHook;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class HookManager {
    NotifyUser plugin;
    Set<Hook> hooks = new HashSet<>();

    public HookManager(NotifyUser plugin) {
        this.plugin = plugin;
        initHooks();
    }

    private void initHooks() {
        hooks = new HashSet<>();
        initFactionsHook();
    }

    private void initFactionsHook() {
        Plugin factionsPlugin = plugin.getServer().getPluginManager().getPlugin("Factions");

        if (factionsPlugin != null) {
            String factionsVersion = factionsPlugin.getDescription().getVersion();
            FactionsHook factionsHook = null;

            // FactionsUUID
            if (factionsVersion.split("-")[1].charAt(0) == 'U') { // Check for FactionsUUID version format, e.g. '1.6.9.5-U0.5.10'
                factionsHook = new FactionsUUIDHook(plugin, plugin.getConfigHandler().getHooksConfig());
                factionsHook.setListener(new FactionsChatListener(factionsHook));
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

    public void disableHooks() {
        for (Hook hook: hooks) {
            hook.disable();
        }
        hooks.clear();
    }

    public void reloadHooks() {
        disableHooks();
        initHooks();
        enableHooks();
    }

    public Set<Hook> getHooks() {
        return this.hooks;
    }
    public NotifyUser getPlugin() { return this.plugin; }

    public ConfigurationSection getHooksConfig() {
        return plugin.getConfigHandler().getHooksConfig();
    }

}
