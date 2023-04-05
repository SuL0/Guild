package kr.sul.guild.command

import kr.sul.guild.Main
import kr.sul.guild.Main.Companion.plugin
import kr.sul.guild.command.annotation.CanKick
import kr.sul.guild.command.annotation.IsBelongInGuild
import kr.sul.guild.message.EnumMessage
import kr.sul.guild.message.PlaceHolderReplace
import kr.sul.guild.storage.StorageLock
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand

@Command("길드")
class KickCommand: GuildCommandWithHelp(
    "kick",
    "<길드멤버>"
) {

    @Subcommand("추방")
    fun execute(
        @IsBelongInGuild
        @CanKick
        gpSupplier: OnlineGuildPlayerSupplier,
        opponentName: String
    ) {
        StorageLock.writeLock(null) {
            val gp = gpSupplier.checkAndGet()
            val guild = gp.guildInfo!!.guild

            if (Main.plugin.proxy.getPlayer(opponentName) == null) {
                gp.sendMessage(EnumMessage.PLAYER_WHO_HAVE_THAT_NAME_NOT_EXISTS_OR_IN_OFFLINE)
                return@writeLock
            }
            val opponentUuid = Main.plugin.proxy.getPlayer(opponentName)!!.uniqueId
            val opponentGp = guild.findMember(opponentUuid)!!
            if (opponentGp.guildInfo!!.playerRank >= gp.guildInfo!!.playerRank) {
                gp.sendMessage(EnumMessage.THIS_COMMAND_CAN_BE_PERFORMED_TO_WHO_HAS_LOWER_RANK)
                return@writeLock
            }

            guild.removeMember(opponentGp)
            gp.sendMessage(EnumMessage.KICKED_PLAYER, PlaceHolderReplace("{player}", opponentName))
            opponentGp.sendMessage(EnumMessage.KICKED_FROM_GUILD, PlaceHolderReplace("{player}", gp.name))
        }
    }
}