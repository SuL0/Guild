package kr.sul.guild.exception

import revxrsal.commands.exception.ThrowableFromCommand

@ThrowableFromCommand
class ExitFromCommandException: RuntimeException() {

}