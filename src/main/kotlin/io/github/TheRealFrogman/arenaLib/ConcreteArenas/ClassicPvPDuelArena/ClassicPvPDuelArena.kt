package io.github.TheRealFrogman.arenaLib.ConcreteArenas.ClassicPvPDuelArena

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.Arena
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.KillPlayerArena
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Common.ArenaComponentRegistry
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard.SCOREBOARD_KEY
import io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Spectators.SPECTATORS_MANAGER_KEY
import io.github.TheRealFrogman.arenaLib.Core.Facets.SessionedFacet
import io.github.TheRealFrogman.arenaLib.Core.Facets.WinnableFacet
import org.bukkit.plugin.java.JavaPlugin

class ClassicPvPDuelArena (
    name: String,
    region: ArenaRegion,
    spawnPoints: MutableList<SpawnPoint>,
    timeAllottedToBeAssisterMs: Long, // milliseconds
    componentRegistry: ArenaComponentRegistry,
    plugin: JavaPlugin,
) : KillPlayerArena(name, region, spawnPoints,  timeAllottedToBeAssisterMs, componentRegistry, plugin),
    SessionedFacet,
    WinnableFacet {

    override val maxPlayers = 2
    override val minPlayers = 2

    init {
        addListener(KillPlayerArenaEvent.ON_KILL) { metadata, event ->
            componentRegistry.get(SCOREBOARD_KEY)
                .addScore(metadata.killer, 1)

            componentRegistry.get(SPECTATORS_MANAGER_KEY)
                .addSpectator(metadata.victim)

            //todo в конце раунда заспавнить на одной из точек спавна и сменить gamemode на adventure с бэкапом
            // для этого сделать приватный метод потому что я буду делать это много раз


            //todo при убийстве сменять раунд на следующий
            // а точнее когда какая-либо команда вся мертва
            // можно сделать в кил плеер арене метод который
            // возвращает булеан в зависимости от того мертвы ли все игроки на арене
            // в даунтайме всегда убитого добавлять в спектаторы
            // после даунтайма, то есть в начале следующего раунда убирать из спектаторов
            // и спавнить на одной из точек спавна своей команды
        }

        addListener(Arena.ArenaEvent.FINISHED) {
            val scoreboard = componentRegistry.get(SCOREBOARD_KEY)

            val winners =
                if(scoreboard.leader == null) listOf()
                else listOf(scoreboard.leader!!)

            win(winners)
        }
    }
}