package kr.sul.guild.command

import kr.sul.guild.command.annotation.IsBelongInGuild
import kr.sul.guild.guild.RankInGuild
import kr.sul.guild.message.EnumMessage
import kr.sul.guild.storage.StorageLock
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand

@Command("길드")
class LeaveCommand: GuildCommandWithHelp(
    "leave",
    ""
) {

    @Subcommand("탈퇴")
    fun execute(
        @IsBelongInGuild
        gpSupplier: OnlineGuildPlayerSupplier
    ) {
        StorageLock.writeLock(null) {
            val gp = gpSupplier.checkAndGet()
            if (gp.guildInfo!!.playerRank == RankInGuild.OWNER) {
                gp.sendMessage(EnumMessage.GUILD_OWNER_IS_BLOCKED_FROM_LEAVING_GUILD)
                return@writeLock
            }

            // TODO Ask player that do you really want to leave the guild
            gp.guildInfo = null
            gp.sendMessage(EnumMessage.LEFT_FROM_GUILD)
        }
    }
}