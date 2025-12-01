package io.github.TheRealFrogman.arenaLib.Arena.Core.Lobby.Concrete

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Arena.Core.Lobby.Abstract.LobbyWithTimer
import org.bukkit.plugin.java.JavaPlugin

class LobbyWithTimerWithoutTeleportLocation(
    plugin: JavaPlugin,
    arena: ArenaBase,
    minPlayersToStart: Int,
    playerCountToSeconds: MutableMap<Int, Int>,
    private val _onTimerRunExceptWhenDepleted: OnTimerRunExceptWhenDepleted,
    private val _onTimerDepleted: OnTimerDepleted
) : LobbyWithTimer(plugin, arena, minPlayersToStart, playerCountToSeconds) {

    fun interface OnTimerRunExceptWhenDepleted {
        fun execute(сurrentTimer: Int)
    }

    fun interface OnTimerDepleted {
        fun execute()
    }

    //todo тут надо сделать, чтобы по истечению таймера игрок сразу телепортировался в арену
    override fun onTimerDepleted() {
        TODO("Not yet implemented")
        _onTimerDepleted.execute()

    }

    override fun onTimerRunExceptWhenDepleted(сurrentTimer: Int) {
        TODO("Not yet implemented")
        _onTimerRunExceptWhenDepleted.execute(сurrentTimer)
    }
}