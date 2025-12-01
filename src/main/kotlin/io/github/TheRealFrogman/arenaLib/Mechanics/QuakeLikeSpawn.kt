package io.github.TheRealFrogman.arenaLib.Mechanics

import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

/**
 * КОД НАПИСАН НЕЙРОСЕТЬЮ
 */
class QuakeLikeSpawn(
    private val spawnPoints: MutableList<SpawnPoint>,
    private val minSpawnDistance: Double,
    private val maxSpawnAttempts: Int
) {
    private val random = Random()

    fun spawnPlayer(player: Player) {
        val spawnLocation = findValidSpawnLocation(player)

        if (spawnLocation != null) {
            player.teleport(spawnLocation)
        } else {
            // Обработка случая, когда не удалось найти подходящую точку спавна
            // Например, можно попробовать заспавнить игрока на дефолтной точке
            // или вывести сообщение об ошибке.
        }
    }

    private fun findValidSpawnLocation(player: Player): Location? {
        for (attempts in 0..<maxSpawnAttempts) {
            val randomSpawnPoint = spawnPoints.get(random.nextInt(spawnPoints.size))

            //            Location potentialSpawnLocation = getRandomLocationAround(randomSpawnPoint);
            if (isValidSpawnLocation(randomSpawnPoint.spawnLocation, player)) {
                return randomSpawnPoint.spawnLocation
            }
        }
        return null // Не удалось найти подходящую точку спавна
    }

    //
    //    private Location getRandomLocationAround(SpawnPoint spawnPoint) {
    //        double x = spawnPoint.getX() + (random.nextDouble() * 6 - 3);
    //        double y = spawnPoint.getY(); // Высота остается прежней
    //        double z = spawnPoint.getZ() + (random.nextDouble() * 6 - 3);
    //        return new Location(spawnPoint.getWorld(), x, y, z);
    //    }
    private fun isValidSpawnLocation(location: Location, player: Player): Boolean {
        // Проверяем, что спавн находится на достаточном расстоянии от других игроков:
        for (otherPlayer in player.getWorld().getPlayers()) {
            if (otherPlayer !== player && otherPlayer.getLocation().distance(location) < minSpawnDistance) {
                return false
            }
        }

        // Дополнительные проверки безопасности (например, на наличие блоков):
        // ...
        return true
    }
}