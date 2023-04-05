package kr.sul.guild.command

import kr.sul.guild.command.annotation.IsBelongInGuild
import kr.sul.guild.guild.Guild
import kr.sul.guild.guild.IOfflineGuildPlayer
import kr.sul.guild.guild.OnlineGuildPlayer
import kr.sul.guild.storage.StorageLock
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Default
import revxrsal.commands.annotation.Subcommand
import java.text.SimpleDateFormat
import kotlin.math.ceil
import kotlin.math.max

@Command("길드")
// TODO 여기 Json 메세지를 통해 추방도 할 수 있도록
class MemberListCommand: GuildCommandWithHelp(
    "memberList",
    "§7[페이지(default=1)]§f"
) {

    @Subcommand("길드원")
    fun execute(
        @IsBelongInGuild
        gpSupplier: OnlineGuildPlayerSupplier,
        @Default("1") page: Int,
    ) {
        lateinit var gp: OnlineGuildPlayer
        // 이 명령어는 무결성 필요없긴 한데. 락 없앨까
        StorageLock.readLock(null) {
            gp = gpSupplier.checkAndGet()
        }
        // TODO 이거 잘 되나? 위에 이거 뜨는데 Wrapped into a reference object to be modified when captured in a closure
        val guild = gp.guildInfo!!.guild
        MemberListPage(gp, guild, page).sendMessage("/${this.parentCommand} ${this.subCommand}")
    }


    class MemberListPage(
        private val gp: OnlineGuildPlayer,
        private val guild: Guild,
        private val page: Int,
    ) {
        private val p = gp.p

        companion object {
            private const val LINES_PER_PAGE = 10
            private val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        }


        fun sendMessage(memberListCommandStr: String) {
            p.sendMessage("")
            p.sendMessage("")
            p.sendMessage(toPlayerInfoFormat(gp))
            p.sendMessage("§a[${guild.name}] §f< §2직책 §f| §e초대권한 §f| §c추방권한 §f| §7마지막 접속일 §f>")
            p.sendMessage("§7- - - - - - - - - - - - - - -")
            val members = guild.getMembers()
            if (guild.getMembersExceptOwner().isEmpty()) {
                p.sendMessage("§7길드원이 없습니다. 길드원을 모집해 보세요!")
            } else {
                for ((i, member) in members
                    .filterNot { it == gp }
                    .drop((page-1)* LINES_PER_PAGE)
                    .take(page*LINES_PER_PAGE).withIndex()) {
                    val index = page*LINES_PER_PAGE+i
                    p.sendMessage("§9${index} ${toPlayerInfoFormat(member)}")
                }
            }

            val jsonMsg = arrayListOf<TextComponent>().apply {
                add(TextComponent("§f§l..........§e+${max(members.size-20, 0)} §f명의 §2길드원§f들"))
                add(TextComponent("  §f§l< ").apply {
                    if (page > 1) {
                        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§e${page-1} §f페이지로 이동"))
                        clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "$memberListCommandStr ${page-1}")
                    } else {
                        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§c이전 페이지가 없습니다."))
                    }
                })
                val lastPage = ceil(members.size/ LINES_PER_PAGE.toDouble()).toInt()
                add(TextComponent("${page}/${lastPage} 페이지"))
                add(TextComponent(" §f§l>  ").apply {
                    if (page < lastPage) {
                        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§e${page+1} §f페이지로 이동"))
                        clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "$memberListCommandStr ${page+1}")
                    } else {
                        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§c다음 페이지가 없습니다."))
                    }
                })
            }
            p.sendMessage(*jsonMsg.toTypedArray())
            p.sendMessage("§b§l[도움말]")
            p.sendMessage("§7▶ §c§l클릭§f하여 §2직책§f과 §e초대권한§f과 §c추방권한§f을 §a변경§f할 수 있습니다.")
        }
        private fun toPlayerInfoFormat(member: IOfflineGuildPlayer): String {
            val canInvite = if (member.guildInfo!!.playerRank.canInvite) "O" else "X"
            val canKick = if (member.guildInfo!!.playerRank.canKick) "O" else "X"
            val formattedDate =
                if (!member.isOnline()) dateFormatter.format(member.lastJoined)
                else "§aONLINE"
            val rank = ChatColor.stripColor(member.guildInfo!!.playerRank.displayName)
            return "§9${member.name} §f| ${member.guildInfo!!.playerRank} §f| $canInvite | $canKick | §7$formattedDate §7< $rank >"
        }
    }
}

