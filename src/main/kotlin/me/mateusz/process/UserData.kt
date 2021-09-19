package me.mateusz.process

import me.mateusz.utils.HashUtil
import org.bukkit.OfflinePlayer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.Exception

class UserData(jplugin : JavaPlugin) {
    val plugin : JavaPlugin = jplugin
    val UserDataFolder = File(jplugin.dataFolder, "userdata" + File.separator)
    val HashUtil : HashUtil = HashUtil()

    init {
        if(!UserDataFolder.exists()) UserDataFolder.mkdirs()
    }

    fun CreateOrGetUser(p : Player, pass : String?) : FileConfiguration {
        val UserDataFile = File(plugin.dataFolder, "userdata" + File.separator + p.uniqueId + ".yml")
        if(!UserDataFile.exists() && pass != null) {
            UserDataFile.createNewFile()
            val config = YamlConfiguration.loadConfiguration(UserDataFile)
            config.set("usr", p.name)
            config.set("ip", p.address?.address?.hostAddress)
            config.set("pass", HashUtil.toSHA256(pass))
            config.set("usePin", false)
            config.set("pin", "not_set")
            UpdateOrSaveUser(p, config)
            return config
        }
        return YamlConfiguration.loadConfiguration(UserDataFile)
    }

    fun set(p : Player, key : String, value : Any) {
        val UserDataFile = File(plugin.dataFolder, "userdata" + File.separator + p.uniqueId + ".yml")
        val config = YamlConfiguration.loadConfiguration(UserDataFile)
        config.set(key, value.toString())
        UpdateOrSaveUser(p, config)
    }

    fun updateIfOld(p : Player, key : String, default : Any) {
        val UserDataFile = File(plugin.dataFolder, "userdata" + File.separator + p.uniqueId + ".yml")
        val config = YamlConfiguration.loadConfiguration(UserDataFile)
        if(!config.contains(key)) {
            config.set(key, default.toString())
            UpdateOrSaveUser(p, config)
        }
        return
    }

    fun UpdateOrSaveUser(p : Player, UserData : FileConfiguration) {
        val UserDataFile = File(plugin.dataFolder, "userdata" + File.separator + p.uniqueId + ".yml")
        UserData.save(UserDataFile)
    }

    fun CheckIfExists(p : Player) : Boolean {
        val UserDataFile = File(plugin.dataFolder, "userdata" + File.separator + p.uniqueId + ".yml")
        return UserDataFile.exists()
    }

    fun Validate(player : Player, p : String, what: String) : Boolean {
        if(what != "pass" && what != "pin") throw Exception("Invalid validation type!")
        val UserDataFile = File(plugin.dataFolder, "userdata" + File.separator + player.uniqueId + ".yml")
        val config = YamlConfiguration.loadConfiguration(UserDataFile)
        if(HashUtil.toSHA256(p) == config.get(what)) return true
        return false
    }

    fun PasswordMatchesRules(pass : String) : Boolean {
        return (pass.length >= 6 && pass.contains(Regex("[A-Z]")) && pass.contains(Regex("[0-9]")))
    }

    fun PinMatchesRules(pin : String) : Boolean {
        return (pin.length == 6 && pin.contains(Regex("\\d{6}")))
    }

    fun DeleteUser(p : Player) : Boolean {
        try {
            val UserDataFile = File(plugin.dataFolder, "userdata" + File.separator + p.uniqueId + ".yml")
            UserDataFile.delete()
            return true
        } catch(e : Exception) {
            return false
        }
    }

    fun DeleteUser(p : OfflinePlayer) : Boolean {
        try {
            val UserDataFile = File(plugin.dataFolder, "userdata" + File.separator + p.uniqueId + ".yml")
            UserDataFile.delete()
            return true
        } catch(e : Exception) {
            return false
        }
    }

    fun get(p : Player, key : String) : Any? {
        val UserDataFile = File(plugin.dataFolder, "userdata" + File.separator + p.uniqueId + ".yml")
        val config = YamlConfiguration.loadConfiguration(UserDataFile)
        return config.get(key)
    }
}