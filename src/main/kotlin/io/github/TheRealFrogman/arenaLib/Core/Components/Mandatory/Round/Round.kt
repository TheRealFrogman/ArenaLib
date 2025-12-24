package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.Arena
import io.github.TheRealFrogman.arenaLib.Core.Components.Common.ArenaComponent
import io.github.TheRealFrogman.arenaLib.Core.Utilities.Countdown
import java.util.UUID

class Round(
    override val arena: Arena,
    val roundConfig: RoundConfig
) : ArenaComponent {

    enum class Event {
        PREPARE,
        START,
        DOWNTIME,
        FINISH,
        PRE_PAUSE,
        PAUSE,
        POST_PAUSE,
        UNPAUSE
    }

    enum class EventTimer {
        PREPARE_TIMER_TICK,
        DURING_ROUND_TIMER_TICK,
        DOWNTIME_TIMER_TICK,
        PRE_PAUSE_TIMER_TICK,
        PAUSE_TIMER_TICK,
        POST_PAUSE_TIMER_TICK,
    }

    data class RoundConfig(
        val prepareTime: Long,
        val roundTime: Long,
        val downtimeTime: Long,
        val prePauseTime: Long,
        val pauseTime: Long,
        val postPauseTime: Long,
        val isTimeoutAllowed: Boolean,
        val isPauseAllowed: Boolean,
        val canTimeoutWhenRunning: Boolean,
    )

    val prepareTime: Long get() = roundConfig.prepareTime
    val roundTime: Long get() = roundConfig.roundTime
    val downtimeTime: Long get() = roundConfig.downtimeTime
    val prePauseTime: Long get() = roundConfig.prePauseTime
    val pauseTime: Long get() = roundConfig.pauseTime
    val postPauseTime: Long get() = roundConfig.postPauseTime
    val isTimeoutAllowed: Boolean get() = roundConfig.isTimeoutAllowed
    val isPauseAllowed: Boolean get() = roundConfig.isPauseAllowed
    val canTimeoutWhenRunning: Boolean get() = roundConfig.canTimeoutWhenRunning

    private val listenersByEvent = mutableMapOf<Event, MutableList<Round.() -> Unit>>()
    private val listenersTimerByEvent = mutableMapOf<EventTimer, MutableList<Round.(Long) -> Unit>>()

    fun addListener(event: Event, listener: Round.() -> Unit) {
        listenersByEvent.computeIfAbsent(event) { mutableListOf() }.add(listener)
    }

    fun addListenerTimer(event: EventTimer, listener: Round.(Long) -> Unit) {
        listenersTimerByEvent.computeIfAbsent(event) { mutableListOf() }.add(listener)
    }

    fun removeAllListeners(event: Event) {
        listenersByEvent.remove(event)
    }

    fun removeAllListenersTimer(event: EventTimer) {
        listenersTimerByEvent.remove(event)
    }

    fun removeListener(event: Event, listener: Round.() -> Unit) {
        listenersByEvent[event]?.remove(listener)
    }

    fun removeListenerTimer(event: EventTimer,listener: Round.(Long) -> Unit) {
        listenersTimerByEvent[event]?.remove(listener)
    }

    fun addOnce(event: Event, listener: Round.() -> Unit) {
        listenersByEvent
            .computeIfAbsent(event) { mutableListOf() }
                .add({
                    listener()
                    listenersByEvent[event]?.remove(listener)
                })
    }

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
            { time -> listenersTimerByEvent[EventTimer.DURING_ROUND_TIMER_TICK]?.forEach { it(this, time) } },
            {},
            {},
            this.arena.plugin
        )
        duringRoundCountdown!!.start()
    }

    private fun runPreparePhase(prepareTime: Long, onCountdownDepleted: () -> Unit) {
        this.state = State.PREPARING
        listenersByEvent[Event.PREPARE]?.forEach { it(this) }

        prepareCountdown = Countdown(
            prepareTime,
            onCountdownDepleted,
            { time -> listenersTimerByEvent[EventTimer.PREPARE_TIMER_TICK]?.forEach { it(this, time) } },
            {},
            {},
            this.arena.plugin
        )
        prepareCountdown!!.start()
    }

    private fun runPausePhase(pauseTime: Long, onCountdownDepleted: () -> Unit) {
        this.state = State.PAUSED
        listenersByEvent[Event.PAUSE]?.forEach { it(this) }

        pauseCountdown = Countdown(
            pauseTime,
            onCountdownDepleted,
            { time -> listenersTimerByEvent[EventTimer.PAUSE_TIMER_TICK]?.forEach { it(this, time) } },
            {},
            {},
            this.arena.plugin
        )
        pauseCountdown!!.start()
    }

    private fun runPrePausePhase(prePauseTime: Long, onCountdownDepleted: () -> Unit) {
        this.state = State.PRE_PAUSE
        listenersByEvent[Event.PRE_PAUSE]?.forEach { it(this) }

        prePauseCountdown = Countdown(
            prePauseTime,
            onCountdownDepleted,
            { time -> listenersTimerByEvent[EventTimer.PRE_PAUSE_TIMER_TICK]?.forEach { it(this, time) } },
            {},
            {},
            this.arena.plugin
        )
        prePauseCountdown!!.start()
    }

    private fun runPostPausePhase(postPauseTime: Long, onCountdownDepleted: () -> Unit) {
        this.state = State.POST_PAUSE
        listenersByEvent[Event.POST_PAUSE]?.forEach { it(this) }

        postPauseCountdown = Countdown(
            postPauseTime,
            onCountdownDepleted,
            { time -> listenersTimerByEvent[EventTimer.POST_PAUSE_TIMER_TICK]?.forEach { it(this, time) } },
            {},
            {},
            this.arena.plugin
        )
        postPauseCountdown!!.start()
    }

    private fun runDowntimePhase(downtimeTime: Long, onCountdownDepleted: () -> Unit) {
        this.state = State.DOWNTIME
        listenersByEvent[Event.DOWNTIME]?.forEach { it(this) }

        downtimeCountdown = Countdown(
            downtimeTime,
            onCountdownDepleted,
            { time -> listenersTimerByEvent[EventTimer.DOWNTIME_TIMER_TICK]?.forEach { it(this, time) } },
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

        when(oldState to newState) {
            State.READY to State.PREPARING -> {}

            State.PREPARING to State.RUNNING -> {
                this.whenStarted = System.currentTimeMillis()

                listenersByEvent[Event.START]?.forEach { it(this) }
            }

            State.PRE_PAUSE to State.PAUSED -> {
                prePauseCountdown?.reset()
                prePauseCountdown?.resume()
            }
            State.PAUSED to State.PREPARING -> {
                if (prepareCountdown?.isPaused == true) {
                    prepareCountdown?.reset()
                    prepareCountdown?.resume()
                }

                pauseCountdown?.reset()
                pauseCountdown?.pause()
            }
            State.PAUSED to State.POST_PAUSE -> {
                pauseCountdown?.reset()
                pauseCountdown?.pause()
            }
            State.PREPARING to State.PAUSED -> {
                prepareCountdown?.reset()
                prepareCountdown?.pause()

            }
            State.POST_PAUSE to State.RUNNING -> {
                if (duringRoundCountdown?.isPaused == true)
                    duringRoundCountdown?.resume()

                listenersByEvent[Event.UNPAUSE]?.forEach { it(this) }
            }
            State.RUNNING to State.DOWNTIME -> {
                this.whenFinished = System.currentTimeMillis()

                duringRoundCountdown?.reset()
                duringRoundCountdown?.pause()

            }
            State.RUNNING to State.PRE_PAUSE -> {
                duringRoundCountdown?.pause()
            }

            else -> throw IllegalStateException("Unhandled state transition: $oldState -> $newState")
        }

        when (newState) {
            State.FINISHED -> {
                listenersByEvent[Event.FINISH]?.forEach { it(this) }
                cancelAllTimers()
            }
            else -> {}
        }
    }

    fun start() {
        check(canStart) { "Can't start" }

        runPreparePhase(prepareTime) {
            runDuringRoundPhase(roundTime) {
                runDowntimePhase(downtimeTime) {
                    setFinished()
                }
            }
        }
    }

    val canStart
        get() = this.state == State.READY

    fun finish() {
        setFinished()
    }

    fun timeout() {
        check(canTimeout) { "Can't timeout" }

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

    val canTimeout get() = isTimeoutAllowed && ((canTimeoutWhenRunning && state == State.RUNNING) || state == State.PREPARING)

    private var stateBeforePause: State? = null
    fun pause() {
        stateBeforePause = this.state
        this.state = State.PAUSED
    }

    fun unpause() {
        checkNotNull(stateBeforePause) { "Can't unpause" }
        this.state = stateBeforePause!!
    }

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
