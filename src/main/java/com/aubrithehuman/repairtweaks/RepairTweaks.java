package com.aubrithehuman.repairtweaks;

import com.aubrithehuman.repairtweaks.commands.CommandHandler;
import com.aubrithehuman.repairtweaks.data.CustomToolsHolder;
import com.aubrithehuman.repairtweaks.listeners.ListenerHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class RepairTweaks extends JavaPlugin implements Listener {
    private static RepairTweaks instance;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;

    public static RepairTweaks getInstance() {
        return instance;
    }

    public void onLoad() {
        instance = this;
        commandHandler = new CommandHandler(instance);
        listenerHandler = new ListenerHandler(instance);

        commandHandler.onLoad();
        listenerHandler.onLoad();
    }

    public void onEnable() {
        this.saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);

        CustomToolsHolder.getInstance().loadCustomRepairRecipes();
        commandHandler.onEnable();
        listenerHandler.onEnable();
    }

    public void onDisable() {
        commandHandler.onDisable();
        listenerHandler.onDisable();
    }
}