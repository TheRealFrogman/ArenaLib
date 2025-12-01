package io.github.TheRealFrogman.arenaLib.Integration.Listeners

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer.ArenaPlayerRepository
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class RegisterArenaPlayerListener(plugin: JavaPlugin) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    val arenaPlayerRepository = ArenaPlayerRepository()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val arenaPlayer = ArenaPlayer(event.player)
        arenaPlayerRepository.addArenaPlayer(arenaPlayer)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerJoinEvent) {
        arenaPlayerRepository.removeArenaPlayer(event.player.uniqueId)
    }
}