package fr.qg.farmhoe

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.RegisteredServiceProvider

interface SellHandler {
    fun sell(items: Map<Material, Int>, multiplier: Double, player: Player)
    fun init()
}

val HANDLERS = mapOf("PLUGIN" to ConfigSellHandler)

object ConfigSellHandler : SellHandler {

    lateinit var economy: Economy
    private val plantPrices: MutableMap<AgeableCropsLoot, Double> = mutableMapOf()

    fun setupEconomy(): Boolean {
        val rsp: RegisteredServiceProvider<Economy>? =
            Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)
        economy = rsp?.provider ?: return false
        return true
    }

    fun loadPlantPrices(config: FileConfiguration) {
        plantPrices.clear()
        val section = config.getConfigurationSection("plant") ?: return
        for (name in section.getKeys(false)) {
            val crop = AgeableCropsLoot.valueOf(name)
            val price = config.getDouble("plant.$name")
            plantPrices[crop] = price
        }
    }

    override fun sell(items: Map<Material, Int>, multiplier: Double, player: Player) {
        val total = items.entries.sumOf { (material, amount) ->
            val crop = AgeableCropsLoot.entries.find { it.lootMaterial == material }
            val price = if (crop != null) plantPrices[crop] ?: 0.0 else 0.0
            price * amount
        } * multiplier

        if (total > 0) economy.depositPlayer(player, total)
    }

    override fun init() {
        setupEconomy()
        loadPlantPrices(HoePlugin.plugin.config)
    }
}
