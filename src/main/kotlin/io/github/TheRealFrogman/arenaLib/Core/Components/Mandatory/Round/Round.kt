package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent
import io.github.TheRealFrogman.arenaLib.Core.Utilities.Countdown
import java.util.UUID

abstract class Round(
    arena: ArenaBase,
    val prepareTime: Long,
    val roundTime: Long,
    val downtimeTime: Long,
    val prePauseTime: Long,
    val pauseTime: Long,
    val postPauseTime: Long,
    private val isTimeoutAllowed: Boolean,
    private val isPauseAllowed: Boolean,
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
        private set(value) {
            val old = field
            field = value
            onStateChange(old, value)
        }

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

    private fun runDuringRoundPhase(roundTime: Long, onCountdownDepleted: () -> Unit) {
        this.state = State.RUNNING

        duringRoundCountdown = Countdown(
            roundTime,
            onCountdownDepleted,
            ::onDuringRoundTick,
            {},
            {},
            this.arena.plugin
        )
        duringRoundCountdown!!.start()
    }

    private fun runPreparePhase(prepareTime: Long, onCountdownDepleted: () -> Unit) {
        this.state = State.PREPARING

        prepareCountdown = Countdown(
            prepareTime,
            onCountdownDepleted,
            ::onPrepareTick,
            {},
            {},
            this.arena.plugin
        )
        prepareCountdown!!.start()
    }

    private fun runPausePhase(pauseTime: Long, onCountdownDepleted: () -> Unit) {
        this.state = State.PAUSED

        pauseCountdown = Countdown(
            pauseTime,
            onCountdownDepleted,
            ::onDuringPauseTick,
            {},
            {},
            this.arena.plugin
        )
        pauseCountdown!!.start()
    }

    private fun runPrePausePhase(prePauseTime: Long, onCountdownDepleted: () -> Unit) {
        this.state = State.PRE_PAUSE

        prePauseCountdown = Countdown(
            prePauseTime,
            onCountdownDepleted,
            ::onPrePauseTick,
            {},
            {},
            this.arena.plugin
        )
        prePauseCountdown!!.start()
    }

    private fun runPostPausePhase(postPauseTime: Long, onCountdownDepleted: () -> Unit) {
        this.state = State.POST_PAUSE

        postPauseCountdown = Countdown(
            postPauseTime,
            onCountdownDepleted,
            ::onPostPauseTick,
            {},
            {},
            this.arena.plugin
        )
        postPauseCountdown!!.start()
    }

    private fun runDowntimePhase(downtimeTime: Long, onCountdownDepleted: () -> Unit) {
        this.state = State.DOWNTIME

        downtimeCountdown = Countdown(
            downtimeTime,
            onCountdownDepleted,
            ::onDowntimeTick,
            {},
            {},
            this.arena.plugin
        )
        downtimeCountdown!!.start()
    }

    private fun setFinished() {
        this.state = State.FINISHED
    }

    private fun setRunning() {
        this.state = State.RUNNING
    }

    private fun onStateChange (oldState: State, newState: State) {
        when (newState) {
            State.PREPARING -> {
                when(oldState) {
                    State.PAUSED -> {
                        if (prepareCountdown?.isPaused == true)
                            prepareCountdown?.resume()
                    }
                    else -> throw IllegalStateException("Unhandled state: $oldState")
                }
                onPrepare()
            }

            State.RUNNING -> {
                when(oldState) {
                    State.PREPARING -> {
                        this.whenStarted = System.currentTimeMillis()
                        onStart()
                    }
                    State.POST_PAUSE -> {

                        if (duringRoundCountdown?.isPaused == true)
                            duringRoundCountdown?.resume()

                        if (prepareCountdown?.isPaused == true)
                            prepareCountdown?.resume()

                        onUnpause()
                    }
                    State.PAUSED -> {

                    }
                    State.DOWNTIME -> {

                    }
                    else -> throw IllegalStateException("Unhandled state: $oldState")
                }
            }
            State.PRE_PAUSE -> {
                when(oldState) {
                    State.RUNNING -> {
                        duringRoundCountdown?.pause()
                    }

                    State.DOWNTIME -> {
                        TODO("как-то сделать перенос паузы")
                    }
                    else -> throw IllegalStateException("Unhandled state: $oldState")
                }
                onPrePause()
            }

            State.PAUSED -> {
                when(oldState) {
                    State.PREPARING -> {
                        prepareCountdown?.reset()
                        prepareCountdown?.pause()
                    }
                    State.RUNNING -> {
                        duringRoundCountdown?.pause()
                    }
                    State.DOWNTIME -> {
                        TODO("как-то сделать перенос паузы")
                    }
                    else -> throw IllegalStateException("Unhandled state: $oldState")
                }
                onPause()
            }

            State.POST_PAUSE -> {
                if (duringRoundCountdown?.isPaused == true)
                    duringRoundCountdown?.resume()

                onPostPause()
            }

            State.DOWNTIME -> onDowntime()

            State.FINISHED -> {
                this.whenFinished = System.currentTimeMillis()
                onFinish()
                cancelAllTimers()
            }
            else -> throw IllegalStateException("Unknown state: $newState")
        }
    }

    fun start() {
        if (!canStart)
            throw IllegalStateException("Can't start")

        runPreparePhase(prepareTime) {
            runDuringRoundPhase(roundTime) {
                runDowntimePhase(downtimeTime){
                    setFinished()
                }
            }
        }
    }

    val canStart get() = this.state == State.READY

    // попросить нейросеть написать это
    fun finish() {
        if (!canFinish)
            throw IllegalStateException("State should be running")

        setFinished()
    }

    val canFinish get() = this.state == State.RUNNING

    fun timeout() {
        if (!canTimeout)
            throw IllegalStateException("Can't timeout")

        if (this.state == State.PREPARING) {
            runPausePhase(pauseTime) {
                runPreparePhase(prepareTime) {
                    setRunning()
                }
            }
        }

        if (this.state == State.RUNNING) {
            runPrePausePhase(prePauseTime) {
                runPausePhase(pauseTime) {
                    runPostPausePhase(postPauseTime) {
                        setRunning()
                    }
                }
            }
        }
    }

    val canTimeout get() = isTimeoutAllowed && (canTimeoutWhenRunning && state == State.RUNNING) || state == State.PREPARING

    //todo этот метод переписать под чек всех подходящих стейтов и потом засунуть в onStateChange
    fun pause() {
        if (!canPause)
            throw IllegalStateException("Can't pause")

        duringRoundCountdown?.pause()
        this.state = State.PAUSED
        onPause()
    }

    val canPause get() = isPauseAllowed && this.state == State.RUNNING

    //todo этот метод переписать под чек всех подходящих стейтов и потом засунуть в onStateChange
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
