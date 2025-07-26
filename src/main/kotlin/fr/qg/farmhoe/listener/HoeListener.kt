package fr.qg.farmhoe.listener
import fr.qg.farmhoe.HoePlugin
import fr.qg.farmhoe.manager.ConfigManager
import fr.qg.farmhoe.manager.ConfigManager.props_model
import fr.qg.farmhoe.manager.FarmManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType

object HoeListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBreakWithHoe(event: BlockBreakEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        val hoe = ConfigManager.hoes[ConfigManager.getHoeNameFromItem(item)] ?: return

        val radius = hoe.properties.radius
        val center = event.block
        val blocks = mutableListOf<Block>()

        for (dx in -radius..radius)
            for (dz in -radius..radius)
                blocks.add(center.world.getBlockAt(center.x + dx, center.y, center.z + dz))

        val stuffs = FarmManager.toMaterialMap(FarmManager.harvest(blocks, hoe.properties.replant), hoe.properties.itemMultiplier)
        event.isCancelled = true

        // Handle Items
        if (hoe.properties.sell) {
            ConfigManager.sellHandler.sell(stuffs, hoe.properties.sellMultiplier, player)
        } else {
            FarmManager.toItemStacks(stuffs).forEach { player.inventory.addItem(it) }
        }

        val props = props_model.toMutableMap()
        if (hoe.properties.stats) {
            val meta = item.itemMeta ?: return
            val pdc = meta.persistentDataContainer
            stuffs.forEach { (material, amount) ->
                val key = NamespacedKey(HoePlugin.plugin, "stats_${material.name}")
                val oldValue = pdc.get(key, PersistentDataType.INTEGER) ?: 0
                pdc.set(key, PersistentDataType.INTEGER, oldValue + amount)
                props["stats_${material.name}"] = "${oldValue + amount}"
            }
            item.itemMeta = meta
        }

        if (hoe.items.updateName || hoe.items.updateLore)
            FarmManager.updateHoeItemMeta(item, hoe, props)
    }


    @EventHandler
    fun onShiftRightClickWithHoe(event: PlayerInteractEvent) {

        if (event.hand != EquipmentSlot.HAND) return
        if (event.action != Action.RIGHT_CLICK_AIR) return

        val player = event.player
        if (!player.isSneaking) return

        val item = player.inventory.itemInMainHand
        val hoeName = ConfigManager.getHoeNameFromItem(item) ?: return
        val hoe = ConfigManager.hoes[hoeName] ?: return

        val actions = hoe.properties.shiftClickAction
        if (actions.isEmpty() || actions.contains("[no]")) return

        actions.forEach { rawCmd ->
            val meta = item.itemMeta ?: return
            val pdc = meta.persistentDataContainer

            val placeholders = mutableMapOf<String, String>()
            placeholders["%player%"] = player.name
            placeholders["%hoe%"] = hoeName

            if (hoe.properties.stats)
                props_model.keys.forEach {
                    val key = NamespacedKey(HoePlugin.plugin, it)
                    val value = pdc.get(key, PersistentDataType.INTEGER) ?: 0
                    placeholders["%$it%"] = value.toString()
                }

            hoe.properties.shiftClickAction.forEach { rawCmd ->
                val cmd = placeholders.entries.fold(rawCmd) { acc, (ph, v) -> acc.replace(ph, v) }
                when {
                    cmd.startsWith("[console]") -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.removePrefix("[console]").trim())
                    cmd.startsWith("[player]") -> player.performCommand(cmd.removePrefix("[player]").trim())
                    else -> player.performCommand(cmd)
                }
            }

        }
        event.isCancelled = true
    }
}