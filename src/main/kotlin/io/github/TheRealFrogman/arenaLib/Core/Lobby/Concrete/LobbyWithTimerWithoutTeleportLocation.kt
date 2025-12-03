package io.github.TheRealFrogman.arenaLib.Core.Lobby.Concrete

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.Lobby.Abstract.LobbyWithTimer
import org.bukkit.plugin.java.JavaPlugin

class LobbyWithTimerWithoutTeleportLocation(
    arena: ArenaBase,
    minPlayers: Int,
    maxPlayers: Int,
    minPlayersToStart: Int,
    playerCountToSeconds: MutableList<PlayerCountToSeconds>,
    private val _onTimerRunExceptWhenDepleted: OnTimerRunExceptWhenDepleted,
    private val _onTimerDepleted: OnTimerDepleted,
    plugin: JavaPlugin,
) : LobbyWithTimer(arena, minPlayers, maxPlayers,minPlayersToStart, playerCountToSeconds, plugin) {

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