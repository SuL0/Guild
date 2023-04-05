package kr.sul.guild.guild

import kr.sul.guild.Main
import java.util.*

// Why guild having UUID instead of IOfflineGuildPlayer(surely loaded in cache), is to stay data homeostasis in situation of swapping OfflineGuildPlayer and OnlineGuildPlayer
open class Guild(
    val uniqueId: UUID,
    var name: String,
    var ownerUuid: UUID,
    private val memberUuidList: ArrayList<UUID>,
): Data {
    val maxMembers = 200 // DEFAULT

    val sizeOfMembers: Int
        get() = memberUuidList.size
    fun getOwner(): IOfflineGuildPlayer {
        return cache.getGuildPlayer(ownerUuid)!!
    }
    fun findMember(uniqueId: UUID): IOfflineGuildPlayer? {
        val targetUuid = memberUuidList.find { it == uniqueId }
            ?: return null
        return cache.getGuildPlayer(targetUuid)
    }
    fun getMembers(): List<IOfflineGuildPlayer> {
        return memberUuidList.map { cache.getGuildPlayer(it)!! }
    }
    fun getMembersExceptOwner(): List<IOfflineGuildPlayer> {
        return getMembers().filter { it != getOwner() }
    }


    fun addMember(guildPlayer: IOfflineGuildPlayer): Boolean {
        if (sizeOfMembers >= maxMembers) {
            return false
        }
        guildPlayer.guildInfo = JoinedGuildInfo(this.uniqueId, RankInGuild.getDefaultRank())
        memberUuidList.add(guildPlayer.uniqueId)
        return true
    }
    fun removeMember(guildPlayer: IOfflineGuildPlayer) {
        if (guildPlayer.guildInfo?.guild != this) {
            throw Exception("${guildPlayer.name} is not in this guild")
        }
        guildPlayer.guildInfo = null
        memberUuidList.remove(guildPlayer.uniqueId)
    }

    companion object {
        private val cache = Main.storageCache
        fun createNewGuild(guildName: String, ownerUuid: UUID): Guild {
            return Guild(UUID.randomUUID(), guildName, ownerUuid, arrayListOf(ownerUuid))
        }
    }
}