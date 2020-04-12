package com.minuxe.notifyuser.hooks.factions;

import com.minuxe.notifyuser.NotifyUser;
import com.minuxe.notifyuser.hooks.Hook;
import com.minuxe.notifyuser.hooks.HookManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;


public class FactionsHook extends Hook {
    ArrayList<String> FACTION_PLUGINS = new ArrayList<>();
    FactionsChatListener listener;
    final static String CHAT_FACTION = "CHAT_FACTION",
                        CHAT_ALLY = "CHAT_ALLY",
                        CHAT_TRUCE = "CHAT_TRUCE";

    public FactionsHook(HookManager hm) {
        super(hm, "factions");
    }

    private void setSupportedPlugins() {
        FACTION_PLUGINS = new ArrayList<>();
        FACTION_PLUGINS.add("Factions");
    }

    protected void setPluginName() {
        String pluginName = null;
        setSupportedPlugins();
        for (String name : FACTION_PLUGINS) {
            NotifyUser.debug("FactionsHook: Checking if plugin " + name + " is enabled...");
            if (Bukkit.getPluginManager().getPlugin(name) != null) {
                NotifyUser.debug("FactionsHook: Plugin " + name + " is enabled!");
                pluginName = name;
                break;
            }
        }
        this.pluginName = pluginName;
        if (pluginName == null) {
            log("Could not find a supported factions plugin to hook into!", "WARNING");
            return;
        }
    }

    protected String getPluginName() {
        return this.pluginName;
    }

    protected void set() {
        setListener();

        NotifyUser.debug("FactionsHook: activated Listener");
    }

    private void setListener() {
        this.listener = new FactionsChatListener(this);
        hm.getPlugin().getServer().getPluginManager().registerEvents(listener, hm.getPlugin());
    }

}
