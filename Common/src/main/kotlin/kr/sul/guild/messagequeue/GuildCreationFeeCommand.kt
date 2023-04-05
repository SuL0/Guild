package kr.sul.guild.messagequeue

import java.util.*

class GuildCreationFeeCommand(
    protocolId: Long,
    pUuid: UUID,
    val itemFee: ItemStackSimplified,
    val correspondingVaultAmount: Double,
    val expireAt: Long  // Bungee측에서는 publish하고 일정시간 안에 Response가 안 오면 캔슬해야 하는데, 이 때 Bukkit서버가 렉으로 인하여 지연됐을 시 Bungee에서는 일정시간 기다려서 작업을 캔슬했음에도 Bukkit에서는 출금을 해버릴 수도 있기 때문(대가없는 출금만 일어나게됨)
): GuildCreationFeeProtocol(protocolId, pUuid) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= expireAt
    }
}