package io.github.TheRealFrogman.arenaLibrary.Arena.ConcreteArenas.DeathmatchArena

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.KillPlayerArena
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard.PlayerScoreboard
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team
import io.github.TheRealFrogman.arenaLib.Core.Facets.ICasualArena
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.plugin.java.JavaPlugin

class DeathmatchArena internal constructor(
    name: String,
    region: ArenaRegion,
    spawnPoints: MutableList<SpawnPoint>,
    override val maxPlayers: Int,
    override val minPlayersPerTeam: Int,
    override val maxPlayersPerTeam: Int,
    teamsInitializers: MutableList<Team.Initializer>,
    plugin: JavaPlugin,
) : KillPlayerArena(name, region, spawnPoints, teamsInitializers, 5, plugin), ICasualArena {

    //Optional components
    private val scoreboard = PlayerScoreboard(this)

    override val minPlayers = 2

    override fun onBukkitKill(metadata: KillMetadata, killingDamageEvent: EntityDamageByEntityEvent) {
        scoreboard.addScore(metadata.killer, 1)
        spawnPointManager.spawnWithLeastPlayersAround(metadata.victim)
    }

    override fun onStart() {
        TODO("заспавнить игроков")

        TODO("А ТОЧНЕЕ НАЧАТЬ РАУНДЫ" +
                "А В НАЧАЛЕ КАЖДОГО РАУНДА СПАВНИТЬ ИГРОКОВ")
    }

    override fun declareWinners(): List<ArenaPlayer> {
        TODO("Not yet implemented")
    }

    override fun onWin(winners: List<ArenaPlayer>) {

        TODO("перевести игроков в спектаторы и через время" +
                "телепортировать игрока туда, где он был до входа на арену. " +
                "Надо запоминать локацию где-то." +
                "Запоминаю в классе игрока"
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