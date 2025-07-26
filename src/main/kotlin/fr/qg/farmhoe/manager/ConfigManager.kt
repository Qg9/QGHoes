package fr.qg.farmhoe.manager

import fr.qg.farmhoe.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

object ConfigManager {

    lateinit var hoes: Map<String,Hoe>
    lateinit var sellHandler: SellHandler
    val props_model = mutableMapOf<String, String>()

    init {
        AgeableCropsLoot.entries.forEach { props_model["stats_${it.lootMaterial.name}"] = "0" }
        load()
    }

    fun load() {
        hoes = loadAllHoes(HoePlugin.plugin.config)
        sellHandler = HANDLERS.get(HoePlugin.plugin.config.get("sell", "PLUGIN")) ?: ConfigSellHandler
    }

    fun loadHoe(config: FileConfiguration, path: String): Hoe {
        val itemSection = "$path.items"
        val propSection = "$path.properties"
        val item = HoeItem(
            type = Material.valueOf(config.getString("$itemSection.type")!!),
            enchanted = config.getBoolean("$itemSection.enchanted"),
            name = config.getString("$itemSection.name", "Hoe")!!.replace("&", "§"),
            lore = config.getStringList("$itemSection.lore").map{ it.replace("&", "§") },
            updateName = config.getBoolean("$itemSection.update-name"),
            updateLore = config.getBoolean("$itemSection.update-lore"),
            modelData = config.getInt("$itemSection.model-data")
        )
        val properties = HoeProperties(
            radius = config.getInt("$propSection.radius"),
            replant = config.getBoolean("$propSection.replant"),
            inInventory = config.getBoolean("$propSection.in-inventory"),
            itemMultiplier = config.getDouble("$propSection.item-multiplier"),
            sell = config.getBoolean("$propSection.sell"),
            sellMultiplier = config.getDouble("$propSection.sell-multiplier"),
            shiftClickAction = config.getStringList("$propSection.shift-click-action"),
            stats = config.getBoolean("$propSection.stats")
        )
        return Hoe(item, properties)
    }

    fun loadAllHoes(config: FileConfiguration): Map<String, Hoe> {
        val hoesSection = config.getConfigurationSection("hoes") ?: return emptyMap()
        return hoesSection.getKeys(false).associateWith {
                hoeName -> loadHoe(config, "hoes.$hoeName")
        }
    }

    fun hoeItemToItemStack(hoeItem: HoeItem, hoeName: String, plugin: Plugin): ItemStack {

        val item = ItemStack(hoeItem.type)
        val meta = item.itemMeta ?: return item

        meta.setDisplayName(props_model.entries.fold(hoeItem.name) { acc, it -> acc.replace("%${it.key}%", it.value)})
        meta.lore = hoeItem.lore.map { props_model.entries.fold(it) { acc, it -> acc.replace("%${it.key}%", it.value)} }

        if (hoeItem.enchanted) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        if (hoeItem.modelData != 0) {
            meta.setCustomModelData(hoeItem.modelData)
        }

        val key = NamespacedKey(plugin, "hoe_name")
        meta.persistentDataContainer.set(key, PersistentDataType.STRING, hoeName)

        item.itemMeta = meta
        return item
    }

    fun getHoeNameFromItem(item: ItemStack): String? {
        val meta = item.itemMeta ?: return null
        val key = NamespacedKey(HoePlugin.plugin, "hoe_name")
        return meta.persistentDataContainer.get(key, PersistentDataType.STRING)
    }

}
