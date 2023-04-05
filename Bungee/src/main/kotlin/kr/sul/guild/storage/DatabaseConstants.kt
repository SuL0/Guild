package kr.sul.guild.storage

object DatabaseConstants {
    private const val GUILD_TABLE_NAME = "g_guild"
    private const val GUILD_PLAYER_TABLE_NAME = "g_guild_player"
    private const val GUILD_JOINED_GUILD_INFO = "g_joined_guild_info"

    const val CREATE_GUILD_TABLE = "CREATE TABLE IF NOT EXISTS $GUILD_TABLE_NAME" +
            "CREATE TABLE `guild` (" +
            "`id` VARCHAR(50) NOT NULL COLLATE 'utf8mb3_general_ci'," +
            "`guild_name` VARCHAR(50) NOT NULL COLLATE 'utf8mb3_general_ci'," +
            "`owner_uuid` VARCHAR(50) NOT NULL COLLATE 'utf8mb3_general_ci'," +
            "PRIMARY KEY (`id`) USING BTREE" +
            ")" +
            "COLLATE='utf8mb3_general_ci'" +
            "ENGINE=InnoDB" +
            ";"
    const val CREATE_GUILD_PLAYER_TABLE = "CREATE TABLE IF NOT EXISTS $GUILD_PLAYER_TABLE_NAME" +
            "CREATE TABLE `g_guild_player` (" +
            "`uid` VARCHAR(50) NOT NULL COLLATE 'utf8mb3_general_ci'," +
            "`player_name` VARCHAR(50) NOT NULL COLLATE 'utf8mb3_general_ci'," +
            "`message_box` LONGTEXT NULL DEFAULT NULL COLLATE 'utf8mb4_bin'," +
            "`invitation_box` LONGTEXT NULL DEFAULT NULL COLLATE 'utf8mb4_bin'," +
            "`last_joined` TIMESTAMP NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()," +
            "PRIMARY KEY (`uid`) USING BTREE" +
            ")" +
            "COLLATE='utf8mb3_general_ci'" +
            "ENGINE=InnoDB" +
            ";"
    const val CREATE_GUILD_JOINED_GUILD_INFO_TABLE = "CREATE TABLE IF NOT EXISTS $GUILD_JOINED_GUILD_INFO" + 
            "CREATE TABLE `g_joined_guild_info` (" +
            "`uid` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8mb3_general_ci'," +
            "`joined_guild_id` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8mb3_general_ci'," +
            "`player_rank` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8mb3_general_ci'," +
            "PRIMARY KEY (`uid`) USING BTREE" +
            ")" +
            "COLLATE='utf8mb3_general_ci'" +
            "ENGINE=InnoDB" +
            ";"
    const val SELECT_GUILD_BY_ID = "SELECT * " +
            "FROM $GUILD_TABLE_NAME " +
            "WHERE ID = ?"
    const val SELECT_GUILD_ID = "SELECT ID " +
            "FROM $GUILD_TABLE_NAME " +
            "WHERE UPPER(GUILD_NAME) = UPPER(?)"
    const val SELECT_GUILD_MEMBERS = "SELECT UID " +
            "FROM $GUILD_PLAYER_TABLE_NAME " +
            "WHERE JOINED_GUILD_ID = ?"
    const val SELECT_GUILD_PLAYER = "SELECT * " +
            "FROM $GUILD_PLAYER_TABLE_NAME AA " +
            "LEFT OUTER JOIN $GUILD_JOINED_GUILD_INFO BB " +
            "ON AA.UID = BB.UID " +
            "WHERE AA.UID = ?"
}