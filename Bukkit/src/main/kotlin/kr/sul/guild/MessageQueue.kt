package kr.sul.guild

import kr.sul.guild.messagequeue.GuildCreationFeeCommand
import kr.sul.guild.messagequeue.GuildCreationFeeResponse
import kr.sul.guild.messagequeue.GuildTopicName
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.redisson.Redisson

class MessageQueue(private val econ: Economy) {
    private val redisson = Redisson.create()
    private val guildTopic = redisson.getTopic(GuildTopicName.topicName)


    init {
        guildTopic.addListener(GuildCreationFeeCommand::class.java) { channel, msg ->
            // Check if request is expired(In the situation of Bukkit server got significant tps drop, so got request lately)
            if (msg.isExpired()) {
                return@addListener
            }

            // 번지코드에 엮여있는 서버들 중 실제로 해당 플레이어가 접속중인 서버(단 1개)
            val p = Bukkit.getPlayer(msg.pUuid)
                ?: return@addListener

            // At first, try to take item for fee
            val correspondingItems = arrayListOf<ItemStack>()
            for (invItem in p.inventory.contents.filterNotNull()) {
                if ((invItem as CraftItemStack).handle.tagOrDefault.hasKey(msg.itemFee.nbtKeyForIdentification)) {
                    if (correspondingItems.sumOf { it.amount } + invItem.amount >= msg.itemFee.amount) {
                        if (correspondingItems.isNotEmpty()) {
                            invItem.amount -= msg.itemFee.amount - correspondingItems.sumOf { it.amount }
                            correspondingItems.forEach {
                                it.amount = 0
                            }
                        } else {
                            invItem.amount -= msg.itemFee.amount
                        }

                        guildTopic.publish(GuildCreationFeeResponse(msg.protocolId, msg.pUuid, true))
                        return@addListener
                    } else {
                        correspondingItems.add(invItem)
                    }
                }
            }

            // pay with money
            val response = econ.withdrawPlayer(p, msg.correspondingVaultAmount)
            guildTopic.publish(GuildCreationFeeResponse(msg.protocolId, msg.pUuid, response.transactionSuccess()))
        }
    }
}