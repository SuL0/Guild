package kr.sul.guild.command

import net.bytebuddy.utility.dispatcher.JavaDispatcher
import net.md_5.bungee.api.connection.ProxiedPlayer

// 플레이어 쓰레드는 각각 async 이지만, 이가 의미하는 바는 커맨드를 처리할 때
//  StorageLock.read or writeLock 스코프(사실상 싱글스레드처럼 만듦) 안에서 모든 프로세스를 처리할 수 있는지 없는지를 나타내는 것임


// TODO 이게 아니라 Storage.tryLock()에 Player을 넣고, 여기서 전체 락 관리와 플레이어당 실행중인 커맨드를 1개로 제한시켜야 함
interface AsyncCommand {
    fun startOfCommand(p: ProxiedPlayer): Boolean {
        if (AsyncCommandHandler.isRunningCommand(p)) return false
        AsyncCommandHandler.startOfCommand(p)
        return true
    }
    fun endOfCommand(p: ProxiedPlayer) {
        AsyncCommandHandler.endOfCommand(p)
    }
}