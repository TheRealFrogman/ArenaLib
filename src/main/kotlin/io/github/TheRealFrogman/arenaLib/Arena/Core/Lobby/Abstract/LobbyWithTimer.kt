package io.github.TheRealFrogman.arenaLib.Arena.Core.Lobby.Abstract

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Arena.Core.Utilities.Countdown
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.text.get

abstract class LobbyWithTimer(
    arena: ArenaBase,
    minPlayers: Int,
    maxPlayers: Int,
    private val minPlayersToStart: Int,
    private val playerCountToSeconds: MutableMap<Int, Int>,
    plugin: JavaPlugin,
) : Lobby(arena, minPlayers, maxPlayers, plugin) {

    init {
        require(playerCountToSeconds.isNotEmpty())
        require(playerCountToSeconds.entries.first().key >= minPlayersToStart)
        require(playerCountToSeconds.entries.first().value >= 1)
        require(playerCountToSeconds.entries.last().key <= maxPlayers)
    }

    private val countdown = Countdown(
        plugin,
        ::onTimerDepleted,
        ::onTimerRunExceptWhenDepleted
    )

    override fun onJoinHook(player: ArenaPlayer) {
        if (players.size >= minPlayersToStart) {
            if (!countdown.isRunning) 
                countdown.startCountdown()

            val seconds: Int = playerCountToSeconds[players.size]!!

            countdown.countdownTime = seconds - countdown.elapsed
        }
    }

    override fun onLeaveHook(player: ArenaPlayer) {
        // If player count drops below minimum, stop the countdown
        if (players.size < minPlayersToStart) {
            countdown.stopCountdown()
            countdown.resetElapsed()
        }

        val seconds: Int = playerCountToSeconds[players.size]!!

        countdown.countdownTime = seconds - countdown.elapsed
    }

    abstract fun onTimerRunExceptWhenDepleted(сurrentTimer: Int)
    abstract fun onTimerDepleted()
}