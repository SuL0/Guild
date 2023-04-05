package kr.sul.guild.command

import kotlinx.coroutines.*
import kr.sul.guild.messagequeue.ItemStackSimplified
import kr.sul.guild.Main.Companion.plugin
import kr.sul.guild.Main.Companion.storageCache
import kr.sul.guild.MessageQueue
import kr.sul.guild.command.annotation.IsNotBelongInGuild
import kr.sul.guild.guild.JoinedGuildInfo
import kr.sul.guild.guild.OnlineGuildPlayer
import kr.sul.guild.guild.RankInGuild
import kr.sul.guild.message.EnumMessage
import kr.sul.guild.message.PlaceHolderReplace
import kr.sul.guild.storage.StorageLock
import revxrsal.commands.annotation.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.random.Random

// TODO 플레이어 스레드에서 지연이 발생하면 모든 행동이 지연되기 때문에,
//  커맨드 실행용 스레드는 단일 스레드로 한 개 새로 생성하는 게 좋지 않으려나??
//  -> 락 있는데 굳이 성능을 떨어뜨릴 필요가? (컨텍스트 스위칭이 성능을 크게 먹기도 하고)

@Command("길드")
class CreateCommand: GuildCommandWithHelp(
    "create",
    "<길드명>"
), AsyncCommand {

    companion object {
        private const val GUILD_CREATION_ITEM_FEE_NBT_KEY = "Guild.GuildCreationItemFee"
        private const val GUILD_CREATION_VAULT_FEE = 10000.0
        private val guildCreationItemFee = ItemStackSimplified(GUILD_CREATION_ITEM_FEE_NBT_KEY, 1)

        private val guildNameInProcess = arrayListOf<String>()  // 대금 지불 절차가 async 이기 때문에 일단 락 해두고 있는 길드이름
    }

    @Subcommand("생성")
    @Cooldown(2, unit = TimeUnit.SECONDS)
    fun execute(
        @IsNotBelongInGuild
        gpSupplier: OnlineGuildPlayerSupplier,
        guildName: String
    ) {
        CoroutineScope(Dispatchers.IO).launch coroutine@ {
            var logicPassed: Boolean? = null
            StorageLock.writeLock(gpSupplier.p) {
                val gp = gpSupplier.checkAndGet()
                if (guildName.length > 4) {
                    gp.sendMessage(EnumMessage.GUILD_NAME_MUST_NOT_BE_MORE_THAN_FOUR_LETTERS)
                    return@writeLock
                }
                if (containForbiddenCharacter(guildName)) {
                    gp.sendMessage(EnumMessage.GUILD_NAME_SHOULD_NOT_CONTAIN_BLANK_OR_SPECIAL_CHARACTER)
                    return@writeLock
                }

                if (!cache.isThisGuildNameCanUse(guildName) || guildNameInProcess.contains(guildName)) {
                    gp.sendMessage(EnumMessage.GUILD_NAME_OVERLAP)
                    return@writeLock
                }
                guildNameInProcess.add(guildName) // writeLock 내부이므로 synchronized list 를 쓸 필요는 없음
                logicPassed = true
            }
            logicPassed ?: return@coroutine  // It doesn't look a nice design, But I don't think I have another way

            // 비용 지불
            val protocolId = Random.nextLong()
            val isTransactionSucceed = MessageQueue.publishGuildCreationFeeCommand(protocolId, gpSupplier.p.uniqueId, guildCreationItemFee, GUILD_CREATION_VAULT_FEE)

            // Be aware at player can may leave the server
            // The process in the below should be processed treating gp as offline.
            StorageLock.writeLock(gpSupplier.p) {
                val offlineGp = storageCache.getGuildPlayer(gpSupplier.p.uniqueId)!!
                if (!isTransactionSucceed) {
                    (offlineGp as? OnlineGuildPlayer)?.sendMessage(
                        EnumMessage.YOU_DONT_HAVE_ENOUGH_MONEY_FOR_CREATE_GUILD.getProcessedMessage(
                        PlaceHolderReplace("{money}", "$GUILD_CREATION_VAULT_FEE")
                    ))
                    return@writeLock
                }

                val guild = cache.createGuild(guildName, offlineGp.uniqueId)
                offlineGp.guildInfo = JoinedGuildInfo(guild.uniqueId, RankInGuild.OWNER)
                (offlineGp as? OnlineGuildPlayer)?.sendMessage("")
                (offlineGp as? OnlineGuildPlayer)?.sendMessage(EnumMessage.CREATED_GUILD1)
                (offlineGp as? OnlineGuildPlayer)?.sendMessage(
                    EnumMessage.CREATED_GUILD2,
                    PlaceHolderReplace("{player}", offlineGp.name),
                    PlaceHolderReplace("{guildName}", guildName)
                )
                plugin.proxy.broadcast(
                    EnumMessage.SOMEONE_CREATED_GUILD.getProcessedMessage(
                        PlaceHolderReplace("{player}", offlineGp.name),
                        PlaceHolderReplace("{guildName}", guildName)
                    )
                )
            }
            guildNameInProcess.remove(guildName)
        }
    }
    private fun containForbiddenCharacter(str: String): Boolean {
        val pattern = Pattern.compile("[ !@#$%^&*(),.?\":{}|<>]") // 공백 포함
        return pattern.matcher(str).find()
    }
}