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
        NotifyUser.debug("initHooks: hooks length = " + hooks.size());
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
                NotifyUser.debug("initFactionsHook: setting Listener");
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
                NotifyUser.debug("initFactionsHook: adding factionshook, now hooks length = " + hooks.size());
            }
        }

    }

    public void enableHooks() {
        NotifyUser.debug("enableHooks: hooks length = " + hooks.size());
        for (Hook hook : hooks) {
            boolean wasEnabled = hook.isEnabled();
            boolean isEnabled = hook.enable();
            if (isEnabled) NotifyUser.log(hook.getName() + " Hook enabled!", "INFO");
            else if (wasEnabled && !isEnabled) NotifyUser.log(hook.getName() + " Hook disabled!", "INFO");
        }
    }

    public void disableHooks() {
        NotifyUser.debug("disableHooks: hooks length = " + hooks.size());
        for (Hook hook: hooks) {
            NotifyUser.debug("disableHooks: hook = " + hook.getName());
            hook.disable();
        }
        hooks.clear();
        NotifyUser.debug("disableHooks: hooks length now = " + hooks.size());
    }

    public void reloadHooks() {
        NotifyUser.debug("reloadHooks: hooks length = " + hooks.size());
        disableHooks();
        initHooks();
        enableHooks();
        NotifyUser.debug("reloadHooks: hooks length now = " + hooks.size());
    }

    public Set<Hook> getHooks() {
        return this.hooks;
    }
    public NotifyUser getPlugin() { return this.plugin; }

    public ConfigurationSection getHooksConfig() {
        return plugin.getConfigHandler().getHooksConfig();
    }

}
