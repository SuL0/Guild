package kr.sul.guild.storage

import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock

// ReentrantLock 은 재진입 가능
object StorageLock {
    private val readWriteLock = ReentrantReadWriteLock()
    private val readLock = readWriteLock.readLock()
    private val writeLock = readWriteLock.writeLock()
    private val playerCurrentRunningCommand = hashMapOf<ProxiedPlayer, String>()  // Retain player to run command one by one.  It is used for async command that doesn't block player thread during process.

    // set p, only if it needs async logic.
    fun readLock(p: ProxiedPlayer?, block: () -> Unit) {
        lock(p, readLock, block)
    }
    fun writeLock(p: ProxiedPlayer?, block: () -> Unit) {
        lock(p, writeLock, block)
    }


    // Read/Write Lock 에 상관없이
    private fun lock(p: ProxiedPlayer?, lock: Lock, block: () -> Unit) {
        try {
            lock.tryLock()
            // Retain player to run command one by one.
            if (p != null) {
                if (playerCurrentRunningCommand.containsKey(p)) {
                    p.sendMessage("§c§lERROR: §4${playerCurrentRunningCommand[p]} §c명령어를 처리 중에 있습니다.")
                    return
                }
                playerCurrentRunningCommand[p] = Thread.currentThread().stackTrace[2].className ?: "UnknownCommand"  // TODO 클래스이름 잘 받아오는지 테스트
            }
            block.invoke()
        } finally {
            playerCurrentRunningCommand.remove(p)
            lock.unlock()
        }
    }
}