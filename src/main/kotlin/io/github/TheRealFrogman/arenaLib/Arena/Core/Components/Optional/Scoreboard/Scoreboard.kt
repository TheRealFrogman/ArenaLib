package io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Optional.Scoreboard

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.ArenaComponent
import java.util.Collections
import java.util.HashMap
import java.util.Map
//todo сделать из скорборда фасад который скрывает Solo и Team скорборды и все
//todo также сделать интерфейс
class Scoreboard(
    arena: ArenaBase,
    private val onScoreChanged: OnScoreChanged
) : ArenaComponent(arena) {

    fun interface OnScoreChanged {
        fun execute(player: ArenaPlayer, score: Int)
    }

    private val scoreboard: MutableMap<ArenaPlayer, Int> = HashMap()

    fun addScore(player: ArenaPlayer, amount: Int) {
        scoreboard.put(player, scoreboard.get(player)!! + amount)
        onScoreChanged.execute(player, scoreboard.get(player)!!)
    }

    fun setScore(player: ArenaPlayer, score: Int) {
        scoreboard.put(player, score)
        onScoreChanged.execute(player, score)
    }

    fun getScore(player: ArenaPlayer): Int = this.scoreboard.get(player)!!

    val leader: ArenaPlayer?
        get() = Collections.max<MutableMap.MutableEntry<ArenaPlayer, Int>?>(
            scoreboard.entries,
            Map.Entry.comparingByValue<ArenaPlayer, Int?>()
        ).key

    fun getFirstLeading(amount: Int): List<ArenaPlayer> {
        val result = ArrayList<ArenaPlayer>()

        val it = scoreboard.entries.iterator()

        while (it.hasNext() && result.size < amount) {
            val entry = it.next()
            result.add(entry.key)
        }

        return result
    }
}