package me.mateusz.commands

import me.mateusz.interfaces.ICommand
import me.mateusz.process.LoginProcess
import me.mateusz.process.UserData
import me.mateusz.utils.HashUtil
import net.md_5.bungee.api.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class cLogin(override var name: String, jplugin : JavaPlugin, preLoginProcess : LoginProcess) : ICommand {
    val plugin : JavaPlugin = jplugin
    val UserData : UserData = UserData(plugin)
    val LoginProcess : LoginProcess = preLoginProcess
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender is Player) {
            val p : Player = sender
            val shouldUsePin = UserData.get(p, "usePin") == "true"
            if(!LoginProcess.checkIfContains(p)) {
                p.sendMessage("§c§l(!) §7Jestes juz uwierzytelniony!")
                return true
            }
            if(shouldUsePin) {
                if(args.size != 2) {
                    p.sendMessage("§c§l(!) §7Uzycie: §8/§flogin §8[§fhaslo§8] [§fpin§8]")
                    return true
                }
            } else {
                if(args.size != 1) {
                    p.sendMessage("§c§l(!) §7Uzycie: §8/§flogin §8[§fhaslo§8]")
                    return true
                }
            }
            if(!UserData.CheckIfExists(p)) {
                p.sendMessage("§c§l(!) §7Nie jestes zrejestrowany! Uzyj komendy §8/§fregister")
                return true
            }
            return if(!UserData.Validate(p, args[0], "pass")) {
                p.sendMessage("§c§l(!) §7Zle haslo!")
                true
            } else {
                if(shouldUsePin && args.size == 2) {
                    if(!UserData.Validate(p, args[1], "pin")) {
                        p.sendMessage("§c§l(!) §7Zly pin!")
                        return true
                    }
                }
                LoginProcess.removePlayer(p)
                p.sendMessage("§a§l(✔) §7Zalogowano!")
                if(plugin.config.getBoolean("SendWelcomeMessage")) {
                    for(message : String in plugin.config.getStringList("WelcomeMessage")) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
                    }
                }
                plugin.server.consoleSender.sendMessage("${org.bukkit.ChatColor.DARK_GRAY}[${org.bukkit.ChatColor.GOLD}Authy${org.bukkit.ChatColor.DARK_GRAY}] ${org.bukkit.ChatColor.YELLOW}Player ${org.bukkit.ChatColor.WHITE}${p.name} ${org.bukkit.ChatColor.YELLOW}logged in with ip ${org.bukkit.ChatColor.WHITE}${p.address?.address?.hostAddress}")
                if(UserData.get(p, "usePin") == "false") {
                    p.sendMessage("§6§l(!) §cNie masz wlaczonego pinu§8! §7Dla bezpieczenstwa ustaw go pod §8/§fpin")
                }
                LoginProcess.EffectRunner.runLogin(p)
                true
            }

        }
        return true
    }
}