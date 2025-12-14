package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent

class RoundManager(
    arena: ArenaBase,
) : ArenaComponent(arena) {

    protected val rounds = listOf<Round>()

    fun getRounds(): Array<Round> = rounds.toTypedArray()

    fun getRoundByNumber(number: Int): Round = rounds[number - 1]

    val roundCount get() = rounds.size

    private var currentRoundIndex = 0

    val currentRound: Round get() = rounds[currentRoundIndex]

    fun nextRound() {
        currentRoundIndex++
    }
    //сделать чтобы можно было создать массив из раундов сразу и дать каждому раунду время
    //по истечению времени в раунде будет выполняться переданная лямбда

    //todo то есть создавать раунды буду в менеджере. создание раундов будет внутри
}
