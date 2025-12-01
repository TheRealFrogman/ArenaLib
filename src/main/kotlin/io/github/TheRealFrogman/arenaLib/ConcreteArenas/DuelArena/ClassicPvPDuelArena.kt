package io.github.TheRealFrogman.arenaLib.ConcreteArenas.DuelArena

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.KillPlayerArena
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard.Scoreboard
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Spectators.Spectators
import io.github.TheRealFrogman.arenaLib.Core.Facets.ISessionedArena
import org.bukkit.plugin.java.JavaPlugin

class ClassicPvPDuelArena (
    name: String,
    region: ArenaRegion,
    spawnPoints: MutableList<SpawnPoint>,
    teams: MutableList<Team>,
    plugin: JavaPlugin,
) : KillPlayerArena(name, region, spawnPoints, teams, plugin), ISessionedArena {

    //Optional components
    private val scoreboard = Scoreboard(this, { player: ArenaPlayer, score: Int -> 123})
    private val spectators = Spectators(this,  {}, {})

    override val maxPlayers = 2
    override val minPlayers = 2

    override val maxPlayersPerTeam = 1
    override val minPlayersPerTeam = 1

    init {
        require(teams.all { it.players.size == minPlayersPerTeam} )
        require(teams.all { it.players.size == maxPlayersPerTeam} )
    }

    override fun onBukkitKill(killer: ArenaPlayer, victim: ArenaPlayer) {
        scoreboard.addScore(killer, 1)
        spectators.addSpectator(victim)
        //todo в конце раунда заспавнить на одной из точек спавна и сменить gamemode на adventure
        //todo сделать откат этих состояний как-то, запрограммировать можно где-то
    }

    override fun declareWinners(): List<ArenaPlayer> {
        TODO("Not yet implemented")
    }

//    override fun checkWinCondition(probablyWinner: ArenaPlayer): Boolean {
//        val leader = scoreboard.leader ?: return false
//
//        return leader.bukkitPlayerUniquieId == probablyWinner.bukkitPlayerUniquieId
//    }

    override fun onWin(
        arena: ArenaBase,
        winners: List<ArenaPlayer>
    ) {
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