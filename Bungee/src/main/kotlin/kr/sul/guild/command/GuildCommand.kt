package kr.sul.guild.command

import kr.sul.guild.Main
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Subcommand

// Command annotation에 @Inherited 가 없어서 자식 클래스가 해당 어노테이션을 자동으로 가지지 않음
//@Command("길드")
abstract class GuildCommand {
    companion object {
        @JvmStatic protected val cache = Main.storageCache
    }
    val parentCommand: List<String> = let {
        return@let try {
            this::class.java.getAnnotation(Command::class.java).value.toList()
        } catch (e: Exception) {
            listOf("§4§lERROR-REFLECTION§f")
        }
    }
    val subCommand: List<String> = let {
        return@let try {
            // TODO 클래스에서 execute 메소드 순서 (첫번째에서 두번째) 바꿔도 잘 인식하는지 테스트
//            val commandMethod = this::class.java.methods.first { it.parameters.any { that -> that.type == OnlineGuildPlayerSupplier::class.java } }
            val commandMethod = this::class.java.methods.firstOrNull { it.getAnnotation(Subcommand::class.java) != null }
            commandMethod?.getAnnotation(Subcommand::class.java)?.value?.toList()
                ?: listOf("")
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            listOf("§4§lERROR-REFLECTION§f")
        }
    }
    val parentSubCommand = let {
        val strBuilder = StringBuilder("")
        for (parent in parentCommand) {
            for (sub in subCommand) {
                if (strBuilder.isNotEmpty()) {
                    strBuilder.append(", ")
                }
                if (sub == "") {
                    strBuilder.append("/$parent")
                } else {
                    strBuilder.append("/$parent $sub")
                }
            }
        }
        strBuilder.toString()
    }
}
