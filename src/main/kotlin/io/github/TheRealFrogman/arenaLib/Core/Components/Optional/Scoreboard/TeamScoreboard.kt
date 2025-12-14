package io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team
import java.util.Collections
import java.util.HashMap
import java.util.Map

class TeamScoreboard(arena: ArenaBase) : ArenaComponent(arena), IScoreboard<Team> {

    private val scoreboard: MutableMap<Team, Long> = HashMap()

    init {
        this.arena.teams.forEach { scoreboard.computeIfAbsent(it) { 0 } }
    }

    override fun addScore(team: Team, amount: Long) {
        scoreboard.put(team, scoreboard.get(team)!! + amount)
    }

    override fun setScore(team: Team, score: Long) {
        scoreboard.put(team, score)
    }

    override fun getScore(team: Team): Long? = this.scoreboard[team]

    override val leader: Team?
        get() = Collections.max<MutableMap.MutableEntry<Team, Long>?>(
            scoreboard.entries,
            Map.Entry.comparingByValue<Team, Long>()
        )?.key

    override fun getFirstLeading(amount: Long): List<Team> {
        val result = ArrayList<Team>()

        val it = scoreboard.entries.iterator()

        while (it.hasNext() && result.size < amount) {
            val entry = it.next()
            result.add(entry.key)
        }

        return result
    }

    override fun getAllSortedByScore(): MutableMap<Team, Long> {
        return scoreboard.entries
            .sortedByDescending { it.value }
            .associate { it.key to it.value }
            .toMutableMap()
    }
}