package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent
import java.util.UUID

class Round(
    arena: ArenaBase,
    val roundPrepareTime: Long,
    val roundTime: Long,
    val prePauseTime: Long,
    val postPauseTime: Long,
    val roundDowntimeTime: Long,
    val onRoundPrepare: OnRoundPrepare,
    val onRoundStart: OnRoundStart,
    val onRoundDowntime: OnRoundDowntime,
    val onRoundFinish: OnRoundFinish,
    val onRoundPause: OnRoundPause
) : ArenaComponent(arena) {

    fun interface OnRoundStart {
        fun execute(round: Round)
    }

    fun interface OnRoundFinish {
        fun execute(round: Round)
    }

    fun interface OnRoundPause {
        fun execute(round: Round)
    }

    fun interface OnRoundDowntime {
        fun execute(round: Round)
    }

    fun interface OnRoundPrepare {
        fun execute(round: Round)
    }

    //todo сделать таймеры до начала раунда и во время раунда и после конца раунда

    var roundState: RoundState = RoundState.READY
        private set

    enum class RoundState {
        READY,
        PREPARING,
        RUNNING,
        PAUSED,
        DOWNTIME,
        FINISHED,
    }

    val uuid = UUID.randomUUID()
    val id: String get() {
        if (whenStarted == 0L)
            throw IllegalStateException("Arena not started")
        if (whenFinished == 0L)
            throw IllegalStateException("Arena not finished")

        return whenStarted.toString() + "_" + whenFinished.toString()
    }

    var whenStarted: Long = 0
        private set

    fun startRound() {
        if (this.roundState == RoundState.READY) {
            this.roundState = RoundState.PREPARING
            this.onRoundPrepare.execute(this)
            TODO("ПОСЛЕ ПРЕПЕРИНГА ПО ТАЙМЕРУ ДЕЛАТЬ RUNNING")
//            this.roundState = RoundState.RUNNING
//            this.onRoundStart.execute(this)
//            this.whenStarted = System.currentTimeMillis()


        } else if (this.roundState == RoundState.PAUSED) {
            this.onRoundPause.execute(this)
            TODO("НАЧИНАТЬ РАУНД СРАЗУ С ТАЙМЕРОМ POST PAUSE")
        } else
            throw IllegalStateException("State should be ready")
    }

    var whenFinished: Long = 0
        private set

    fun finishRound() {
        if (this.roundState == RoundState.RUNNING) {
            this.roundState = RoundState.DOWNTIME
            this.onRoundDowntime.execute(this)
            TODO("ПОСЛЕ ДАУНТАЙМА ПО ТАЙМЕРУ ДЕЛАТЬ FINISHED")
//            this.roundState = RoundState.FINISHED
//            this.whenFinished = System.currentTimeMillis()
//            this.onRoundFinish.execute(this)
        }
        else
            throw IllegalStateException("State should be running")
    }

    fun pauseRound() {
        if (this.roundState == RoundState.RUNNING) {
            this.roundState = RoundState.PAUSED
            TODO("ОСТАНОВИТЬ КАУНТДАУН")
            this.onRoundPause.execute(this)

        }
        else
            throw IllegalStateException("State should be running")
    }

    fun pauseWithTimer() {
        if (this.roundState == RoundState.RUNNING) {
            this.roundState = RoundState.PAUSED
            TODO("ЗАПУСТИТЬ ТАЙМЕР ПЕРЕД ПАУЗОЙ И ПО ТАЙМЕРУ ЗАПАУЗИТЬ")
            this.onRoundPause.execute(this)
        }
        else
            throw IllegalStateException("State should be running")
    }

}
