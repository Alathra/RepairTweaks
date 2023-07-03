package com.aubrithehuman.repairtweaks.data;

import com.aubrithehuman.repairtweaks.RepairTweaks;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class CustomToolsHolder {
    private static CustomToolsHolder customToolsHolder;
    private final Map<Material, Material> customTools = new HashMap<>();
    private final RepairTweaks instance;

    private CustomToolsHolder() {
        instance = RepairTweaks.getInstance();
    }

    public static CustomToolsHolder getInstance() {
        if (customToolsHolder == null)
            customToolsHolder = new CustomToolsHolder();

        return customToolsHolder;
    }

    /**
     * load repair info from config
     */
    public void loadCustomRepairRecipes() {
        customTools.clear();

        ConfigurationSection section = instance.getConfig().getConfigurationSection("customRepair");

        if (section == null) {
            instance.getLogger().severe("Failed to load customRepair section from config.yml!");
            instance.getServer().getPluginManager().disablePlugin(instance);
            return;
        }

        Map<String, Object> map = section.getValues(false);

        for (String keyMaterial : map.keySet()) {
            if (map.get(keyMaterial) instanceof final String valueMaterial) {
                final Material material1 = Material.getMaterial(keyMaterial.toUpperCase());
                final Material material2 = Material.getMaterial(valueMaterial.toUpperCase());

                if (material1 != null && material2 != null) {
                    customTools.put(material1, material2);
                    instance.getLogger().config("Registered custom repair behavior for %s using material %s.".formatted(material1.name(), material2.name()));
                    continue;
                }
            }

            instance.getLogger().warning("Failed to load custom repair behavior for \"%s\"".formatted(keyMaterial));
        }
    }

    public Map<Material, Material> getCustomTools() {
        return customTools;
    }
}
