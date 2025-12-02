package io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent
import java.util.Collections
import java.util.HashMap
import java.util.Map
//todo сделать из скорборда фасад который скрывает Solo и Team скорборды и все
//todo также сделать интерфейс
class PlayerScoreboard(
    arena: ArenaBase,
    private val onScoreChanged: OnScoreChanged
) : ArenaComponent(arena), IScoreboard<ArenaPlayer> {

    fun interface OnScoreChanged {
        fun execute(player: ArenaPlayer, score: Long)
    }

    private val scoreboard: MutableMap<ArenaPlayer, Long> = HashMap()

    override fun addScore(player: ArenaPlayer, amount: Long) {
        scoreboard.put(player, scoreboard.get(player)!! + amount)
        onScoreChanged.execute(player, scoreboard.get(player)!!)
    }

    override fun setScore(player: ArenaPlayer, score: Long) {
        scoreboard.put(player, score)
        onScoreChanged.execute(player, score)
    }

    override fun getScore(player: ArenaPlayer): Long = this.scoreboard.get(player)!!

    override val leader: ArenaPlayer?
        get() = Collections.max<MutableMap.MutableEntry<ArenaPlayer, Long>?>(
            scoreboard.entries,
            Map.Entry.comparingByValue<ArenaPlayer, Long?>()
        )?.key

    override fun getFirstLeading(amount: Long): List<ArenaPlayer> {
        val result = ArrayList<ArenaPlayer>()

        val it = scoreboard.entries.iterator()

        while (it.hasNext() && result.size < amount) {
            val entry = it.next()
            result.add(entry.key)
        }

        return result
    }

    override fun getAllSortedByScore(): MutableMap<ArenaPlayer, Long> {
        return scoreboard.entries
            .sortedByDescending { it.value }
            .associate { it.key to it.value }
            .toMutableMap()
    }
}