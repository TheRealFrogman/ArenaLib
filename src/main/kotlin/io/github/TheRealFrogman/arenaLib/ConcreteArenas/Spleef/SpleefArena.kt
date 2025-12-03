package io.github.TheRealFrogman.arenaLib.ConcreteArenas.Spleef

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.BlockBreakArenaWinnable
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.plugin.java.JavaPlugin

class SpleefArena(
    name: String,
    region: ArenaRegion,
    spawnPoints: MutableList<SpawnPoint>,
    override val maxPlayersPerTeam: Int,
    override val minPlayersPerTeam: Int,
    override val minPlayers: Int,
    override val maxPlayers: Int,
    teamsInitializers: MutableList<Team.Initializer>,
    plugin: JavaPlugin
) : BlockBreakArenaWinnable(name, region, spawnPoints, teamsInitializers, plugin) {

    override val blocksAllowedToBreak: List<Material> = listOf(Material.SNOW_BLOCK)

    override fun declareWinners(): List<ArenaPlayer> {
        TODO("Not yet implemented")
    }

    override fun onWin(winners: List<ArenaPlayer>) {
        TODO("Not yet implemented")
    }

    override fun onStart() {
        TODO("Not yet implemented")
    }

    override fun onLeave(arenaPlayer: ArenaPlayer) {
        TODO("Not yet implemented")
    }

    override fun onBukkitBlockBreak(arenaPlayer: ArenaPlayer, block: Block) {
        TODO("Not yet implemented")
    }

}