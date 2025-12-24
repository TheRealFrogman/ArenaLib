package io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.Arena
import java.util.Collections

class Scoreboard<T>(override val arena: Arena) : IScoreboard<T> {

    enum class Operation {
        ADD,
        SUBTRACT,
        SET
    }

    class ScoreChange<T>(
        val target: T,
        val amount: Long,
        val operation: Operation
    ) {
        val timestamp = System.currentTimeMillis()
    }

    private val scoreChanges: MutableMap<T, MutableList<ScoreChange<T>>> = mutableMapOf()

    override fun getScoreChanges(): Map<T, List<ScoreChange<T>>> {
        val immutableMap = mutableMapOf<T, List<ScoreChange<T>>>()

        for ((target, changes) in scoreChanges) {
            immutableMap[target] = changes.toList()
        }

        return Collections.unmodifiableMap(immutableMap)
    }

    override fun addScore(target: T, amount: Long) {
        scoreChanges.computeIfAbsent(target) { mutableListOf() }.add(ScoreChange(target, amount, Operation.ADD))
    }

    override fun subScore(target: T, amount: Long) {
        scoreChanges.computeIfAbsent(target) { mutableListOf() }.add(ScoreChange(target, amount, Operation.SUBTRACT))
    }

    override fun setScore(target: T, score: Long) {
        val currentScore = getScore(target) ?: 0L
        val delta = score - currentScore

        if (delta != 0L) {
            scoreChanges.computeIfAbsent(target) { mutableListOf() }
                .add(ScoreChange(target, delta, Operation.SET))
        } else {
            // Если устанавливаем то же значение - добавляем нулевую транзакцию
            scoreChanges.computeIfAbsent(target) { mutableListOf() }
                .add(ScoreChange(target, 0L, Operation.SET))
        }
    }

    override fun getScore(target: T): Long? {
        return scoreChanges[target]?.sumOf { it.amount }
    }

    override val leader: T?
        get() {
            val targets = scoreChanges.keys

            val scores = mutableMapOf<T, Long>()
            targets.forEach { target -> getScore(target)?.let { scores[target] = it } }

            return scores.maxByOrNull { it.value }?.key
        }

    override fun getFirstLeading(amount: Long): List<T> {
        val result = ArrayList<T>()

        val it = scoreChanges.entries.iterator()

        while (it.hasNext() && result.size < amount) {
            val entry = it.next()
            result.add(entry.key)
        }

        return result
    }

    override fun getAllSortedByScore(): MutableMap<T, Long> {
        val targets = scoreChanges.keys

        val scores = mutableMapOf<T, Long>()
        targets.forEach { target -> getScore(target)?.let { scores[target] = it } }

        return scores.entries
            .sortedByDescending { it.value }
            .associate { it.key to it.value }
            .toMutableMap()
    }
}