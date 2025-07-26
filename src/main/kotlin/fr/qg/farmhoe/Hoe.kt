package fr.qg.farmhoe

import org.bukkit.Material


data class Hoe(
    val items: HoeItem,
    val properties: HoeProperties
)

data class HoeItem(
    val type: Material,
    val enchanted: Boolean,
    val name: String,
    val lore: List<String>,
    val updateName: Boolean,
    val updateLore: Boolean,
    val modelData: Int
)

data class HoeProperties(
    val radius: Int,
    val stats: Boolean,
    val replant: Boolean,
    val inInventory: Boolean,
    val itemMultiplier: Double,
    val sell: Boolean,
    val sellMultiplier: Double,
    val shiftClickAction: List<String>
)