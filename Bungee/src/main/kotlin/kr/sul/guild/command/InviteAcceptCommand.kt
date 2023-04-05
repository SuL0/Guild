package kr.sul.guild.command

import kr.sul.guild.command.annotation.IsNotBelongInGuild
import kr.sul.guild.guild.Invitation
import kr.sul.guild.message.EnumMessage
import kr.sul.guild.message.PlaceHolderReplace
import kr.sul.guild.storage.StorageLock
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand
import java.util.*

@Command("길드")
class InviteAcceptCommand: GuildCommand() {
    @Subcommand("초대수락")
    fun execute(
        @IsNotBelongInGuild
        gpSupplier: OnlineGuildPlayerSupplier,
        invitationUuid: String
    ) {
        StorageLock.writeLock(null) {
            val gp = gpSupplier.checkAndGet()
            val invitation = gp.invitationBox.find(UUID.fromString(invitationUuid))
            if (invitation == null) {
                gp.sendMessage(EnumMessage.CANNOT_FIND_THAT_INVITATION)
                return@writeLock
            }
            val invitationGuild = cache.getGuild(invitation.guildUuid)!!
            val result = invitationGuild.addMember(gp)
            if (!result) {
                 gp.sendMessage(EnumMessage.GUILD_MEMBERS_EXCEEDED)
                return@writeLock
            }
            gp.sendMessage(EnumMessage.YOU_ARE_NOW_A_GUILD_MEMBER, PlaceHolderReplace("{guildName}", invitationGuild.name))
            invitationGuild.getMembers().filterNot { it == gp }.forEach { member ->
                member.sendMessage(EnumMessage.NEW_GUILD_MEMBER_IS_ARRIVED, PlaceHolderReplace("{player}", gp.name))
            }
        }
    }

    init {
        Invitation.init(this)
    }
}