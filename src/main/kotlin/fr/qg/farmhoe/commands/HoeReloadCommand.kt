package fr.qg.farmhoe.commands

import fr.qg.farmhoe.manager.ConfigManager
import fr.qg.farmhoe.HoePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object HoeReloadCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        HoePlugin.plugin.reloadConfig()
        ConfigManager.load()
        sender.sendMessage("§a[Hoe] Config reloaded!")

        return true
    }
}