package kr.sul.guild.command

import kr.sul.guild.command.annotation.IsBelongInGuild
import kr.sul.guild.command.annotation.IsOwner
import kr.sul.guild.guild.OnlineGuildPlayer
import kr.sul.guild.message.EnumMessage
import kr.sul.guild.storage.StorageLock
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand

@Command("길드")
class DisbandCommand: GuildCommandWithHelp(
    "disband",
    ""
) {

    @Subcommand("해산")
    fun execute(
        @IsBelongInGuild
        @IsOwner
        gpSupplier: OnlineGuildPlayerSupplier
    ) {
        StorageLock.writeLock(null) {
            val gp = gpSupplier.checkAndGet()
            val joinedGuild = gp.guildInfo!!.guild
            // TODO Should ask "do you really want to disband your guild?"

            joinedGuild.getMembers()
                .forEach { member ->
                    joinedGuild.removeMember(member)
                }
            cache.deleteGuild(joinedGuild)
            gp.sendMessage(EnumMessage.GUILD_HAS_BEEN_DISBANDED)
            // Send message, the guild disbanded, to members of guild.
            for (member in joinedGuild.getMembersExceptOwner()) {
                if (member is OnlineGuildPlayer) {
                    member.sendMessage(EnumMessage.YOUR_GUILD_HAS_BEEN_DISBANDED)
                } else {
                    member.messageBox.addMessage(EnumMessage.YOUR_GUILD_HAS_BEEN_DISBANDED.getProcessedMessage())
                }
            }
        }
    }
}