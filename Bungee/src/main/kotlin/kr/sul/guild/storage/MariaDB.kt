package guild.storage

import com.google.gson.Gson
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kr.sul.guild.guild.*
import kr.sul.guild.message.MessageBox
import kr.sul.guild.storage.DatabaseConstants
import kr.sul.guild.storage.Storage
import kr.sul.guild.util.withResources
import java.sql.ResultSet
import java.util.*
import kotlin.jvm.Throws

class MariaDB(
    private val ip: String,
    private val port: Int,
    private val userName: String,
    private val password: String,
    private val schemaName: String
): Storage {
    private val ds: HikariDataSource
    private val gson = Gson()
    init {
        val config = HikariConfig()
        config.run {
            jdbcUrl = "jdbc:mysql://$ip:$port/${this@MariaDB.schemaName}?useUnicode=yes&amp;characterEncoding=utf-8"
            username = this@MariaDB.userName
            password = this@MariaDB.password
        }
        ds = HikariDataSource(config)

        ds.connection.use { }  // It'll throw an exception if connection failed to disable plugin
        createTableIfNotExists(DatabaseConstants.CREATE_GUILD_TABLE)
        createTableIfNotExists(DatabaseConstants.CREATE_GUILD_PLAYER_TABLE)
        createTableIfNotExists(DatabaseConstants.CREATE_GUILD_JOINED_GUILD_INFO_TABLE)
    }

    private fun createTableIfNotExists(query: String) {
        ds.connection.use { connection ->
            connection.prepareStatement(query).use {
                it.executeQuery()
            }
        }
    }

    // 다들 데이터베이스 필드명을 하드코딩하네
    // closing statement makes its related resultSet closed
    @Throws
    override fun getGuild(guildUuid: UUID): Guild? {
        withResources {
            val conn = ds.connection.use()

            // this 가 Any가 됐는데 .use()는 어떻게 사용되는거지?
            val guildRs = run<Any, ResultSet> guildRs@ {
                val stmt = conn.prepareStatement(DatabaseConstants.SELECT_GUILD_BY_ID).use()
                stmt.setString(1, guildUuid.toString())
                val rs = stmt.executeQuery()
                if (rs.next()) {
                    return@guildRs rs
                } else {
                    return null
                }
            }

            val stmt = conn.prepareStatement(DatabaseConstants.SELECT_GUILD_MEMBERS).use()
            stmt.setString(1, guildUuid.toString())
            val rs = stmt.executeQuery()
            val memberUuidList = arrayListOf<UUID>()
            while (rs.next()) {
                memberUuidList.add(UUID.fromString("uid"))
            }
            if (memberUuidList.isNotEmpty()) {
                return Guild(
                    UUID.fromString(guildRs.getString("id")),
                    guildRs.getString("guild_name"),
                    UUID.fromString(guildRs.getString("owner_uuid")),
                    memberUuidList
                )
            } else {
                throw Exception("It should grab at least one player, guild owner")
            }
        }
    }

    // guildName is case-insensitive
    @Throws
    override fun getGuildUuid(guildName: String): UUID? {
        withResources {
            val conn = ds.connection.use()
            val stmt = conn.prepareStatement(DatabaseConstants.SELECT_GUILD_ID).use()
            stmt.setString(1, guildName)
            val rs = stmt.executeQuery()
            return if (rs.next()) {
                UUID.fromString(rs.getString("id"))
            } else {
                null
            }
        }
    }

    override fun saveGuild(guild: Guild): Boolean {
        val id = guild.uniqueId
        val ownerUuid = guild.ownerUuid
        val guildName = guild.name
        TODO("Not yet implemented")
    }

    override fun getGuildPlayer(uuid: UUID): OfflineGuildPlayer? {
        withResources {
            val conn = ds.connection.use()
            val stmt = conn.prepareStatement(DatabaseConstants.SELECT_GUILD_PLAYER).use()
            stmt.setString(1, uuid.toString())
            val rs = stmt.executeQuery()
            return if (rs.next()) {
                val name = rs.getString("player_name")
                val joinedGuildInfo = if (rs.getString("joined_guild_id") == null) {
                    val joinedGuildId = UUID.fromString(
                        rs.getString("joined_guild_id")
                    )
                    val playerRank = RankInGuild.valueOf(
                        rs.getString("player_rank")
                    )
                    JoinedGuildInfo(joinedGuildId, playerRank)
                } else null
                val messageBox = run {

                }
                val invitationBox = TODO()
                val lastJoined = rs.getTimestamp("last_joined")

                OfflineGuildPlayer(
                    uuid,
                    name,
                    joinedGuildInfo,
                    messageBox,
                    invitationBox,
                    lastJoined
                )
            } else {
                null
            }
        }
        TODO("Not yet implemented")
    }

    override fun saveGuildPlayer(offlineGuildPlayer: OfflineGuildPlayer): Boolean {
        val playerName = offlineGuildPlayer.name
        val joinedGuildId = offlineGuildPlayer.guildInfo?.guildUuid
        val messageBox = offlineGuildPlayer.messageBox
        val lastJoined = offlineGuildPlayer.lastJoined
        TODO("Not yet implemented")
    }

    override fun onClose() {
        closeConnection()
    }

    private fun closeConnection() {
        ds.close()
    }
}

