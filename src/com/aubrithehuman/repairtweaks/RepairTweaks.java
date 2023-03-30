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
	public void prepare(PrepareAnvilEvent event) {
		if (event.getInventory() instanceof AnvilInventory) {
			ItemStack repairItem = event.getInventory().getItem(1);
			ItemStack tool = event.getInventory().getItem(0);
			if (repairItem != null && tool != null) {
				if (this.getConfig().getStringList("items").contains(tool.getType().name())) {
					if (this.getConfig().getStringList("materials").contains(repairItem.getType().name())) {

						//THIS IS A PRECAUTION (fixing isaacs bug!)
						event.getInventory().setMaximumRepairCost(1000);

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
						int cost = (int) Math.round(6.2D * Math.log(4.5D * (double) Math.min(repairItem.getAmount(), needed) - 0.5D) + 1.2D);
						//this line can be removed to standardize it
						cost += Math.max(((((Repairable) tool.getItemMeta()).getRepairCost() + 1) / 2) - 1, 0);

						//cap it
						if (cost <= 0) {
							cost = 0;
						}
						if (cost >= 39) {
							cost = 39;
//							System.out.println("NOT RIGHT");
						}
						event.getInventory().setRepairCost(cost);

						//Trident Math Specifically
						if (tool.getType() == Material.TRIDENT && repairItem.getType() == Material.PRISMARINE_CRYSTALS) {
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
	}

	/**
	 * Most of this just makes sure not to modify the Repair cost tag when doing a raw
	 * item repair.
	 *
	 * Some is also related to trident repairing
	 *
	 * @param event
	 */
	@EventHandler
	public void click(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			if (event.getInventory() instanceof AnvilInventory && event.getSlot() == 2) {
				ItemStack item = event.getCurrentItem();
				if (item == null) {
					return;
				}

				//Replace grabbed item with old repairCost tag
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

						//Trident Crystals refund (it consumes the whole stack, so this calculated how much to gift the player back
						if (contents[0].getType() == Material.TRIDENT && contents[1].getType() == Material.PRISMARINE_CRYSTALS) {
							ItemStack crystals = contents[1].clone();

							int needed = 0;
							if (contents[0].getItemMeta() instanceof Damageable) {
								needed = ((Damageable) contents[0].getItemMeta()).getDamage() / (contents[0].getType().getMaxDurability() / 4) + 1;
							}
							needed = Math.min(needed, 4);

							int amount = crystals.getAmount() - needed;
							if (amount < 0) amount = 0;
							crystals.setAmount(amount);

							event.getWhoClicked().getWorld().dropItem(event.getWhoClicked().getLocation(), crystals);
						}
					}
				}
			}
		}

	}
}