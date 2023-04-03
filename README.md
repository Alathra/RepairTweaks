# RepairTweaks
A spigot plugin to modify and improve the anvil repair system.

Repair costs follow the equation, where n is raw materials used for repair, and c is the configured coefficient (default 4.5):  cost = c*ln(4.5n-0.5)+1.2

(1 = 7 levels,
2 = 11 levels,
3 = 13 levels,
4 = 14 levels)

Cost is also then increased by half the repair cost tag minus one. this makes more complex tools and armor more costly to repair. If a tool has a repair value of greater than 100, it does not have this repair behavior and will be deemed too expensive. This cost caps at 25 by default. (mending a diamond pickaxe to full costs 780 xp or about 23 levels).

Repairing with raw materials also does not increase the tools repair cost, meaning they can be repaired any number of times.

Despite this, raw enchanting is still less xp effificent for simple tools, up to 7 combines.
When installed, existing tools will not work well with this system.
