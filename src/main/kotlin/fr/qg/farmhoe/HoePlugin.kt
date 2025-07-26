package fr.qg.farmhoe

import fr.qg.farmhoe.commands.HoeGiveCommand
import fr.qg.farmhoe.commands.HoeReloadCommand
import fr.qg.farmhoe.commands.HoeSetCommand
import fr.qg.farmhoe.listener.HoeListener
import org.bukkit.command.CommandExecutor
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

class HoePlugin : JavaPlugin() {

    companion object {
        lateinit var plugin: HoePlugin
    }

    override fun onEnable() {
        saveDefaultConfig()
        plugin = this
        server.pluginManager.registerEvents(HoeListener, this)
        initialiseCommand("qghoegive", HoeGiveCommand, HoeGiveCommand)
        initialiseCommand("qghoeset", HoeSetCommand, HoeSetCommand)
        initialiseCommand("qghoereload", HoeReloadCommand, null)
    }

    fun JavaPlugin.initialiseCommand(name: String, el: CommandExecutor, cm: TabCompleter?) {
        getCommand(name)!!.setExecutor(el)
        if(cm != null)getCommand(name)!!.tabCompleter = cm
    }
}