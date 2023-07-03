package com.aubrithehuman.repairtweaks.commands;

import com.aubrithehuman.repairtweaks.RepairTweaks;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;

public class CommandManager {
    private final RepairTweaks instance;

    public CommandManager(RepairTweaks instance) {
        this.instance = instance;
    }

    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(instance).shouldHookPaperReload(true).silentLogs(true));
    }

    public void onEnable() {
        CommandAPI.onEnable();

        // Register commands
        new RepairTweaksCommand();
    }

    public void onDisable() {
        CommandAPI.onDisable();
    }
}