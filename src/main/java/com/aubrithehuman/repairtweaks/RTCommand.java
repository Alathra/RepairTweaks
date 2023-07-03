package com.aubrithehuman.repairtweaks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class RTCommand  implements CommandExecutor, TabCompleter {

    private final String label = "&6[&aRepairTweaks&6]&r ";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        final Player p = (Player) commandSender;
        if (args.length == 0) {
            p.sendMessage(label + "RepairTweaks v" + Bukkit.getPluginManager().getPlugin("RepairTweaks").getDescription().getVersion());
        } else if (args.length == 1) {
            if(args[0].equalsIgnoreCase("info")) {
                p.sendMessage(label + "RepairTweaks v" + Bukkit.getPluginManager().getPlugin("RepairTweaks").getDescription().getVersion());
                p.sendMessage(label + "RepairTweaks by AubriTheHuman");
            } else if(args[0].equalsIgnoreCase("tweaks")) {
                p.sendMessage(label + "List of valid custom repair combinations");
                for(Material m : RepairTweaks.customTools.keySet()) {
                    p.sendMessage(" - " + m.name() + " + "  + RepairTweaks.customTools.get(m).name());
                }
            } else {
                p.sendMessage(label + " Improper usage!");
            }
        }
        return true;
    }

    List<String> available = List.of(new String[] {
            "info",
            "tweaks"
    });

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if(args.length >= 1) {
            return available;
        }
        return null;
    }
}
