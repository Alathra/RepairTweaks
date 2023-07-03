package com.aubrithehuman.repairtweaks.listeners;

import org.bukkit.event.Listener;

public interface ListenerI extends Listener {
    /**
     * Unregisters all event listeners in the object
     */
    default void unregister() {

    }
}
