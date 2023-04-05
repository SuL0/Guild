package kr.sul.guild.guild

import kr.sul.guild.Main.Companion.storageCache
import java.util.*

class InvitationBox(
    private val pUUID: UUID,
    val setting: SETTING,
    private val invitationList: ArrayList<Invitation>
) {
    val gp: IOfflineGuildPlayer
        get() = storageCache.getGuildPlayer(pUUID)!!

    enum class SETTING {
        DENY_ALL,
        DEFAULT
    }

    fun clearAll() {
        invitationList.clear()
    }

    fun sendAll() {
        val gp = this.gp
        if (invitationList.isEmpty()) return
        if (gp is OnlineGuildPlayer) {
            invitationList.forEach { invitation ->
                invitation.sendMessage(gp)
            }
        }
    }
    fun find(invitationUuid: UUID): Invitation? {
        return invitationList.find { it.randomUuid == invitationUuid }
    }

    fun addInvitation(invitation: Invitation): Boolean {
        if (setting == SETTING.DENY_ALL) return false
        val gp = this.gp
        // 길드 UUID 로 중복 제거
        if (invitationList.removeIf { it.guildUuid == invitation.guildUuid })
        invitationList.add(invitation)
        invitationList.sortWith(compareBy { it.timestamp })
        if (gp is OnlineGuildPlayer) {
            invitation.sendMessage(gp)
        }
        return true
    }


    fun remove(invitation: Invitation) {
        invitationList.remove(invitation)
    }
}