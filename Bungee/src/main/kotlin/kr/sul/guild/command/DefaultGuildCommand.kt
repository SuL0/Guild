package kr.sul.guild.command

import kr.sul.guild.storage.StorageLock
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Default

@Command("길드")
object DefaultGuildCommand: GuildCommand() {
    @Default
    fun execute(gpSupplier: OnlineGuildPlayerSupplier, ) {
        StorageLock.readLock(null) {
            val gp = gpSupplier.checkAndGet()
            GuildCommandWithHelp.printCommandHelps(gp)
        }
    }
}