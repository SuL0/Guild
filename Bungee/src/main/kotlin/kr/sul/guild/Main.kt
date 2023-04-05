package kr.sul.guild

import kr.sul.guild.storage.MockedDB
import kr.sul.guild.storage.Storage
import kr.sul.guild.storage.StorageCache
import kr.sul.guild.storage.StorageCacheWithAutoSave
import net.md_5.bungee.api.plugin.Plugin

class Main : Plugin() {
    companion object {
        lateinit var plugin: Plugin private set
        lateinit var storage: Storage  private set
        lateinit var storageCache: StorageCache private set
        lateinit var config: ConfigReader private set
//        val storageHandler = StorageHandler(storage, storageCache)
    }

    override fun onEnable() {
        plugin = this
        storage = MockedDB()
        storageCache = StorageCacheWithAutoSave(storage, 1)  // TODO should change the auto-save interval value
        config = ConfigReader(plugin)
        registerClasses()
        registerCommands()
        Lamp.initialize()
        MessageQueue.initialize()
    }
    private fun registerClasses() {
        plugin.proxy.pluginManager.registerListener(plugin, storageCache)
    }
    private fun registerCommands() {
        /*
        val manager = BungeeCommandManager(plugin)
        manager.registerCommand(CreateGuildCommand())

        manager.commandContexts.registerIssuerAwareContext(OnlineGuildPlayer::class.java) { cmd ->
            return@registerIssuerAwareContext storageCache.getGuildPlayerData(cmd.player.uniqueId, false)!! as OnlineGuildPlayer
        }
        manager.commandConditions.addCondition(OnlineGuildPlayer::class.java, "isBelongInGuild") { _, _, value ->
            if (value.joinedGuild == null) {
                value.sendMessage(EnumMessage.YOU_ARE_NOT_IN_GUILD)
                throw ConditionFailedException()
            }
        }
        manager.commandConditions.addCondition(OnlineGuildPlayer::class.java, "isManager") { _, _, value ->
            if (!value.joinedGuild!!.isManager(value)) {
                value.sendMessage(EnumMessage.YOU_ARE_NOT_A_MANAGER_OF_GUILD)
                throw ConditionFailedException()
            }
        }
        manager.commandConditions.addCondition(OnlineGuildPlayer::class.java, "isOwner") { _, _, value ->
            if (value.joinedGuild!!.getOwner() != value) {
                value.sendMessage(EnumMessage.YOU_ARE_NOT_AN_OWNER_OF_GUILD)
                throw ConditionFailedException()
            }
        }*/
    }
}