package kr.sul.guild.exception

import kr.sul.guild.command.DefaultGuildCommand
import kr.sul.guild.command.OnlineGuildPlayerSupplier
import kr.sul.guild.message.EnumMessage
import kr.sul.guild.message.PlaceHolderReplace
import revxrsal.commands.bungee.core.BungeeActor
import revxrsal.commands.bungee.exception.SenderNotPlayerException
import revxrsal.commands.command.CommandActor
import revxrsal.commands.exception.*
import java.util.concurrent.TimeUnit


// Tree 구조를 지닌 Command Tree가 필요함. 근데 이 데이터는 누가 넘겨줘야하지?
// -> CommandHelp가 담당하는 게 맞는 것 같은데?
object ExceptionHandler: CommandExceptionAdapter() {
    override fun cooldown(actor: CommandActor, exception: CooldownException) {
        (actor as BungeeActor).asPlayer()?.sendMessage(EnumMessage.COMMAND_COOLDOWN.getProcessedMessage(
            PlaceHolderReplace("{cooldown}", exception.getTimeLeft(TimeUnit.SECONDS).toString())
        ))
    }

    override fun tooManyArguments(actor: CommandActor, exception: TooManyArgumentsException) {
        printCommandHelp(actor)
        actor.reply(" §4§lERROR > §c인자가 필요한 양보다 많습니다.")
    }

    override fun noSubcommandSpecified(actor: CommandActor, exception: NoSubcommandSpecifiedException) {
//        printCommandHelp(actor)
        actor.reply(" §4§lERROR > §c인자가 모자랍니다.")
    }

    override fun invalidCommand(actor: CommandActor, exception: InvalidCommandException) {
        printCommandHelp(actor)
        actor.reply(" §4§lERROR > §c알 수 없는 명령어입니다.")
    }
    // .getParent(=CommandCategory) 는 마치 폴더같은 개념
    override fun missingArgument(actor: CommandActor, exception: MissingArgumentException) {
//        printCommandHelp(actor)
        actor.reply(" §4§lERROR > §c인자가 모자랍니다.")
    }

    fun senderNotPlayer(actor: CommandActor, exception: SenderNotPlayerException) {
        actor.reply("§c콘솔에서는 사용할 수 없습니다.")
    }
    fun exitFromCommandException(actor: CommandActor, exception: ExitFromCommandException) {
        return
        // Just do nothing
    }


    override fun commandInvocation(actor: CommandActor, exception: CommandInvocationException) {
        actor.error("명령어 실행 중 에러가 발생했습니다.")
        exception.cause.printStackTrace()
    }
    override fun onUnhandledException(actor: CommandActor, throwable: Throwable) {
        throwable.printStackTrace()
    }

    private fun printCommandHelp(actor: CommandActor) {
        actor as BungeeActor
        val p = actor.asPlayer() ?: return
        val gpSupplier = OnlineGuildPlayerSupplier(p)
        DefaultGuildCommand.execute(gpSupplier)
    }
}