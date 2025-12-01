package io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.Round

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.ArenaComponent

abstract class Round(
    arena: ArenaBase
) : ArenaComponent(arena) {

    var roundState: RoundState = RoundState.READY
        private set

    enum class RoundState {
        READY,
        RUNNING,
        FINISHED
    }

    var whenStarted: Long = 0
        private set

    fun startRound() {
        if (this.roundState == RoundState.READY) {
            this.roundState = RoundState.RUNNING

            this.whenStarted = System.currentTimeMillis()

            this.onRoundStart()
        } else
            throw IllegalStateException("State should be ready")
    }
    abstract fun onRoundStart()

    var whenFinished: Long = 0
        private set

    fun finishRound() {
        if (this.roundState == RoundState.RUNNING) {
            this.roundState = RoundState.FINISHED

            this.whenFinished = System.currentTimeMillis()

            this.onRoundFinish()
        }
        else
            throw IllegalStateException("State should be running")
    }
    abstract fun onRoundFinish()
}
