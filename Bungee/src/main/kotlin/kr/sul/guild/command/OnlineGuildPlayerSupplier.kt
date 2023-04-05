package kr.sul.guild.command

import kr.sul.guild.Main.Companion.storageCache
import kr.sul.guild.exception.ExitFromCommandException
import kr.sul.guild.guild.OnlineGuildPlayer
import kr.sul.guild.guild.RankInGuild
import kr.sul.guild.message.EnumMessage
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

class OnlineGuildPlayerSupplier(
    val p: ProxiedPlayer
) {
    private var checkIsBelongInGuild = false
    private var checkIsNotBelongInGuild = false
    private var checkCanInvite = false
    private var checkCanKick = false
    private var checkIsOwner = false

    fun checkIsBelongInGuild(b: Boolean): OnlineGuildPlayerSupplier {
        checkIsBelongInGuild = b
        return this
    }
    fun checkIsNotBelongInGuild(b: Boolean): OnlineGuildPlayerSupplier {
        checkIsNotBelongInGuild = b
        return this
    }
    fun checkCanInvite(b: Boolean): OnlineGuildPlayerSupplier {
        checkCanInvite = b
        return this
    }
    fun checkCanKick(b: Boolean): OnlineGuildPlayerSupplier {
        checkCanKick = b
        return this
    }
    fun checkIsOwner(b: Boolean): OnlineGuildPlayerSupplier {
        checkIsOwner = b
        return this
    }



    private fun getGuildPlayer(): OnlineGuildPlayer {
        return storageCache.getGuildPlayer(p.uniqueId)!! as OnlineGuildPlayer
    }

    fun checkAndGet(): OnlineGuildPlayer {
        val guildPlayer = getGuildPlayer()
        if (checkIsBelongInGuild) {
            if (guildPlayer.guildInfo == null) {
                guildPlayer.sendMessage(EnumMessage.YOU_ARE_NOT_IN_GUILD)
                throw ExitFromCommandException()
            }
        }
        if (checkIsNotBelongInGuild) {
            if (guildPlayer.guildInfo != null) {
                guildPlayer.sendMessage(EnumMessage.YOU_ALREADY_BELONG_IN_GUILD)
                throw ExitFromCommandException()
            }
        }
        if (checkCanInvite) {
            if (!guildPlayer.guildInfo!!.playerRank.canInvite) {
                guildPlayer.sendMessage(EnumMessage.YOU_DONT_HAVE_PERMISSION_FOR_INVITE)
                throw ExitFromCommandException()
            }
        }
        if (checkCanKick) {
            if (!guildPlayer.guildInfo!!.playerRank.canKick) {
                guildPlayer.sendMessage(EnumMessage.YOU_DONT_HAVE_PERMISSION_FOR_KICK)
                throw ExitFromCommandException()
            }
        }
        if (checkIsOwner) {
            if (guildPlayer.guildInfo!!.playerRank != RankInGuild.OWNER) {
                guildPlayer.sendMessage(EnumMessage.YOU_ARE_NOT_AN_OWNER_OF_GUILD)
                throw ExitFromCommandException()
            }
        }
        return guildPlayer
    }
}