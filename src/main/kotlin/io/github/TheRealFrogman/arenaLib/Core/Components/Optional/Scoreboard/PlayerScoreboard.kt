package io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent
import java.util.Collections
import java.util.HashMap
import java.util.Map

class PlayerScoreboard(arena: ArenaBase) : ArenaComponent(arena), IScoreboard<ArenaPlayer> {

    private val scoreboard: MutableMap<ArenaPlayer, Long> = HashMap()

    init {
        this.arena.players.forEach { scoreboard.computeIfAbsent(it) { 0 } }
    }

    override fun addScore(player: ArenaPlayer, amount: Long) {
        scoreboard.put(player, scoreboard.get(player)!! + amount)
    }

    override fun setScore(player: ArenaPlayer, score: Long) {
        scoreboard.put(player, score)
    }

    override fun getScore(player: ArenaPlayer): Long? = this.scoreboard[player]

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