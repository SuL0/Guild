package kr.sul.guild.command

import kr.sul.guild.Main.Companion.config
import kr.sul.guild.guild.OnlineGuildPlayer
import kr.sul.guild.guild.RankInGuild

abstract class GuildCommandWithHelp(
    private val configNodeName: String,
    private val argsString: String
): GuildCommand() {
    init {
        if (helpSupportingCommandList.any { it::class.java == this::class.java }) throw Exception("Same command has tried to register")
        helpSupportingCommandList.add(this)
    }

    val preMadeHelpForm = HelpFormBuilder(
        this.parentSubCommand,
        argsString,
        config.getString("commands.$configNodeName")
    )


    // 다른 요소와 강하게 의존관계가 형성되었고 하드코딩됨. 펄미션부분을 간결한 방법으로 분리할 수 있으면 좋겠는데
    companion object {
        private val helpSupportingCommandList = arrayListOf<GuildCommandWithHelp>()

        fun printCommandHelps(gp: OnlineGuildPlayer) {
            gp.sendMessage("")
            gp.sendMessage("")
            gp.sendMessage(" §a◎  §f§l마인토피아 길드 명령어")
            gp.sendMessage("")
            if (gp.guildInfo == null) {
                val createCommand = helpSupportingCommandList.first { it::class.java == CreateCommand::class.java }
                val helpLine = createCommand.preMadeHelpForm.getResult("§2")
                gp.sendMessage("$helpLine  §b§l↙도움말 확인↙ |")
                gp.sendMessage("§b§l[도움말] - -")
                gp.sendMessage("§7▶ 길드 생성 비용은 ${config.getString("guild_creation_fee")} Gold가 필요합니다.")
                gp.sendMessage("§7▶ 인벤토리상에서 길드 창설권이 있을 경우 창설권이 먼저 사용됩니다.")
                gp.sendMessage("§7▶ 길드명은 특수문자를 제외한 4글자로 제한됩니다.")
                return
            }

            // It runs only If player is belonged in guild
            val playerRank = gp.guildInfo!!.playerRank
            val filterNotPredicate: (GuildCommandWithHelp) -> (Boolean) = if (playerRank == RankInGuild.OWNER) {
                {
                    it::class.java == CreateCommand::class.java
                }
            } else {
                {
                    it::class.java == CreateCommand::class.java
                            || it::class.java == DisbandCommand::class.java
                }
            }
            for ((i, command) in helpSupportingCommandList.filterNot(filterNotPredicate).withIndex()) {
                // 짝홀 색깔
                val color = if (command::class.java == DisbandCommand::class.java) {
                    "§c"
                } else {
                    if (i % 2 == 0) "§a" else "§2"
                }

                val helpLine = command.preMadeHelpForm.getResult(color)
                if (command::class.java == DisbandCommand::class.java) {  // sends one blanked line
                    gp.sendMessage("")
                }
                gp.sendMessage(helpLine)
            }
        }
    }

    class HelpFormBuilder(command: String, args: String, description: String) {
        private val defaultForm = config.getString("commands.helpMessageForm")
        private var result = defaultForm
        init {
            result = result.replace("{command}", command)
            if (args == "") {
                result = result.replace("{args} ", "{args}")
            }
            result = result.replace("{args}", args)
            result = result.replace("{description}", description)
        }

        fun getResult(colored: String): String {
            return result.replace("{color}", colored)
        }
    }
}