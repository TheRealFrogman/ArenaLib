package io.github.TheRealFrogman.arenaLib.Core.Lobby.Abstract

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.Arena
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Utilities.Countdown
import org.bukkit.plugin.java.JavaPlugin

abstract class LobbyWithTimer(
    arena: Arena,
    minPlayers: Int,
    maxPlayers: Int,
    private val minPlayersToStart: Int,
    private val playerCountToSeconds: MutableList<PlayerCountToSeconds>,
    plugin: JavaPlugin,
) : Lobby(arena, minPlayers, maxPlayers, plugin) {

    interface PlayerCountToSeconds {
        val playerCount: Int
        val seconds: Long
    }

    init {
        require(playerCountToSeconds.isNotEmpty())
        require(playerCountToSeconds.first().playerCount >= minPlayersToStart)
        require(playerCountToSeconds.first().seconds >= 1)
        require(playerCountToSeconds.last().playerCount <= maxPlayers)
    }

    private val countdown = Countdown(
        0,
        ::onTimerDepleted,
        ::onTimerRunExceptWhenDepleted,
        {},
        {},
        plugin,
    )

    override fun onJoinHook(player: ArenaPlayer) {
        if (players.size >= minPlayersToStart) {
            if (!countdown.isRunning) {
                countdown.start()
            }

            val newTime = getMappedCountdownTime()
            if (countdown.countdownTime != newTime) {
                countdown.countdownTime = newTime
                // если смена успешна — ресетим
                countdown.reset()
            }
        } else {
            // если ещё не достигли минимума, всё равно обновим время, но не стартуем
            val newTime = getMappedCountdownTime()
            if (countdown.countdownTime != newTime) {
                countdown.countdownTime = newTime
                countdown.reset()
            }
        }
    }

    override fun onLeaveHook(player: ArenaPlayer) {
        // If player count drops below minimum, stop the countdown
        if (players.size < minPlayersToStart) {
            countdown.stop()
            countdown.reset()
        }

        val newTime = getMappedCountdownTime()
        if (countdown.countdownTime != newTime) {
            countdown.countdownTime = newTime
            // если смена успешна — ресетим
            countdown.reset()
        }
    }

    abstract fun onTimerRunExceptWhenDepleted(currentTimer: Long)
    abstract fun onTimerDepleted()

    private fun getMappedCountdownTime(): Long {
        return playerCountToSeconds
            .filter { it.playerCount <= players.size }
            .maxByOrNull { it.playerCount }
            ?.seconds
            ?: 0
    }
}