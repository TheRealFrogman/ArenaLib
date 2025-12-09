package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent
import io.github.TheRealFrogman.arenaLib.Core.Utilities.Countdown
import java.util.UUID

abstract class Round(
    arena: ArenaBase,
    private val canTimeoutWhenRunning: Boolean,
) : ArenaComponent(arena) {

    abstract fun onPrepare()
    abstract fun onPrepareTick(time: Long)
    abstract fun onStart()
    abstract fun onDuringRoundTick(time: Long)
    abstract fun onDowntime()
    abstract fun onDowntimeTick(time: Long)
    abstract fun onFinish()
    abstract fun onPrePause()
    abstract fun onPrePauseTick(time: Long)
    abstract fun onPause()
    abstract fun onDuringPauseTick(time: Long)
    abstract fun onPostPause()
    abstract fun onPostPauseTick(time: Long)
    abstract fun onUnpause()

    var state: State = State.READY
        private set

    enum class State {
        READY,
        PREPARING,
        RUNNING,
        PRE_PAUSE,
        PAUSED,
        POST_PAUSE,
        DOWNTIME,
        FINISHED,
    }

    val uuid = UUID.randomUUID()

    // получать только когда арена завершилась
    val id: String
        get() = when {
            whenStarted == 0L -> throw IllegalStateException("Arena not started")
            whenFinished == 0L -> throw IllegalStateException("Arena not finished")
            else -> "${whenStarted}_${whenFinished}"
        }

    var whenStarted: Long = 0
        private set

    var whenFinished: Long = 0
        private set

    var duringRoundCountdown: Countdown? = null
    var prepareCountdown: Countdown? = null
    var prePauseCountdown: Countdown? = null
    var pauseCountdown: Countdown? = null
    var postPauseCountdown: Countdown? = null
    var downtimeCountdown: Countdown? = null

    private fun runDuringRoundCountdown(roundTime: Long, downtimeTime: Long, onCountdownDepleted: () -> Unit = {}) {
        duringRoundCountdown = Countdown(
            roundTime,
            {
                runDowntimeCountdown(downtimeTime)
                this.state = State.DOWNTIME
                onDowntime()
            },
            ::onDuringRoundTick,
            {},
            {},
            this.arena.plugin
        )
        duringRoundCountdown!!.start()
    }

    private fun runPrepareCountdown(prepareTime: Long, roundTime: Long, downtimeTime: Long) {
        prepareCountdown = Countdown(
            prepareTime,
            {
                runDuringRoundCountdown(roundTime, downtimeTime)
                this.state = State.RUNNING
                this.whenStarted = System.currentTimeMillis()
                onStart()
            },
            ::onPrepareTick,
            {},
            {},
            this.arena.plugin
        )
        prepareCountdown!!.start()
    }

    fun runPauseCountdown(pauseTime: Long, postPauseTime: Long) {
        pauseCountdown = Countdown(
            pauseTime,
            {
                runPostPauseCountdown(postPauseTime)
                this.state = State.POST_PAUSE
                onPostPause()
            },
            ::onDuringPauseTick,
            {},
            {},
            this.arena.plugin
        )
        pauseCountdown!!.start()
    }

    fun runPrePauseCountdown(prePauseTime: Long, pauseTime: Long, postPauseTime: Long) {
        prePauseCountdown = Countdown(
            prePauseTime,
            {
                runPauseCountdown(pauseTime, postPauseTime)
                this.state = State.PAUSED
                onPause()
            },
            ::onPrePauseTick,
            {},
            {},
            this.arena.plugin
        )
        prePauseCountdown!!.start()
    }

    fun runPostPauseCountdown(postPauseTime: Long) {
        postPauseCountdown = Countdown(
            postPauseTime,
            {
                duringRoundCountdown?.resume()
                this.state = State.RUNNING
                onUnpause()
            },
            ::onPostPauseTick,
            {},
            {},
            this.arena.plugin
        )
        postPauseCountdown!!.start()
    }

    fun runDowntimeCountdown(downtimeTime: Long) {
        downtimeCountdown = Countdown(
            downtimeTime,
            {
                this.state = State.FINISHED
                this.whenFinished = System.currentTimeMillis()

                onFinish()
                cancelAllTimers()
            },
            ::onDowntimeTick,
            {},
            {},
            this.arena.plugin
        )
        downtimeCountdown!!.start()
    }

    fun start(prepareTime: Long, roundTime: Long, downtimeTime: Long) {
        if (!canStart)
            throw IllegalStateException("Can't start")

        runPrepareCountdown(prepareTime, roundTime, downtimeTime)
        this.state = State.PREPARING
        onPrepare()
    }

    val canStart get() = this.state == State.READY

    // попросить нейросеть написать это
    fun finish() {
        if (!canFinish)
            throw IllegalStateException("State should be running")

        this.cancelAllTimers()
        this.whenFinished = System.currentTimeMillis()
        this.state = State.FINISHED

        onFinish()
    }

    val canFinish get() = this.state == State.RUNNING

    fun timeout(prePauseTime: Long, pauseTime: Long, postPauseTime: Long) {
        if (!canTimeout)
            throw IllegalStateException("Can't timeout")

        if (this.state == State.PREPARING) {
            prepareCountdown?.reset()
            prepareCountdown?.stop()
            runPauseCountdown(pauseTime, postPauseTime)
        }

        if (this.state == State.RUNNING) {
            duringRoundCountdown?.pause()
            runPrePauseCountdown(prePauseTime, pauseTime, postPauseTime)
        }

        this.state = State.PRE_PAUSE
        onPrePause()
    }

    val canTimeout get() = (canTimeoutWhenRunning && state == State.RUNNING) || state == State.PREPARING

    fun pause() {
        if (!canPause)
            throw IllegalStateException("Can't pause")

        duringRoundCountdown?.pause()
        this.state = State.PAUSED
        onPause()
    }

    val canPause get() = this.state == State.RUNNING

    fun unpause() {
        if (!canUnpause)
            throw IllegalStateException("Can't unpause")

        duringRoundCountdown?.resume()
        this.state = State.RUNNING
        onUnpause()
    }

    val canUnpause get() = this.state == State.PAUSED || this.state == State.PRE_PAUSE

    fun cancelAllTimers() {
        try {

            prepareCountdown?.reset()
            prepareCountdown?.stop()

            duringRoundCountdown?.reset()
            duringRoundCountdown?.stop()

            downtimeCountdown?.reset()
            downtimeCountdown?.stop()

            prePauseCountdown?.reset()
            prePauseCountdown?.stop()

            postPauseCountdown?.reset()
            postPauseCountdown?.stop()

            pauseCountdown?.reset()
            pauseCountdown?.stop()

        } catch (_: Exception) {
            // ignore
        }
    }
}
