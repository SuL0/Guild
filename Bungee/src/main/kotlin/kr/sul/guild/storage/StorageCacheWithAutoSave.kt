package kr.sul.guild.storage

import kr.sul.guild.Main.Companion.plugin
import java.util.concurrent.TimeUnit

class StorageCacheWithAutoSave(storage: Storage, autoSaveIntervalByMin: Int) : StorageCache(storage) {

    init {
        plugin.proxy.scheduler.schedule(plugin, {
            StorageLock.readLock(null) {
                saveCachesToDB()
            }
            StorageLock.writeLock(null) {
                clearUnnecessaryCaches()
            }
        }, autoSaveIntervalByMin.toLong(), autoSaveIntervalByMin.toLong(), TimeUnit.MINUTES)
    }


    private fun saveCachesToDB() {
        for (wrappedGuild in guildMap.values) {
            if (wrappedGuild.needToBeDeleted) {
                // try to delete on MySQL (It's not unconditional)
                // TODO
            } else {
                // update
                // TODO
            }
        }

        return
        TODO("Not implemented yet")
    }

    // clear unnecessary caches after saving to DB
    private fun clearUnnecessaryCaches() {
        // if any player in guild isn't online, remove them.
        for (wrappedGuild in guildMap.values) {
            if (wrappedGuild.needToBeDeleted
                || !wrappedGuild.guild.getMembers().any { p -> p.isOnline() }
            ) {
                guildUuidMapper.remove(wrappedGuild.guild.name)
                guildMap.remove(wrappedGuild.guild.uniqueId)
                wrappedGuild.guild.getMembers()
                    .map { it.uniqueId }
                    .forEach { pUUID ->
                        guildPlayerMap.remove(pUUID)
                    }
            }
        }
        // if player isn't belong to any guild and in offline, remove them.
        for (guildPlayer in guildPlayerMap.values) {
            if (guildPlayer.guildInfo == null && !guildPlayer.isOnline()) {
                guildPlayerMap.remove(guildPlayer.uniqueId)
            }
        }
    }
}