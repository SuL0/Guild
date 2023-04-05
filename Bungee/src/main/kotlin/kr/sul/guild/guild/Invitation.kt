package kr.sul.guild.guild

import kr.sul.guild.command.InviteAcceptCommand
import kr.sul.guild.command.InviteDenyCommand
import kr.sul.guild.message.EnumMessage
import kr.sul.guild.message.PlaceHolderReplace
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import java.sql.Timestamp
import java.util.*

class Invitation(
    private val fromWho: String,
    val guildUuid: UUID,
    val guildName: String,
    timestamp: Timestamp?=null

) {
    val timestamp = timestamp ?: Timestamp(System.currentTimeMillis())
    val randomUuid = UUID.randomUUID()!!

    companion object {
        fun init(acceptCommand: InviteAcceptCommand) {
            this.acceptCommand = acceptCommand.parentSubCommand
        }
        fun init(denyCommand: InviteDenyCommand) {
            this.denyCommand = denyCommand.parentSubCommand
        }
        private var acceptCommand = "§4§lERROR-NOT_INIT"
        private var denyCommand = "§4§lERROR-NOT_INIT"
    }

    fun sendMessage(gp: OnlineGuildPlayer) {
        val jsonMsg = arrayListOf<TextComponent>().apply {
            add(TextComponent(EnumMessage.INVITATION_ARRIVED.getProcessedMessage(
                PlaceHolderReplace("{guildName}", guildName)
            )))
            add(TextComponent(" §f( §a수락 ").apply {
                hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§a수락하기"))
                clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "$acceptCommand $randomUuid")
            })

            add(TextComponent("/"))
            add(TextComponent(" §c거절 §f) ").apply {
                hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§c거절하기"))
                clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "$denyCommand $randomUuid")
            })
            add(TextComponent("§a§l<- 마우스 클릭"))
        }
        gp.p.sendMessage(*jsonMsg.toTypedArray())
    }
}