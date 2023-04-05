package kr.sul.guild.command

import net.md_5.bungee.api.connection.ProxiedPlayer

object AsyncCommandHandler {
    private val commandPlayerList = arrayListOf<ProxiedPlayer>()
    fun startOfCommand(p: ProxiedPlayer) {
        if (commandPlayerList.contains(p)) throw Exception("This player is already running another command")
        commandPlayerList.add(p)
    }
    fun endOfCommand(p: ProxiedPlayer) {
        commandPlayerList.remove(p)
    }
    fun isRunningCommand(p: ProxiedPlayer): Boolean {
        return !commandPlayerList.contains(p)
    }
}