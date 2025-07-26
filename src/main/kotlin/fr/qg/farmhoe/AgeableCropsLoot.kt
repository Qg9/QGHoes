package fr.qg.farmhoe

import org.bukkit.Material

enum class AgeableCropsLoot(
    val cropMaterial: Material,
    val lootMaterial: Material,
    val minLoot: Int,
    val maxLoot: Int
) {
    WHEAT(Material.WHEAT, Material.WHEAT, 1, 1),
    CARROTS(Material.CARROTS, Material.CARROT, 1, 4),
    POTATOES(Material.POTATOES, Material.POTATO, 1, 4),
    BEETROOTS(Material.BEETROOTS, Material.BEETROOT, 1, 1),
    NETHER_WART(Material.NETHER_WART, Material.NETHER_WART, 2, 4),
    SWEET_BERRY_BUSH(Material.SWEET_BERRY_BUSH, Material.SWEET_BERRIES, 2, 3),
    COCOA(Material.COCOA, Material.COCOA_BEANS, 2, 3),
    MELON_STEM(Material.MELON_STEM, Material.MELON_SLICE, 3, 7),      // Melon: Harvest gives 3-7 slices
    PUMPKIN_STEM(Material.PUMPKIN_STEM, Material.PUMPKIN, 1, 1),      // Pumpkin block
    BAMBOO_SAPLING(Material.BAMBOO_SAPLING, Material.BAMBOO, 1, 1),  // When grown
    KELP(Material.KELP, Material.KELP, 1, 1),                         // Each kelp block drops 1
    FROSTED_ICE(Material.FROSTED_ICE, Material.AIR, 0, 0),            // No loot (melts to air)
    CAVE_VINES(Material.CAVE_VINES, Material.GLOW_BERRIES, 1, 2),     // Cave vines: 1-2 glow berries

    TORCHFLOWER_CROP(Material.TORCHFLOWER_CROP, Material.TORCHFLOWER, 1, 1)
}
