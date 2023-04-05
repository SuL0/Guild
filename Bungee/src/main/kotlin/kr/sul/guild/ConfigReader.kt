package kr.sul.guild

import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.nio.file.Files

class ConfigReader(
    plugin: Plugin
) {
    companion object {
        private const val CONFIG_FILE_NAME = "bungee.yml"
    }
    init {
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdir()
        }
        val configFile = File(plugin.dataFolder, CONFIG_FILE_NAME)
        if (!configFile.exists()) {
            Files.copy(plugin.getResourceAsStream(CONFIG_FILE_NAME), configFile.toPath())
        }
    }
    private val provider = ConfigurationProvider.getProvider(YamlConfiguration::class.java)
    private val config = provider.load(File(plugin.dataFolder, CONFIG_FILE_NAME))
    private val defaultConfig = provider.load(plugin.getResourceAsStream(CONFIG_FILE_NAME))
    fun getString(node: String): String {
        return config.getString(node) ?: defaultConfig.getString(node)!!
    }
}