package io.github.TheRealFrogman.arenaLib.Core.ArenaBase

import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team
import org.bukkit.plugin.java.JavaPlugin

abstract class WinnableArena(
    name: String,
    region: ArenaRegion,
    spawnPoints: MutableList<SpawnPoint>,
    teamsInitializers: MutableList<Team.Initializer>,
    plugin: JavaPlugin
) : ArenaBase(name, region, spawnPoints, teamsInitializers, plugin) {

//    protected fun declareWinners(potentialWinners: List<ArenaPlayer>) {
//        val results = checkWinCondition(potentialWinners)
//        require(results.all { it } ) { "condition not met" }
//        check(winners.isNotEmpty()) { "winners already set" }
//
//        this.winners += potentialWinners
//        onWin(this, potentialWinners)
//
//        // declareWinners само по себе означает что арена закрыта
//        // поэтому закрываем арену
//        super.finish()
//    }

    final override fun onFinish() {
        winners += declareWinners()

        TODO("будто это смысла не имеет")
        onWin(winners)
    }

    //todo сделать иммутабельный лист
    private  var winners: List<ArenaPlayer> = ArrayList()

    abstract fun declareWinners(): List<ArenaPlayer>

//    protected abstract fun checkWinCondition(probablyWinners: List<ArenaPlayer>): List<Boolean>

    protected abstract fun onWin(winners: List<ArenaPlayer>)
}
