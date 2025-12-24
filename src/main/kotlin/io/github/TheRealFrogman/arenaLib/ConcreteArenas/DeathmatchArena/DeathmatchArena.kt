package io.github.TheRealFrogman.arenaLibrary.Arena.ConcreteArenas.DeathmatchArena

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.Arena
import io.github.TheRealFrogman.arenaLib.Core.Facets.WinnableFacet
import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.KillPlayerArena
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Common.ArenaComponentRegistry
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round.ROUND_MANAGER_KEY
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round.Round
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round.RoundManager
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SPAWN_POINT_MANAGER_KEY
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard.SCOREBOARD_KEY
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard.Scoreboard
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Spectators.SPECTATORS_MANAGER_KEY
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Spectators.SpectatorsManager
import io.github.TheRealFrogman.arenaLib.Core.Controllers.PlayerController
import io.github.TheRealFrogman.arenaLib.Core.Facets.CasualFacet
import org.bukkit.plugin.java.JavaPlugin

class DeathmatchArena (
    name: String,
    region: ArenaRegion,
    spawnPoints: MutableList<SpawnPoint>,
    override val maxPlayers: Int,
    timeAllottedToBeAssisterMs: Long, // milliseconds
    componentRegistry: ArenaComponentRegistry,
    plugin: JavaPlugin,
) : KillPlayerArena(name, region, spawnPoints, timeAllottedToBeAssisterMs, componentRegistry, plugin),
    CasualFacet,
    PlayerController,
    WinnableFacet {

    override val minPlayers = 2

    init {
        componentRegistry.register(SCOREBOARD_KEY, Scoreboard<ArenaPlayer>(this))
        componentRegistry.register(SPECTATORS_MANAGER_KEY, SpectatorsManager(this))
        componentRegistry.register(ROUND_MANAGER_KEY, RoundManager(this))

        addListener(KillPlayerArenaEvent.ON_KILL) { metadata, event ->

            componentRegistry.get(SCOREBOARD_KEY)
                .addScore(metadata.killer, 1)

            componentRegistry.get(SPAWN_POINT_MANAGER_KEY)
                .spawnWithLeastPlayersAround(metadata.victim)

        }


        componentRegistry.get(ROUND_MANAGER_KEY)
            .getRounds()
            .forEach { round ->
                round?.addListener(Round.Event.FINISH) {
                    players.forEach {
                        val spawnPointManager = componentRegistry.get(SPAWN_POINT_MANAGER_KEY)
                        spawnPointManager.spawnAtRandom(it)
                    }
                }
            }

        addListener(ArenaEvent.FINISHED) {
            val scoreboard = componentRegistry.get(SCOREBOARD_KEY)

            val potentialWinners = scoreboard.getFirstLeading(3)

            val winners = potentialWinners.ifEmpty { listOf() }

            win(winners)
        }

        val roundsManager = componentRegistry.get(ROUND_MANAGER_KEY)

        roundsManager.lastRound?.addListener(Round.Event.FINISH) {
            val spectatorsManager = componentRegistry.get(SPECTATORS_MANAGER_KEY)

            players.forEach { spectatorsManager.addSpectator(it) }
        }
    }

    override fun join(player: ArenaPlayer) {
        TODO("Not yet implemented")
        super<CasualFacet>.join(player)
    }

    override fun leave(player: ArenaPlayer) {
        TODO("Not yet implemented")
        TODO("здесь можно отобразить игроку результат арены на момент выхода")

        super<CasualFacet>.leave(player)
    }
}