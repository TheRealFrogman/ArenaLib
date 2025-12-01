package io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.Round

import com.google.common.collect.ImmutableList
import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.ArenaComponent

class RoundManager(
    arena: ArenaBase,
    protected val rounds: ImmutableList<Round>
) : ArenaComponent(arena) {

    fun getRounds(): Array<Round> = rounds.toTypedArray()

    fun getRoundByNumber(number: Int): Round = rounds[number - 1]

    val roundCount = rounds.size

    private var currentRoundIndex = 0

    val currentRound: Round
        get() = rounds[currentRoundIndex]


    //сделать чтобы можно было создать массив из раундов сразу и дать каждому раунду время
    //по истечению времени в раунде будет выполняться переданная лямбда

    //todo то есть создавать раунды буду в менеджере. создание раундов будет внутри
}
