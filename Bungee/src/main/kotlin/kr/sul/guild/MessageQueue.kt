package kr.sul.guild

import kotlinx.coroutines.delay
import kr.sul.guild.Main.Companion.plugin
import kr.sul.guild.messagequeue.*
import org.redisson.Redisson
import org.redisson.api.RTopic
import org.redisson.api.RedissonClient
import java.util.*

object MessageQueue {
    // in another thread or JVM
    private val redisson: RedissonClient
    private var guildTopic: RTopic

    private val receivedMap = hashMapOf<Long, Any>() // It's only used for GuildCreationFeeResponse for now, but in further, it can use for any kind of respond have protocolId

    init {
        // To clarify Redis connection can be failed. -> plugin get disabled
        try {
            redisson = Redisson.create()
            guildTopic = redisson.getTopic(GuildTopicName.topicName)
        } finally {}
        guildTopic.addListener(GuildCreationFeeResponse::class.java) { _, msg ->
            receivedMap[msg.protocolId] = msg
        }
    }
    fun initialize() {}

    suspend fun publishGuildCreationFeeCommand(protocolId: Long, pUuid: UUID, itemFee: ItemStackSimplified, amount: Double): Boolean {
        val expireAt = System.currentTimeMillis() + 1000
        val guildCreationFeeCommand = GuildCreationFeeCommand(protocolId, pUuid, itemFee, amount, expireAt)
        guildTopic.publish(guildCreationFeeCommand)
        while (!guildCreationFeeCommand.isExpired()) {
            delay(10)
            if (receivedMap.containsKey(protocolId)) {
                val response = receivedMap[protocolId] as GuildCreationFeeResponse
                receivedMap.remove(protocolId)
                return response.isSucceed
            }
        }
        return false
    }
}