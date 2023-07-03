package com.aubrithehuman.repairtweaks.listeners;

import com.aubrithehuman.repairtweaks.RepairTweaks;
import com.aubrithehuman.repairtweaks.data.CustomToolsHolder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.Repairable;

import java.util.Map;

public class PrepareAnvilListener implements ListenerI {
    private final RepairTweaks instance;

    PrepareAnvilListener(RepairTweaks instance) {
        this.instance = instance;
    }

    public void unregister() {
        PrepareAnvilEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void prepare(PrepareAnvilEvent e) {
        AnvilInventory inv = e.getInventory();
        ItemStack tool = inv.getFirstItem();
        ItemStack repairConsumable = inv.getSecondItem();

        if (repairConsumable == null || tool == null)
            return;

        final Map<Material, Material> customTools = CustomToolsHolder.getInstance().getCustomTools();

        if (instance.getConfig().getStringList("items").contains(tool.getType().name()) || customTools.containsKey(tool.getType())) {
            if (instance.getConfig().getStringList("materials").contains(repairConsumable.getType().name()) || customTools.containsValue(repairConsumable.getType())) {

                //THIS IS A PRECAUTION (fixing isaacs bug!) and still preventing spawn shop tools from repair
                inv.setMaximumRepairCost(100);

                //calc needed mats to repair
                int needed = 0;
                if (tool.getItemMeta() instanceof Damageable) {
                    needed = ((Damageable) tool.getItemMeta()).getDamage() / (tool.getType().getMaxDurability() / 4) + 1;
                    needed = Math.min(needed, 4);
                }

                /*
                 * calc cost of repair, scales logarithmically
                 * second math adds in tools complexity based on previous combines at half penalty
                 * a tool with 3 previous combines (repairCost: 7), adds 3 levels to repair costs
                 * most tools will not have this effect them, mostly swords and armor are subject to this.
                 * this equation is totally refactorable and cheapenable
                 */
                int cost = (int) Math.round(instance.getConfig().getDouble("costCoefficient") * Math.log(4.5D * (double) Math.min(repairConsumable.getAmount(), needed) - 0.5D) + 1.2D);
                //this line can be removed to standardize it
                cost += Math.max(((((Repairable) tool.getItemMeta()).getRepairCost() + 1) / 2) - 1, 0);

                //cap it
                if (cost <= 0) {
                    cost = 0;
                }
                if (cost >= instance.getConfig().getInt("maxCost")) {
                    cost = instance.getConfig().getInt("maxCost");
//							System.out.println("NOT RIGHT");
                }

                //cap the max repair value, used to allow certain tools to remain unrepairable
                if (((Repairable) inv.getItem(0).getItemMeta()).getRepairCost() < 100) {
                    inv.setRepairCost(cost);
                }

                //Custom repair Math Specifically
                //if the repair item and the tool item are registered
                if (customTools.containsKey(tool.getType()) && customTools.containsValue(repairConsumable.getType())) {
                    //check if its a match
                    if (customTools.get(tool.getType()) == repairConsumable.getType()) {
                        ItemStack out = tool.clone();
                        Damageable meta = (Damageable) out.getItemMeta();
                        meta.setDamage(Math.max(0, meta.getDamage() - Math.min(needed, repairConsumable.getAmount())
                            * (tool.getType().getMaxDurability() / 4)));
                        ((Repairable) meta).setRepairCost((((Repairable) meta).getRepairCost() + 1) * 2 - 1);
                        out.setItemMeta(meta);
                        e.setResult(out);
                    }
                }
            } else {
                inv.setMaximumRepairCost(40);
            }
        } else {
            inv.setMaximumRepairCost(40);
        }
    }
}
