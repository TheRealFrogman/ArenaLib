package io.github.TheRealFrogman.arenaLib.Core.Lobby.Concrete

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.Arena
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Lobby.Abstract.LobbyWithTimer
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin

class LobbyWithTimerAndTeleportLocation(
    arena: Arena,
    minPlayers: Int,
    maxPlayers: Int,
    minPlayersToStart: Int,
    playerCountToSeconds: MutableList<PlayerCountToSeconds>,
    private val teleportLocation: Location,
    private val _onTimerRunExceptWhenDepleted: (currentTimer: Long) -> Unit,
    private val _onTimerDepleted: () -> Unit,
    plugin: JavaPlugin,
) : LobbyWithTimer(
    arena,
    minPlayers,
    maxPlayers,
    minPlayersToStart,
    playerCountToSeconds,
    plugin
) {

    override fun onJoinHook(player: ArenaPlayer) {
        super.onJoinHook(player)

        player.teleportWithBackup(teleportLocation)
    }

    override fun onLeaveHook(player: ArenaPlayer) {
        super.onLeaveHook(player)
        player.restoreLocation()
    }

    override fun onTimerRunExceptWhenDepleted(currentTimer: Long) {
        _onTimerRunExceptWhenDepleted.invoke(currentTimer)
    }

    override fun onTimerDepleted() {
        _onTimerDepleted.invoke()
    }
}