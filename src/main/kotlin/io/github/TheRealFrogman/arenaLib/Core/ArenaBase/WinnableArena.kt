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

    override fun onFinish() {
        winners += declareWinners()

        TODO("будто это смысла не имеет")
        onWin(winners)
    }

    private  var winners: MutableList<ArenaPlayer> = ArrayList()

    protected abstract fun declareWinners(): List<ArenaPlayer>

    protected abstract fun onWin(winners: List<ArenaPlayer>)
}
