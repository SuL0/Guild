package kr.sul.guild.guild

import kr.sul.guild.Main.Companion.storageCache
import java.util.*

data class JoinedGuildInfo(
    val guildUuid: UUID,
    val playerRank: RankInGuild
) {
    val guild: Guild
        get() = storageCache.getGuild(guildUuid)!!
}