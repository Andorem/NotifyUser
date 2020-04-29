package io.github.andorem.notifyuser.hooks;

import com.google.common.base.Throwables;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public abstract class Hook {
    protected String name;
    protected Plugin plugin;
    protected String pluginName = null;
    private boolean isEnabled = false;
    private ConfigurationSection hooksConfig;
    private boolean debugEnabled = false;

    public Hook(Plugin plugin, String name, ConfigurationSection hooksConfig) throws NullPointerException {
        this.plugin = plugin;
        this.name = name;
        this.hooksConfig = hooksConfig;
    }

    ConfigurationSection getHooksConfig() {
        return this.hooksConfig;
    }

    public boolean enable() {
        if (!isConfigEnabled()) {
            isEnabled = false;
        }
        else if (!doesPluginExist()) {
            log(Level.WARNING, "Enabled in config, but " + name + " was not detected! Disabling.");
            log(Level.WARNING, "To get rid of this error, install supported plugin or set the hook to 'false' in config.");
            disable();
        }
        else try {
            if (!isEnabled) set();
            isEnabled = true;
        }
        catch(Exception e) {
            log(Level.SEVERE, "ERROR while attempting to initalize:");
            log(Level.SEVERE, Throwables.getStackTraceAsString(e));
            log(Level.SEVERE, "Something went wrong. Could not initialize hook! (Is " + name + " supported and up to date?)");

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
        return getHooksConfig().getBoolean(getName().toLowerCase());
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

    public void log(Level logLevel, String message) {
        plugin.getLogger().log(logLevel, "[" + StringUtils.capitalize(getName()) + " Hook] " + message);
    }

public String getName() {
        return this.name;
    }
public boolean isEnabled() { return this.isEnabled; };
public abstract void set();
}
