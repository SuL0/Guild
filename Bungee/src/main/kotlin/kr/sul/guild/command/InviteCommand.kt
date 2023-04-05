package kr.sul.guild.command

import kr.sul.guild.Main.Companion.plugin
import kr.sul.guild.command.annotation.CanInvite
import kr.sul.guild.command.annotation.IsBelongInGuild
import kr.sul.guild.guild.Invitation
import kr.sul.guild.message.EnumMessage
import kr.sul.guild.message.PlaceHolderReplace
import kr.sul.guild.storage.StorageLock
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand

@Command("길드")
class InviteCommand: GuildCommandWithHelp(
    "invite",
    "<플레이어>"
) {

    @Subcommand("초대")
    fun execute(
        @IsBelongInGuild
        @CanInvite
        gpSupplier: OnlineGuildPlayerSupplier,
        opponentName: String
    ) {
        StorageLock.writeLock(null) {
            val gp = gpSupplier.checkAndGet()
            val guild = gp.guildInfo!!.guild
            if (guild.sizeOfMembers >= guild.maxMembers) {
                gp.sendMessage(EnumMessage.GUILD_MEMBERS_EXCEEDED)
                return@writeLock
            }
            if (plugin.proxy.getPlayer(opponentName) == null) {
                gp.sendMessage(EnumMessage.PLAYER_WHO_HAVE_THAT_NAME_NOT_EXISTS_OR_IN_OFFLINE)
                return@writeLock
            }
            val opponentUuid = plugin.proxy.getPlayer(opponentName)!!.uniqueId
            val opponent = cache.getGuildPlayer(opponentUuid)!!
            gp.sendMessage(EnumMessage.SENT_INVITATION, PlaceHolderReplace("{player}", opponent.name))
            opponent.invitationBox.addInvitation(Invitation(gp.name, guild.uniqueId, guild.name))
        }
    }


    // TODO add support of inviting offline player
//    suspend fun inviteOfflineGuildPlayer(gpSupplier: OnlineGuildPlayerSupplier, opponentName: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//
//        }.join()
//    }
}