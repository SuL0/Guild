package kr.sul.guild.command

import kr.sul.guild.guild.Invitation
import kr.sul.guild.message.EnumMessage
import kr.sul.guild.storage.StorageLock
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand
import java.util.*

@Command("길드")
// Generally this command autocompleted by invitation Json command.
class InviteDenyCommand: GuildCommand() {
    @Subcommand("초대거절")
    fun execute(
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
            gp.sendMessage(EnumMessage.DENY_INVITATION)
            gp.invitationBox.remove(invitation)
        }
    }

    init {
        Invitation.init(this)
    }
}