package fr.qg.farmhoe.manager

import fr.qg.farmhoe.AgeableCropsLoot
import fr.qg.farmhoe.Hoe
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Ageable
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ThreadLocalRandom

object FarmManager {
    fun harvest(blocks: List<Block>, replant: Boolean): Map<AgeableCropsLoot, Int> {
        val result = mutableMapOf<AgeableCropsLoot, Int>()

        blocks.forEach {
            val data = it.blockData as? Ageable ?: return@forEach
            val ageableEnum = AgeableCropsLoot.entries.find { it1 -> it1.cropMaterial == it.type } ?: return@forEach

            if (data.age != data.maximumAge) return@forEach
            result[ageableEnum] = result.getOrDefault(ageableEnum, 0) + 1
            if(!replant) it.type = Material.AIR else {
                data.age = 0
                it.blockData = data
            }
        }

        return result
    }

    fun toMaterialMap(lootMap: Map<AgeableCropsLoot, Int>, multiplier: Double): Map<Material, Int> =
        lootMap.entries.associate { (crop, count) ->
            val random = ThreadLocalRandom.current().nextInt(crop.minLoot, crop.maxLoot + 1)
            crop.lootMaterial to ((random * multiplier).toInt() * count)
        }

    fun toItemStacks(materialMap: Map<Material, Int>): List<ItemStack> =
        materialMap.flatMap { (material, amount) ->
            if (amount <= 0) emptyList()
            else List((amount + material.maxStackSize - 1) / material.maxStackSize) { i ->
                val size = if (i < amount / material.maxStackSize) material.maxStackSize else amount % material.maxStackSize
                ItemStack(material, if (size == 0) material.maxStackSize else size)
            }
        }

    fun updateHoeItemMeta(item: ItemStack, hoe: Hoe, placeholders: Map<String, String>) {
        val meta = item.itemMeta ?: return

        if (hoe.items.updateName && meta.hasDisplayName()) {
            var newName = meta.displayName
            placeholders.forEach { (ph, value) ->
                newName = newName.replace("%$ph%", value)
            }
            meta.setDisplayName(newName)
        }

        if (hoe.items.updateLore && meta.lore != null) {
            val newLore = hoe.items.lore.map { line ->
                var updatedLine = line
                placeholders.forEach { (ph, value) ->
                    updatedLine = updatedLine.replace("%$ph%", value)
                }
                updatedLine
            }
            meta.lore = newLore
        }

        item.itemMeta = meta
    }
}