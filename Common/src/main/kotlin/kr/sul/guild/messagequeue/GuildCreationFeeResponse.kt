package kr.sul.guild.messagequeue

import java.io.Serializable
import java.util.*

class GuildCreationFeeResponse(protocolId: Long, pUuid: UUID, val isSucceed: Boolean): GuildCreationFeeProtocol(protocolId, pUuid) {

}