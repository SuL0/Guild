package kr.sul.guild.command

import kr.sul.guild.Main.Companion.plugin
import kr.sul.guild.command.annotation.IsBelongInGuild
import kr.sul.guild.guild.OnlineGuildPlayer
import kr.sul.guild.message.EnumMessage
import kr.sul.guild.storage.StorageLock
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Default

@Command("g", "ㅎ", "길드채팅")
class GuildChatCommand: GuildCommandWithHelp(
    "chat",
    ""
) {
    init {
        GuildChat
    }
    @Default
    fun execute(
        @IsBelongInGuild
        gpSupplier: OnlineGuildPlayerSupplier
    ) {
        StorageLock.writeLock(null) {
            val gp = gpSupplier.checkAndGet()
            gp.guildChat = !gp.guildChat
            if (gp.guildChat) {
                gp.sendMessage(EnumMessage.GUILD_CHAT_ON)
            } else {
                gp.sendMessage(EnumMessage.GUILD_CHAT_OFF)
            }
        }
    }


    object GuildChat: Listener {
        init {
            plugin.proxy.pluginManager.registerListener(plugin, this)
        }

        @EventHandler
        fun onChat(e: ChatEvent) {
            if (e.isCommand || e.isProxyCommand || e.sender !is ProxiedPlayer) return
            val p = e.sender as ProxiedPlayer
            StorageLock.readLock(null) {
                val gp = try {
                    OnlineGuildPlayerSupplier(p).checkIsBelongInGuild(true).checkAndGet()
                } catch (ignored: Exception) {
                    return@readLock
                }
                if (gp.guildChat) {
                    for (member in gp.guildInfo!!.guild.getMembers().filterIsInstance<OnlineGuildPlayer>()) {
                        member.p.sendMessage("§2[길드채팅] §f${p.displayName} : ${e.message}")
                    }
                    e.isCancelled = true
                }
            }
        }
    }
}