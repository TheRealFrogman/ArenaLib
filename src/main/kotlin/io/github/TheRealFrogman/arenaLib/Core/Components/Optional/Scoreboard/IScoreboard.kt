package io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard

import io.github.TheRealFrogman.arenaLib.Core.Components.Common.ArenaComponent
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard.Scoreboard.ScoreChange

interface IScoreboard<T> : ArenaComponent {

    fun addScore(type: T, amount: Long)

    fun subScore(type: T, amount: Long)

    fun setScore(type: T, score: Long)

    fun getScore(type: T): Long?

    fun getScoreChanges(): Map<T, List<ScoreChange<T>>>

    val leader: T?

    fun getFirstLeading(amount: Long): List<T>

    fun getAllSortedByScore(): MutableMap<T, Long>
}