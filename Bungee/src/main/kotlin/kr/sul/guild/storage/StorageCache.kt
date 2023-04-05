package kr.sul.guild.storage

import kr.sul.guild.Main.Companion.plugin
import kr.sul.guild.guild.*
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListMap

// I don't know which will be better, to use UUID or Guild Name as a PK
open class StorageCache(private val storage: Storage): Listener {
    protected val guildUuidMapper = ConcurrentSkipListMap<String, UUID>(String.CASE_INSENSITIVE_ORDER)  // auto-removing by storageCacheWithAutoSave
    protected val guildMap = ConcurrentHashMap<UUID, GuildWrapper>()   // auto-removing by storageCacheWithAutoSave
    protected val guildPlayerMap = ConcurrentHashMap<UUID, IOfflineGuildPlayer>()  // auto-removing by storageCacheWithAutoSave


    fun getGuild(guildName: String): Guild? {
        val guildUuid = getMatchingGuildUuid(guildName)!!
        return getGuild(guildUuid)
    }
    fun getGuild(guildUuid: UUID): Guild? {
        return guildMap[guildUuid]?.guild
    }

    fun getGuildPlayer(uuid: UUID): IOfflineGuildPlayer? {
        return guildPlayerMap[uuid]
    }

    private fun getMatchingGuildUuid(guildName: String): UUID? {
        return guildUuidMapper[guildName]
    }


    fun isThisGuildNameCanUse(guildName: String): Boolean {
        if (getMatchingGuildUuid(guildName) != null || storage.getGuildUuid(guildName) != null) {
            return false
        }
        return true
    }
    // 이게 Cache에 있는 게 맞나... Handler에 옮겨야하나
    fun createGuild(guildName: String, ownerUUID: UUID): Guild {
        val guild = Guild.createNewGuild(guildName, ownerUUID)
        if (!isThisGuildNameCanUse(guildName)) {
            throw Exception("It shouldn't be happened")
        }
        guildUuidMapper[guildName] = guild.uniqueId
        guildMap[guild.uniqueId] = GuildWrapper(guild, true, false)
        return guild
    }

    fun deleteGuild(guild: Guild) {
        // didn't check DB cause It cause lag(I'll using this on main thread)
        // and It's not important to block perfectly, if user found vulnerabilities to delete not existing guild.
        if (!guildMap.containsKey(guild.uniqueId)) {
            throw Exception("The guild want to delete not exists")
        }
        guildMap[guild.uniqueId]!!.needToBeDeleted = true
    }






    // FIXME 같은 플레이어가 이미 로그인 된 상태에서 Login을 시도하면, onLogin이 onPlayerLogout보다 선행되지 않나?
    // TODO 이러면 onPlayerLogin할 당시 캐시에 플레이어가 OnlineGuildPlayer로 있게 되는데
    private val playerThread = hashMapOf<UUID, Thread>()
    // swap OfflineGuildPlayer and OnlineGuildPlayer in cache automatically
    @EventHandler
    fun onPlayerPostLogin(e: PostLoginEvent) {
        val pUuid = e.player.uniqueId
        // 빠르게 재접속을 할 경우(플레이어 전용 스레드가 새로 생성됨) 이전에 사용되던 Player Netty Thread 가 살아서 아직 작업을 처리중일 수도 있으니 접속을 막고 기다려야 함
        if (playerThread[pUuid]?.isAlive == true) {
            playerThread[pUuid]!!.join()
        }
        playerThread[pUuid] = Thread.currentThread()

        StorageLock.writeLock(null) {
            cachePlayerRelated(pUuid)
            if (guildPlayerMap[pUuid]!! !is OfflineGuildPlayer) {
                throw Exception("wtf why this isn't an OfflineGuildPlayer")
            }
            guildPlayerMap[pUuid] = OnlineGuildPlayer(guildPlayerMap[pUuid]!! as OfflineGuildPlayer, e.player)
        }
    }

    // TODO 서버 전환했을 때도 고려해야 함
    // 굳이 다중 번지 기준으로 설계할 이유 있나?
    @EventHandler
    fun onPlayerLogout(e: PlayerDisconnectEvent) {
        val p = e.player
        StorageLock.writeLock(null) {
            if (guildPlayerMap[p.uniqueId]!! !is OnlineGuildPlayer) {
                throw Exception("wtf why this isn't an OnlineGuildPlayer")
            }
            guildPlayerMap[p.uniqueId]!!.lastJoined = Timestamp(System.currentTimeMillis())
            guildPlayerMap[p.uniqueId] = (guildPlayerMap[p.uniqueId]!! as OnlineGuildPlayer).extractOfflineGuildPlayer()
        }
    }

    // Cache all related things with player who joined the game just now.
    private fun cachePlayerRelated(pUuid: UUID) {
        // Cache GuildPlayer if not cached
        if (!guildPlayerMap.contains(pUuid)) {
            // It just caches OfflineGuildPlayer, not OnlineGuildPlayer (What make OfflineGuildPlayer to OnlineGuildPlayer is onLogin method, will be performed next of this)
            cacheGuildPlayer(pUuid)
        }
        val guildPlayer = guildPlayerMap[pUuid]!!


        // Cache Guild if player have a guild (and if so, have to cache all guild players belonging to the same guild)
        if (guildPlayer.guildInfo != null) {
            // Cache guild
            cacheGuild(guildPlayer.guildInfo!!.guildUuid)
        }
    }

    private fun cacheGuild(guildUuid: UUID): Boolean {
        if (!guildMap.containsKey(guildUuid)) {
            val guild = storage.getGuild(guildUuid)
                ?: return false
            guildUuidMapper[guild.name] = guild.uniqueId
            guildMap[guild.uniqueId] = GuildWrapper(guild, false, false)

            if (guild.getMembersExceptOwner().isNotEmpty()) {
                // Cache guild members too
                for (member in guild.getMembers()) {
                    cacheGuildPlayer(member.uniqueId)
                }
            }
            return true
        }
        return false
    }
    private fun cacheGuildPlayer(pUuid: UUID): Boolean {
        if (!guildPlayerMap.containsKey(pUuid)) {
            var getGuildPlayer: IOfflineGuildPlayer? = storage.getGuildPlayer(pUuid)
            if (getGuildPlayer == null) {
                val pName = plugin.proxy.getPlayer(pUuid).name
                    ?: return false
                getGuildPlayer = OfflineGuildPlayer.createNewOfflineGuildPlayer(pUuid, pName)
            }
            guildPlayerMap[pUuid] = getGuildPlayer
            return true
        }
        return false
    }



    // I concluded not to use UPSERT query, that may cause possibility of bug
    data class GuildWrapper(val guild: Guild, val needInsertQuery: Boolean, var needToBeDeleted: Boolean)
}