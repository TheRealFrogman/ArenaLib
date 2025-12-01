package io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase

import com.google.common.collect.ImmutableList
import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer.ArenaPlayerRepository
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.Round.Round
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.Team.Team
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin

abstract class KillPlayerArena(
    name: String,
    region: ArenaRegion,
    teams: MutableList<Team>,
    plugin: JavaPlugin,
) : WinnableArena(name, region, teams, plugin) {

    private val eventHook: Listener = object : Listener {

        @EventHandler
        fun onKillEvent(e: PlayerDeathEvent) {
            val victim = e.getEntity()

            val ds = e.damageSource
            if (ds.causingEntity !is Player) return
            val killer = ds as Player;

            val arenaKiller = players.stream()
                .filter { arenaPlayer -> arenaPlayer.bukkitPlayerUniquieId == killer.uniqueId }
                .findFirst()
                .orElse(null)

            val arenaVictim = players.stream()
                .filter { arenaPlayer -> arenaPlayer.bukkitPlayerUniquieId == victim.uniqueId }
                .findFirst()
                .orElse(null)

            val valid = arenaKiller != null && arenaVictim != null

            if (valid) {
                try {
                    onBukkitKill(arenaKiller, arenaVictim)
                } catch (e: Exception) {
                    catch(this::class, ::onBukkitKill)
                    e.printStackTrace()
                }
            }

        }
    }

    protected abstract fun onBukkitKill(killer: ArenaPlayer, victim: ArenaPlayer)

    private val arenaPlayerRepository = ArenaPlayerRepository(this)

    init {
        plugin.getServer()
            .getPluginManager()
            .registerEvents(this.eventHook, plugin)
    }
}