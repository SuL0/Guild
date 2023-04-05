package kr.sul.guild.guild

import kr.sul.guild.Main.Companion.plugin
import kr.sul.guild.message.EnumMessage
import kr.sul.guild.message.MessageBox
import kr.sul.guild.message.PlaceHolderReplace
import java.sql.Timestamp
import java.util.*

// MessageBox에는 무슨 타입으로 메시지를 저장해야 하지?
open class OfflineGuildPlayer(
    override val uniqueId: UUID,
    override val name: String,
    override var guildInfo: JoinedGuildInfo?,
    override val messageBox: MessageBox,
    override val invitationBox: InvitationBox,
    override var lastJoined: Timestamp
): IOfflineGuildPlayer, Data {

    override fun isOnline(): Boolean {
        return plugin.proxy.getPlayer(uniqueId) != null
    }

    // Just put message in message box
    override fun sendMessage(enumMessage: EnumMessage, vararg placeHolderReplaces: PlaceHolderReplace) {
        messageBox.addMessage(enumMessage.getProcessedMessage(*placeHolderReplaces))
    }

    // what object will send messages in messageBox?

    companion object {
        fun createNewOfflineGuildPlayer(pUuid: UUID, name: String): IOfflineGuildPlayer {
            return OfflineGuildPlayer(
                pUuid, name, null, MessageBox(),
                InvitationBox(pUuid, InvitationBox.SETTING.DEFAULT, arrayListOf()),
                Timestamp(System.currentTimeMillis())
            )
        }
    }
}