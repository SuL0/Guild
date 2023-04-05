package kr.sul.guild

import kr.sul.guild.Main.Companion.plugin
import kr.sul.guild.command.*
import kr.sul.guild.command.annotation.*
import kr.sul.guild.exception.ExceptionHandler
import revxrsal.commands.bungee.BungeeCommandActor
import revxrsal.commands.bungee.core.BungeeHandler

object Lamp {
    fun initialize() {
        val guildCommandHandler = BungeeHandler(plugin)
        guildCommandHandler.registerContextResolver(OnlineGuildPlayerSupplier::class.java) { command  ->
            val actor = command.actor<BungeeCommandActor>()
            actor.requirePlayer()  // 명령어 사용자를 플레이어로 제한
            return@registerContextResolver OnlineGuildPlayerSupplier(actor.asPlayer()!!)
        }
        guildCommandHandler.exceptionHandler = ExceptionHandler
        // ParameterValidator 이란 parameter인 OnlineGuildPlayerSupplier에 annotation을 닮으로서 어떤 사항을 강제하게 해주는 것
        guildCommandHandler.registerParameterValidator(OnlineGuildPlayerSupplier::class.java) { value, param, actor ->
            if (param.hasAnnotation(IsBelongInGuild::class.java)) {
                value.checkIsBelongInGuild(true)
            }
            if (param.hasAnnotation(IsNotBelongInGuild::class.java)) {
                value.checkIsNotBelongInGuild(true)
            }
            if (param.hasAnnotation(CanKick::class.java)) {
                value.checkCanKick(true)
            }
            if (param.hasAnnotation(CanInvite::class.java)) {
                value.checkCanInvite(true)
            }
            if (param.hasAnnotation(IsOwner::class.java)) {
                value.checkIsOwner(true)
            }
        }
        listOf(
            DefaultGuildCommand,
            InviteAcceptCommand(),
            InviteDenyCommand(),
            CreateCommand(),
            MemberListCommand(),
            InviteCommand(),
            GuildChatCommand(),
            KickCommand(),
            LeaveCommand(),
            DisbandCommand()
        ).forEach { command ->
//            guildCommandHandler.register(Orphans.path(command.getParentCommand()).handler(command))
            guildCommandHandler.register(command)
        }
        plugin.proxy.broadcast("cmd: ${guildCommandHandler.commands}")  // 이거써서 Command Guide 만들어야겠는데
        plugin.proxy.broadcast("c: ${guildCommandHandler.categories}")
        plugin.proxy.broadcast("r: ${guildCommandHandler.rootPaths}")
        // Is this occur error?
//        plugin.proxy.broadcast("chat: ${GuildChatCommand::class.java.methods.first().getAnnotation(Command::class.java).value}")
    }
}