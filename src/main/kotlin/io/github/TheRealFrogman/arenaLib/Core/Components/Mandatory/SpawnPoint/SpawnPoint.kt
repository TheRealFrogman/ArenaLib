package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint

import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import org.bukkit.Location
import org.bukkit.World

class SpawnPoint(@JvmField val spawnLocation: Location) {
    val world: World?
        get() = spawnLocation.getWorld()
    val x: Double
        get() = spawnLocation.getX()
    val y: Double
        get() = spawnLocation.getY()
    val z: Double
        get() = spawnLocation.getZ()
    val yaw: Float
        get() = spawnLocation.getYaw()
    val pitch: Float
        get() = spawnLocation.getPitch()

    fun spawnPlayer(player: ArenaPlayer) {
        player.teleport(spawnLocation)
    }
}
