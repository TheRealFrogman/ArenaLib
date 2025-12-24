package io.github.TheRealFrogman.arenaLib.Core.Utilities

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class PlayerImmobilize(val plugin: JavaPlugin) {

    private val immobilizedPlayers = mutableSetOf<Player>()

    fun isImmobilized(player: Player) = immobilizedPlayers.contains(player)

    fun immobilizeFor(player: Player, timeMs: Long) {
        immobilizedPlayers.add(player)
        val task = object : BukkitRunnable() {
            override fun run() {
                immobilizedPlayers.remove(player)
            }
        }.runTaskLater(plugin, timeMs)
    }

    fun immobilize(player: Player) {
        immobilizedPlayers.add(player)
    }

    fun unimmobilize(player: Player) {
        immobilizedPlayers.remove(player)
    }

    private val moveEventHook = object : Listener {
        @EventHandler
        fun onPlayerMove(event: org.bukkit.event.player.PlayerMoveEvent) {
            if (immobilizedPlayers.contains(event.player))
                if(event.hasChangedPosition())
                    event.isCancelled = true

        }
    }

    init {
        plugin.server.pluginManager.registerEvents(moveEventHook, plugin)
    }
}