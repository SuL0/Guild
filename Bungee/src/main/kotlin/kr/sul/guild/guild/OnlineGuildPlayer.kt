package kr.sul.guild.guild

import kr.sul.guild.message.EnumMessage
import kr.sul.guild.message.PlaceHolderReplace
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Listener
import java.sql.Timestamp
import java.util.*

class OnlineGuildPlayer(
    private val offlineGuildPlayer: OfflineGuildPlayer,
    val p: ProxiedPlayer
): IOfflineGuildPlayer, Listener {
    // just delegate to guildPlayerData
    override val uniqueId: UUID
        get() = offlineGuildPlayer.uniqueId
    override val name: String
        get() = offlineGuildPlayer.name
    override var guildInfo
        get() = offlineGuildPlayer.guildInfo
        set(value) {
            offlineGuildPlayer.guildInfo = value
        }
    override val messageBox
        get() = offlineGuildPlayer.messageBox
    override val invitationBox: InvitationBox
        get() = offlineGuildPlayer.invitationBox
    override fun isOnline(): Boolean {
        return offlineGuildPlayer.isOnline()
    }
    override var lastJoined: Timestamp
        get() = offlineGuildPlayer.lastJoined
        set(value) {
            offlineGuildPlayer.lastJoined = value
        }


    var guildChat = false

    init {
        messageBox.sendAll(this)
        invitationBox.sendAll()
    }
//    val p = plugin.proxy.getPlayer(offlineGuildPlayer.uniqueId)!!

    override fun sendMessage(enumMessage: EnumMessage, vararg placeHolderReplaces: PlaceHolderReplace) {
        p.sendMessage(enumMessage.getProcessedMessage(*placeHolderReplaces))
    }
    fun sendMessage(message: String) {
        p.sendMessage(message)
    }

    fun extractOfflineGuildPlayer(): OfflineGuildPlayer {
        return offlineGuildPlayer
    }
}