package com.aubrithehuman.repairtweaks.listeners;

import com.aubrithehuman.repairtweaks.RepairTweaks;

import java.util.ArrayList;
import java.util.List;

public class ListenerHandler {
    private final RepairTweaks instance;
    private final List<ListenerI> listeners = new ArrayList<>();

    public ListenerHandler(RepairTweaks instance) {
        this.instance = instance;
        this.listeners.addAll(List.of(
            new InventoryClickListener(instance),
            new PrepareAnvilListener(instance)
        ));
    }

    public void onLoad() {

    }

    public void onEnable() {
        for (ListenerI listener : listeners) {
            instance.getServer().getPluginManager().registerEvents(listener, instance);
        }
    }

    public void onDisable() {
        for (ListenerI listener : listeners) {
            listener.unregister();
        }
    }
}
