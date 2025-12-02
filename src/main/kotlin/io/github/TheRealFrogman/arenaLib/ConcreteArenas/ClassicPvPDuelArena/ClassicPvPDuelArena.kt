package io.github.TheRealFrogman.arenaLib.ConcreteArenas.ClassicPvPDuelArena

import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.KillPlayerArena
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard.PlayerScoreboard
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Spectators.Spectators
import io.github.TheRealFrogman.arenaLib.Core.Facets.ISessionedArena
import org.bukkit.plugin.java.JavaPlugin

class ClassicPvPDuelArena (
    name: String,
    region: ArenaRegion,
    spawnPoints: MutableList<SpawnPoint>,
    teamsInitializers: MutableList<Team.Initializer>,
    plugin: JavaPlugin,
) : KillPlayerArena(name, region, spawnPoints, teamsInitializers, plugin), ISessionedArena {

    override val maxPlayers = 2
    override val minPlayers = 2

    override val maxPlayersPerTeam = 1
    override val minPlayersPerTeam = 1

    //Optional components
    private val scoreboard = PlayerScoreboard(this, { player: ArenaPlayer, score: Int -> 123})
    private val spectators = Spectators(this,  {}, {})

    override fun onBukkitKill(killer: ArenaPlayer, victim: ArenaPlayer) {
        scoreboard.addScore(killer, 1)
        spectators.addSpectator(victim)
        //todo в конце раунда заспавнить на одной из точек спавна и сменить gamemode на adventure
        //todo сделать откат этих состояний как-то, запрограммировать можно где-то
    }

    override fun declareWinners(): List<ArenaPlayer> {
        if(scoreboard.leader == null)
            return listOf()
        else
            return listOf(scoreboard.leader!!)
    }

    override fun onWin(winners: List<ArenaPlayer>) {
        TODO("Not yet implemented")
    }

    override fun onStart() {
    }

    override fun onLeave(arenaPlayer: ArenaPlayer) {
        TODO("Not yet implemented")
    }

    override fun start(players: MutableSet<ArenaPlayer>) {
    }
}