package fr.qg.farmhoe.commands

import fr.qg.farmhoe.manager.ConfigManager
import fr.qg.farmhoe.HoePlugin.Companion.plugin
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

object HoeGiveCommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String,args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("Usage: /qghoegive <hoeName> [player]")
            return true
        }

        val hoeName = args[0]
        val hoe = ConfigManager.hoes[hoeName]
        if (hoe == null) {
            sender.sendMessage("Unknown hoe: $hoeName")
            return true
        }

        val target: Player? = when {
            args.size >= 2 -> Bukkit.getPlayer(args[1])
            sender is Player -> sender
            else -> null
        }

        if (target == null) {
            sender.sendMessage("Player not found or must be specified.")
            return true
        }

        val item = ConfigManager.hoeItemToItemStack(hoe.items, hoeName, plugin)
        target.inventory.addItem(item)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return when (args.size) {
            1 -> ConfigManager.hoes.keys
                .filter { it.startsWith(args[0], ignoreCase = true) }
            2 -> Bukkit.getOnlinePlayers().map { it.name }
                .filter { it.startsWith(args[1], ignoreCase = true) }
            else -> emptyList()
        }
    }
}
