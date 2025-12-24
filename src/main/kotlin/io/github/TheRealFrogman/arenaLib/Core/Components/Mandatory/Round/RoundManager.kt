package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.Arena
import io.github.TheRealFrogman.arenaLib.Core.Components.Common.ArenaComponent
import java.util.Collections

class RoundManager(
    override val arena: Arena,
) : ArenaComponent {

    enum class Event {
        INIT
    }

    private val listeners = mutableMapOf<Event, MutableList<RoundManager.() -> Unit>>()
    fun addListener(event: Event, listener: RoundManager.() -> Unit) {
        listeners.computeIfAbsent(event) { mutableListOf() }.add(listener)
    }

    var initialized = false
        private set

    protected val rounds = mutableListOf<Round?>()

    fun getRounds() = Collections.unmodifiableList(rounds)

    //можно получить раунд и привязать слушатель
    fun getRoundByNumber(number: Int): Round = rounds[number - 1] ?: throw IllegalStateException("Round $number not found")

    val roundCount get() = rounds.size

    var currentRoundIndex = 0
        private set

    val currentRound: Round get() = rounds[currentRoundIndex] ?: throw IllegalStateException("Current round not found")

    val lastRound get() = rounds.last()

    val hasNext: Boolean get() = currentRoundIndex < rounds.size - 1

    fun nextRound(): Round {
        if (!hasNext)
            return currentRound

        currentRoundIndex++
        currentRound.start()
        return currentRound
    }

    fun timeoutCurrentRound() {
        if (currentRound.canTimeout) {
            if (currentRound.state == Round.State.DOWNTIME) {
                if (hasNext) {
                    val round = rounds[currentRoundIndex + 1] ?: return

                    round.addOnce(Round.Event.START, { round.timeout() })
                }
            } else {
                currentRound.timeout()
            }
        }
    }

    fun startRoundSequence() {

        for(i in 0 until rounds.size) {
            //!! потому что мы берем размер массива
            val round = rounds[i]!!

            val nextRound = rounds[i + 1]

            if (nextRound == null) {
                round.addListener(Round.Event.FINISH, { arena.finish() })
                return
            }

            round.addListener(Round.Event.FINISH, { nextRound.start() })
        }

        rounds[0]?.start()
    }

    fun initIdenticalRounds(count: Int, config: Round.RoundConfig) {
        check(!initialized) { "Already initialized" }

        for(i in count - 1 downTo 0) {
            rounds.add(Round(arena, config))
        }

        initialized = true
        listeners[Event.INIT]?.forEach { it() }
    }

    fun initMappedRounds(configs: List<Round.RoundConfig>){
        check(!initialized) { "Already initialized" }

        configs.forEach { rounds.add(Round(arena, it)) }

        initialized = true
        listeners[Event.INIT]?.forEach { it() }
    }
}
