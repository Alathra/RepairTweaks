package com.aubrithehuman.repairtweaks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.plugin.java.JavaPlugin;

public class RepairTweaks extends JavaPlugin implements Listener {
	public void onEnable() {
		this.saveDefaultConfig();
		this.getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void move(PrepareAnvilEvent event) {
		if (event.getInventory() instanceof AnvilInventory) {
			ItemStack repairItem = event.getInventory().getItem(1);
			ItemStack tool = event.getInventory().getItem(0);
			if (repairItem != null && tool != null) {
				if (this.getConfig().getStringList("items").contains(tool.getType().name())) {
					if (this.getConfig().getStringList("materials").contains(repairItem.getType().name())) {
						event.getInventory().setMaximumRepairCost(1000);
						int needed = 0;
						if (tool.getItemMeta() instanceof Damageable) {
							needed = ((Damageable) tool.getItemMeta()).getDamage()
									/ (tool.getType().getMaxDurability() / 4) + 1;
							needed = Math.min(needed, 4);
						}

						System.out.println(needed);
						int cost = (int) Math
								.round(6.2D * Math.log(4.5D * (double) Math.min(repairItem.getAmount(), needed) - 0.5D)
										+ 1.2D);
						if (cost <= 0) {
							cost = 0;
						}

						if (cost >= 39) {
							cost = 39;
							System.out.println("NOT RIGHT");
						}

						System.out.println(cost);
						event.getInventory().setRepairCost(cost);
						if (tool.getType() == Material.TRIDENT
								&& repairItem.getType() == Material.PRISMARINE_CRYSTALS) {
							ItemStack out = tool.clone();
							Damageable meta = (Damageable) out.getItemMeta();
							meta.setDamage(Math.max(0, meta.getDamage() - Math.min(needed, repairItem.getAmount())
									* (tool.getType().getMaxDurability() / 4)));
							((Repairable) meta).setRepairCost((((Repairable) meta).getRepairCost() + 1) * 2 - 1);
							out.setItemMeta(meta);
							event.setResult(out);
						}
					} else {
						event.getInventory().setMaximumRepairCost(40);
					}
				} else {
					event.getInventory().setMaximumRepairCost(40);
				}
			}
		}

		System.out.println("Prepared Event");
	}

	@EventHandler
	public void click(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (event.getInventory() instanceof AnvilInventory && event.getSlot() == 2) {
				ItemStack item = event.getCurrentItem();
				if (item == null) {
					return;
				}

				if (this.getConfig().getStringList("items").contains(item.getType().name())) {
					ItemStack[] contents = ((AnvilInventory) event.getInventory()).getContents();
					if (contents[0] != null && contents[1] != null
							&& this.getConfig().getStringList("materials").contains(contents[1].getType().name())) {
						Repairable repairable = (Repairable) contents[0].getItemMeta();
						Repairable repairableNew = (Repairable) item.getItemMeta();
						if (repairable != null && repairableNew != null) {
							int current = repairable.getRepairCost();
							repairableNew.setRepairCost(current);
							item.setItemMeta(repairableNew);
							event.setCurrentItem(item);
						}
					}
				}
			}
		}

	}
}