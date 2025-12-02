package io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard

interface IScoreboard<T> {

    fun addScore(type: T, amount: Long)

    fun setScore(type: T, score: Long)

    fun getScore(type: T): Long

    val leader: T?

    fun getFirstLeading(amount: Long): List<T>

    fun getAllSortedByScore(): MutableMap<T, Long>
}