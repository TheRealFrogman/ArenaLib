package io.github.TheRealFrogman.arenaLib.Core.Utilities

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class Countdown(
    private val plugin: JavaPlugin,
    var countdownTime: Int,
    private val onTimerDepleted: OnTimerDepleted,
    private val onTimerRunExceptWhenDepleted: OnTimerRunExceptWhenDepleted
) {

    fun interface OnTimerDepleted {
        fun execute()
    }

    fun interface OnTimerRunExceptWhenDepleted {
        fun execute(currentTimer: Int)
    }

    private val task: BukkitRunnable = object : BukkitRunnable() {
        override fun run() {
            elapsed++

            if (remaining == 0) {
                stopCountdown()
                onTimerDepleted.execute()
            } else {
                onTimerRunExceptWhenDepleted.execute(remaining)
            }
        }
    }

    var elapsed = 0

    fun reset() {
        elapsed = 0
    }

    val remaining: Int
        get() {
            val product = countdownTime - elapsed

            return if (product < 0) {
                0
            } else
                product
        }

    var isRunning: Boolean = false
        private set

    fun startCountdown() {
        if (this.isRunning)
            return
        //run every second
        task.runTaskTimer(plugin, 0, 20)
        this.isRunning = true
    }

    fun stopCountdown() {
        if (this.isRunning == false)
            return

        task.cancel()
        this.isRunning = false
    }
}