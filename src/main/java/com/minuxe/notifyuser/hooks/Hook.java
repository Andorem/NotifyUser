package com.minuxe.notifyuser.hooks;

import com.minuxe.notifyuser.NotifyUser;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public abstract class Hook {
    protected String name;
    protected String pluginName = null;
    protected final HookManager hm;
    private boolean isEnabled;

    public Hook(HookManager hm, String name) throws NullPointerException {
        this.hm = hm;
        this.name = name;
        NotifyUser.debug(name + "Hook: Hook initialized.");
        NotifyUser.debug(name + "Hook: hooksConfig value for " + name.toLowerCase() + " = " + hm.getHooksConfig().getBoolean(name.toLowerCase()));
//        if (!isPluginEnabled()) {
//            log("Something went wrong while initializing hook. Disabling it.", "WARN");
//            throw new NullPointerException("Could not initialize Hook!");
//        }
    }

    boolean enable() {
        if (!isConfigEnabled()) {
            NotifyUser.debug(name + "Hook: Not enabled in config. Disabling.");
            isEnabled = false;
        }
        else if (!doesPluginExist()) {
            log("Enabled in config, but " + name + " was not detected! Disabling.", "WARNING");
            log("To get rid of this error, install supported plugin or set the hook to 'false' in config.", "WARNING");
            disable();
        }
        else try {
            set();
            isEnabled = true;
        }
        catch(Exception e) {
            log("Something went wrong. Could not initialize hook! (Is " + name + " supported and up to date?", "WARNING");
            disable();
        }
        return isEnabled;
    }

    protected void disable() {
        this.isEnabled = false;
    }

    boolean canEnable() {
        return isConfigEnabled() && doesPluginExist();
    }

    boolean isConfigEnabled() {
        return hm.getHooksConfig().getBoolean(getName().toLowerCase());
    }
    boolean doesPluginExist() {
        return Bukkit.getPluginManager().getPlugin(name) != null;
    }

//    void setEnabled(boolean doEnable) {
//        hm.getHooksConfig().set(getName(), doEnable);
//    }

//    void setName(String name) {
//        if (!hm.getHooksConfig().contains(name)) {
//            hm.getHooksConfig().set(name, isEnabled());
//            this.name = name;
//        }
//    }

    protected void log(String message, String logLevel) {
        NotifyUser.log("[" + StringUtils.capitalize(getName()) + " Hook] " + message, logLevel);
    }
//    protected Plugin getPlugin() {
//        return hm.getPlugin();
//    }
    public HookManager getManager() {
        return hm;
    }
    protected String getName() {
        return this.name;
    }
    protected abstract void set();
}
