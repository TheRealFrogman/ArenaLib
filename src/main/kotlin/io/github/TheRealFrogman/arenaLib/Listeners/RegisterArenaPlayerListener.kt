package io.github.TheRealFrogman.arenaLib.Listeners

import io.github.TheRealFrogman.arenaLib.ArenaLib
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class RegisterArenaPlayerListener(plugin: JavaPlugin) : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }


    val arenaPlayerRepository = ArenaLib.context.arenaPlayerRepository

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