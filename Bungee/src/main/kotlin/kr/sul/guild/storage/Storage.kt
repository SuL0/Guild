package kr.sul.guild.storage

import kr.sul.guild.guild.Guild
import kr.sul.guild.guild.OfflineGuildPlayer
import java.util.*

interface Storage {

    fun saveGuild(guild: Guild): Boolean
    fun getGuild(guildUuid: UUID): Guild?
    // TODO case-insensitive (대소문자 구분없이)
    fun getGuildUuid(guildName: String): UUID?

    fun saveGuildPlayer(offlineGuildPlayer: OfflineGuildPlayer): Boolean
    fun getGuildPlayer(uuid: UUID): OfflineGuildPlayer?

    fun onClose()
}