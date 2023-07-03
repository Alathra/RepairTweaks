package com.aubrithehuman.repairtweaks.listeners;

import com.aubrithehuman.repairtweaks.RepairTweaks;
import com.aubrithehuman.repairtweaks.data.CustomToolsHolder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class InventoryClickListener implements ListenerI {
    private final RepairTweaks instance;

    InventoryClickListener(RepairTweaks instance) {
        this.instance = instance;
    }

    private static int getAmount(@NotNull ItemStack item1, ItemStack mats) {
        int needed = 0;
        if (item1.getItemMeta() instanceof Damageable) {
            needed = ((Damageable) item1.getItemMeta()).getDamage() / (item1.getType().getMaxDurability() / 4) + 1;
        }
        needed = Math.min(needed, 4);

        int amount = mats.getAmount() - needed;
        if (amount < 0) amount = 0;
        return amount;
    }

    public void unregister() {
        InventoryClickEvent.getHandlerList().unregister(this);
    }

    /**
     * Most of this just makes sure not to modify the Repair cost tag when doing a raw
     * item repair.
     * <p>
     * Some is also related to trident repairing
     */
    @EventHandler
    public void click(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p))
            return;

        if (!(e.getInventory() instanceof AnvilInventory inv && e.getSlot() == 2))
            return;

        ItemStack item = e.getCurrentItem();
        if (item == null)
            return;

        final Map<Material, Material> customTools = CustomToolsHolder.getInstance().getCustomTools();

        if (!instance.getConfig().getStringList("items").contains(item.getType().name()) && !customTools.containsKey(item.getType()))
            return;

        //Replace grabbed item with old repairCost tag
        final ItemStack[] contents = inv.getContents();
        final @Nullable ItemStack item1 = inv.getFirstItem();
        final @Nullable ItemStack item2 = inv.getSecondItem();

        if (
            item1 != null &&
                item2 != null &&
                (instance.getConfig().getStringList("materials").contains(item2.getType().name()) ||
                    customTools.containsValue(item2.getType()))
        ) {
            Repairable repairable = (Repairable) item1.getItemMeta();
            Repairable repairableNew = (Repairable) item.getItemMeta();
            if (repairable != null && repairableNew != null) {
                int current = repairable.getRepairCost();
                repairableNew.setRepairCost(current);
                item.setItemMeta(repairableNew);
                e.setCurrentItem(item);
            }

            // Custom Repairs refund (it consumes the whole stack, so this calculated how much to gift the player back)
            // Don't run if player can't upgrade
            if (inv.getRepairCost() <= p.getLevel() && customTools.containsKey(item1.getType()) && customTools.containsValue(item2.getType()) && customTools.containsKey(item.getType())) {
                //check if its a match
                if (customTools.get(item1.getType()) == item2.getType()) {
                    ItemStack mats = item2.clone();

                    final int amount = getAmount(item1, mats);
                    mats.setAmount(amount);

                    p.getWorld().dropItem(p.getLocation(), mats);
                }
            }
        }
    }

}
