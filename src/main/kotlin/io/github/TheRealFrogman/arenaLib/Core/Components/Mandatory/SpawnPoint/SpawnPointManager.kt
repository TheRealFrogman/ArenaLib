package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent

class SpawnPointManager(
    arena: ArenaBase,
    private val spawns: MutableList<SpawnPoint> = ArrayList<SpawnPoint>()
) : ArenaComponent(arena) {

    init {
        require(spawns.all {
            it.spawnLocation.world == arena.region.world
            arena.region.contains(it.spawnLocation)
        }) { "All spawns should be in arena region" }

        require(arena.players.size > spawns.size) { "Max players can't be more than spawn points" }
    }

    fun getSpawns(): MutableList<SpawnPoint> {
        return ArrayList(spawns) // Создает новую копию списка
    }
}
