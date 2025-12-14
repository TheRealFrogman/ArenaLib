package io.github.TheRealFrogman.arenaLib.Core.Utilities

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class Countdown(
    var countdownTime: Long,
    private val onTimerDepleted: () -> Unit,
    private val onTimerRunExceptWhenDepleted: (currentTimer: Long) -> Unit,
    private val onPause: () -> Unit,
    private val onResume: () -> Unit,
    private val plugin: JavaPlugin,
) {

    private var task: BukkitTask? = null

    var elapsed = 0L
        private set

    fun reset() {
        elapsed = 0
    }

    val remaining: Long
        get() {
            val product = countdownTime - elapsed

            return if (product < 0) 0
            else product
        }

    var isRunning: Boolean = false
        private set

    var isPaused: Boolean = false
        private set

    fun start() {
        if (isRunning) return
        // Если была пауза — используем resume() для вызова onResume,
        // иначе обычный старт.
        if (isPaused) {
            resume()
            return
        }
        isPaused = false
        isRunning = true
        task = createRunnable().runTaskTimer(plugin, 0L, 20L)
    }

    fun stop() {
        if (!isRunning && !isPaused) return
        task?.cancel()
        task = null
        isRunning = false
        isPaused = false
    }
    fun pause() {
        if (!isRunning || isPaused) return
        task?.cancel()
        task = null
        isPaused = true
        isRunning = false
        onPause.invoke()
    }

    // Возобновление после паузы
    fun resume() {
        if (!isPaused) return
        isPaused = false
        isRunning = true
        task = createRunnable().runTaskTimer(plugin, 0L, 20L)
        onResume.invoke()
        return
    }

    fun setTime(newTime: Long, restart: Boolean = false) {
        countdownTime = newTime
        if (restart) {
            reset()
            stop()
            start()
        }
    }

    private fun createRunnable(): BukkitRunnable {
        return object : BukkitRunnable() {
            var firstRun = false

            override fun run() {
                // Это нужно чтобы на первом тике не прибавлялась секунда
                if (firstRun)
                    elapsed++

                if (!firstRun)
                    firstRun = true

                if (remaining <= 0L) {
                    stop()
                    onTimerDepleted.invoke()
                } else {
                    onTimerRunExceptWhenDepleted.invoke(remaining)
                }

            }
        }
    }
}