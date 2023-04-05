package kr.sul.guild

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    override fun onEnable() {
        val econ = Bukkit.getServer().servicesManager.getRegistration(Economy::class.java)?.provider
            ?: let {
                server.pluginManager.disablePlugin(this)
                return
            }
        MessageQueue(econ)
    }
}