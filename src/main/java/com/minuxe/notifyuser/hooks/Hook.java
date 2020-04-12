package com.minuxe.notifyuser.hooks;

import com.minuxe.notifyuser.NotifyUser;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public abstract class Hook {
    protected boolean isEnabled = false;
    protected String name = "default";
    protected String pluginName = null;
    protected HookManager hm;

    public Hook(HookManager hm, String name) throws NullPointerException {
        this.hm = hm;
        this.name = name;
        setPluginName();
        if (getPluginName() == null) {
            log("Something went wrong while initializing hook. Disabling it.", "WARNING");
//            throw new NullPointerException("Hook Plugin name cannot be null!");
        }
    }
    boolean isEnabled() {
        isEnabled = hm.getHooksConfig().getBoolean(getName()) &&
                Bukkit.getPluginManager().getPlugin(getPluginName()) != null;
        NotifyUser.debug("Hook: isEnabled = " + isEnabled);
        return isEnabled;
    }

    void setEnabled(boolean doEnable) {
        hm.getHooksConfig().set(getName(), doEnable);
    }

    void setName(String name) {
        if (!hm.getHooksConfig().contains(name)) {
            hm.getHooksConfig().set(name, isEnabled());
            this.name = name;
        }
    }

    protected void log(String message, String logLevel) {
        NotifyUser.log("[" + StringUtils.capitalize(getName()) + " Hook] " + message, logLevel);
    }
    protected Plugin getPlugin() {
        return hm.getPlugin();
    }
    protected String getName() {
        return this.name;
    }
    protected abstract String getPluginName();
    protected abstract void setPluginName();
    protected abstract void set();
}
