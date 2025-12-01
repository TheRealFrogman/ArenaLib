package io.github.TheRealFrogman.arenaLib.Core.Lobby.Concrete

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Lobby.Abstract.LobbyWithTimer
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin

class LobbyWithTimerAndTeleportLocation(
    arena: ArenaBase,
    minPlayers: Int,
    maxPlayers: Int,
    minPlayersToStart: Int,
    playerCountToSeconds: MutableMap<Int, Int>,
    private val teleportLocation: Location,
    private val _onTimerRunExceptWhenDepleted: OnTimerRunExceptWhenDepleted,
    private val _onTimerDepleted: OnTimerDepleted,
    plugin: JavaPlugin,
) : LobbyWithTimer(
    arena,
    minPlayers,
    maxPlayers,
    minPlayersToStart,
    playerCountToSeconds,
    plugin
) {

    fun interface OnTimerRunExceptWhenDepleted {
        fun execute(сurrentTimer: Int)
    }

    fun interface OnTimerDepleted {
        fun execute()
    }

    override fun onJoinHook(player: ArenaPlayer) {
        super.onJoinHook(player)

        player.teleportWithBackup(teleportLocation)
    }

    override fun onLeaveHook(player: ArenaPlayer) {
        super.onLeaveHook(player)
        player.restoreLocation()
    }

    override fun onTimerRunExceptWhenDepleted(сurrentTimer: Int) {
        _onTimerRunExceptWhenDepleted.execute(сurrentTimer)
    }

    override fun onTimerDepleted() {
        _onTimerDepleted.execute()
    }
}