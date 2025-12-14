package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint

import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import org.bukkit.Location
import org.bukkit.World

class SpawnPoint(val spawnLocation: Location) {
    val world: World
        get() = spawnLocation.world
    val x: Double
        get() = spawnLocation.x
    val y: Double
        get() = spawnLocation.y
    val z: Double
        get() = spawnLocation.z
    val yaw: Float
        get() = spawnLocation.yaw
    val pitch: Float
        get() = spawnLocation.pitch

    fun spawnPlayer(player: ArenaPlayer) {
        player.teleport(spawnLocation)
    }
}
