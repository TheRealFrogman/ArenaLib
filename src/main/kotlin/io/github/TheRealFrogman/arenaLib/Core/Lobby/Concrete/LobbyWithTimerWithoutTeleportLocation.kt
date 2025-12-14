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
    private val _onTimerRunExceptWhenDepleted: (currentTimer: Long) -> Unit,
    private val _onTimerDepleted: () -> Unit,
    plugin: JavaPlugin,
) : LobbyWithTimer(arena, minPlayers, maxPlayers,minPlayersToStart, playerCountToSeconds, plugin) {

    //todo тут надо сделать, чтобы по истечению таймера игрок сразу телепортировался в арену
    override fun onTimerDepleted() {
        TODO("Not yet implemented")
        _onTimerDepleted.invoke()

    }

    override fun onTimerRunExceptWhenDepleted(currentTimer: Long) {
        TODO("Not yet implemented")
        _onTimerRunExceptWhenDepleted.invoke(currentTimer)
    }
}