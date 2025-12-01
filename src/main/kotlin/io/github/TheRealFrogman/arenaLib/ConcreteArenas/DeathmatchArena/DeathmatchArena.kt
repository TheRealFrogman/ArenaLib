package io.github.TheRealFrogman.arenaLibrary.Arena.ConcreteArenas.DeathmatchArena

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.KillPlayerArena
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard.Scoreboard
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team
import io.github.TheRealFrogman.arenaLib.Core.Facets.ICasualArena
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class DeathmatchArena internal constructor(
    name: String,
    region: ArenaRegion,
    spawnPoints: MutableList<SpawnPoint>,
    override val maxPlayers: Int,
    override val minPlayersPerTeam: Int,
    override val maxPlayersPerTeam: Int,
    teamsInitializers: MutableList<Team.Initializer>,
    plugin: JavaPlugin,
) : KillPlayerArena(name, region, spawnPoints, teamsInitializers, plugin), ICasualArena {

    //Optional components
    private val scoreboard =
        _root_ide_package_.io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard.Scoreboard(
            this,
            { player: io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer, score: Int -> 123 })

    override val minPlayers = 2

    override fun onBukkitKill(killer: io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer, victim: io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer) {
        scoreboard.addScore(killer, 1)

        val copySpawnpoints: MutableList<SpawnPoint?> = ArrayList<SpawnPoint?>(spawnPointManager.getSpawns())
        Collections.shuffle(copySpawnpoints)

        copySpawnpoints.get(0)!!.spawnPlayer(victim)
    }

    override fun onStart() {
        TODO("заставнить игроков")
    }

    override fun declareWinners(): List<ArenaPlayer> {
        TODO("Not yet implemented")
    }
//
//    override fun checkWinCondition(probablyWinners: List<ArenaPlayer>): List<Boolean> {
//        val leader = scoreboard.leader ?: return false
//
//        return leader.bukkitPlayerUniquieId == probablyWinner.bukkitPlayerUniquieId
//    }

    override fun onWin(winners: List<ArenaPlayer>) {

        TODO("перевести игроков в спектаторы и через время" +
                "телепортировать игрока туда, где он был. " +
                "Надо запоминать локацию где-то"
        )

        TODO("написать желаемые методы ArenaPlayer а потом их релизовать")
    }

    override fun addPlayer(player: ArenaPlayer) {
        TODO("Просто добавить игрока в список")
    }

    override fun removePlayer(player: ArenaPlayer) {
        super.leave(player)
    }

    override fun onLeave(arenaPlayer: ArenaPlayer) {
        TODO("")
    }
}