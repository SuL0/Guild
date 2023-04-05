package kr.sul.guild.message

import com.google.gson.JsonObject
import kr.sul.guild.guild.OnlineGuildPlayer
import kr.sul.guild.storage.JsonSerializable
import org.json.simple.parser.JSONParser

// important messages associated with guild that happened when player is in offline
class MessageBox: JsonSerializable<MessageBox> {
    private val messageList = arrayListOf<String>()
    fun addMessage(str: String) {
        messageList.add(str)
    }
    fun sendAll(onlineGuildPlayer: OnlineGuildPlayer) {
        if (messageList.isEmpty()) return

        onlineGuildPlayer.p.sendMessage("")
        onlineGuildPlayer.p.sendMessage("§2§n §f §f §f §c§l수신된 길드 메시지§2§n §f §f §f §f §f §f §f ")
        for (msg in messageList) {
            onlineGuildPlayer.p.sendMessage(msg)
        }
        onlineGuildPlayer.p.sendMessage("")
        messageList.clear()
    }

    override fun load(jsonStr: String): MessageBox {
        val json = JSONParser().parse(jsonStr) as JsonObject
        MessageBox().run {

        }
    }

    override fun save(): String {
        TODO("Not yet implemented")
    }
}