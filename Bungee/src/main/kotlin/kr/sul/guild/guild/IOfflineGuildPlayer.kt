package kr.sul.guild.guild

import kr.sul.guild.message.EnumMessage
import kr.sul.guild.message.MessageBox
import kr.sul.guild.message.PlaceHolderReplace
import java.sql.Timestamp
import java.util.*

interface IOfflineGuildPlayer {
    val uniqueId: UUID
    val name: String
    var guildInfo: JoinedGuildInfo?
    val messageBox: MessageBox
    val invitationBox: InvitationBox
    var lastJoined: Timestamp
    fun isOnline(): Boolean
    fun sendMessage(enumMessage: EnumMessage, vararg placeHolderReplaces: PlaceHolderReplace)
}