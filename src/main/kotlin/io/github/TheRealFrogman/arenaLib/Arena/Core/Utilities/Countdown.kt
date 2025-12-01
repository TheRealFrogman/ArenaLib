package io.github.TheRealFrogman.arenaLib.Arena.Core.Utilities

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class Countdown(
    private val plugin: JavaPlugin,
    private val onTimerDepleted: OnTimerDepleted,
    private val onTimerRunExceptWhenDepleted: OnTimerRunExceptWhenDepleted
) {

    fun interface OnTimerDepleted {
        fun execute()
    }

    fun interface OnTimerRunExceptWhenDepleted {
        fun execute(сurrentTimer: Int)
    }

    private val task: BukkitRunnable = object : BukkitRunnable() {
        override fun run() {
            elapsed++

            if (remaining == 0) {
                this.cancel()
                onTimerDepleted.execute()
            } else {
                onTimerRunExceptWhenDepleted.execute(remaining)
            }
        }
    }

    var elapsed: Int = 0
        private set

    var countdownTime: Int = 0

    val remaining: Int
        get() = countdownTime - elapsed

    fun resetElapsed(): Int {
        return 0.also { elapsed = it }
    }

    var isRunning: Boolean = false
        private set

    fun startCountdown() {
        //run every second
        task.runTaskTimer(plugin, 0, 20)
//        this.updateCountdownTime()
        this.isRunning = true
    }

    fun stopCountdown() {
        task.cancel()
//        this.updateCountdownTime()
        this.isRunning = false
    }
}