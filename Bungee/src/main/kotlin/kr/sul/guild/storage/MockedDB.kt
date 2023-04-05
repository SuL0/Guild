package kr.sul.guild.storage

import kr.sul.guild.guild.Guild
import kr.sul.guild.guild.OfflineGuildPlayer
import java.util.*

class MockedDB: Storage {
    override fun saveGuild(guild: Guild): Boolean {
        return true
    }

    override fun getGuild(guildUuid: UUID): Guild? {
        return null
    }

    override fun getGuildUuid(guildName: String): UUID? {
        return null
    }

    override fun saveGuildPlayer(offlineGuildPlayer: OfflineGuildPlayer): Boolean {
        return true
    }

    override fun getGuildPlayer(uuid: UUID): OfflineGuildPlayer? {
        return null
    }

    override fun onClose() {
        return
    }
}