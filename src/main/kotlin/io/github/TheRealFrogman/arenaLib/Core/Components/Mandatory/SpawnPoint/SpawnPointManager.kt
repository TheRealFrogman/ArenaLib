package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent
import org.bukkit.Location
import kotlin.math.sqrt

class SpawnPointManager(
    arena: ArenaBase,
    private val spawns: MutableList<SpawnPoint> = ArrayList<SpawnPoint>()
) : ArenaComponent(arena) {

    init {
        require(spawns.all {
            it.spawnLocation.world == arena.region.world &&
                    arena.region.contains(it.spawnLocation)
        }) { "All spawns should be in arena region" }

        require(arena.players.size > spawns.size) { "Max players can't be more than spawn points" }
    }

    fun spawnAtRandom(player: ArenaPlayer) {
        player.teleport(spawns.random().spawnLocation)
    }

    fun spawnWithLeastPlayersAround(player: ArenaPlayer) {
        val playerLocations = arena.players.map { it.location }
        val spawnLocations = spawns.map { it.spawnLocation }

        // Находим спавн точку с максимальной суммарной дистанцией до всех игроков
        val bestSpawnLocation = spawnLocations.maxByOrNull { spawnLocation ->
            playerLocations.map { playerLocation ->
                calculateDistance(spawnLocation, playerLocation)
            }.average()
        }

        // Устанавливаем позицию игрока на лучшую точку спавна
        if (bestSpawnLocation != null) {
            player.teleport(bestSpawnLocation)
        } else {
            spawnAtRandom(player)
        }
    }

    // Вспомогательная функция для расчета расстояния между двумя точками
    private fun calculateDistance(loc1: Location, loc2: Location): Double {
        val dx = loc1.x - loc2.x
        val dy = loc1.y - loc2.y
        return sqrt(dx * dx + dy * dy)
    }
}
