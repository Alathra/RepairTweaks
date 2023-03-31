# RepairTweaks
A spigot plugin to modify and improve the anvil repair system.

Repair costs follow the equation, where n is raw materials used for repair:  cost = 6.2*ln(4.5n-0.5)+1.2

(1 = 10 levels,
2 = 14 levels,
3 = 17 levels,
4 = 19 levels)

Cost is also then increased by half the repair cost tag minus one. this makes more complex tools and armor more costly to repair.

Repairing with raw materials also does not increase the tools repair cost, meaning they can be repaired any number of times.

Despite this, raw enchanting is still less xp effificent for simple tools, up to 4 combines.
When installed, existing tools will not work well with this system.
