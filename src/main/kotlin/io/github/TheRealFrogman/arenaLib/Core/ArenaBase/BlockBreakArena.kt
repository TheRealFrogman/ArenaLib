package io.github.TheRealFrogman.arenaLib.Core.ArenaBase

import io.github.TheRealFrogman.arenaLib.ArenaLib
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

abstract class BlockBreakArena(
    name: String,
    region: ArenaRegion,
    spawnPoints: MutableList<SpawnPoint>,
    teamsInitializers: MutableList<Team.Initializer>,
    plugin: JavaPlugin,
) : ArenaBase(name, region, spawnPoints, teamsInitializers, plugin) {

    val arenaPlayerRepository = ArenaLib.context.arenaPlayerRepository

    val eventHook = object : Listener {

        @EventHandler
        private fun onBlockBreak(event: BlockBreakEvent) {
            val maybeArenaPlayer = arenaPlayerRepository.getArenaPlayer(event.player.uniqueId)
            if (maybeArenaPlayer == null)
                return

            val arenaPlayer = maybeArenaPlayer as ArenaPlayer

            val isRegionContainsBlock = region.contains(event.block.location)

            val valid = isPlayerInArena(arenaPlayer) && isRegionContainsBlock && event.block.type in blocksAllowedToBreak

            if (valid) {
                brokenBlocks.computeIfAbsent(arenaPlayer) { mutableListOf() }.add(event.block)
                onBukkitBlockBreak(arenaPlayer, event.block)
            }
        }
    }

    init {
        check(blocksAllowedToBreak.isNotEmpty()) { "Blocks allowed to break should not be empty" }

        plugin.getServer()
            .getPluginManager()
            .registerEvents(this.eventHook, plugin)
    }

    protected abstract val blocksAllowedToBreak: List<Material>
    val brokenBlocks: MutableMap<ArenaPlayer, MutableList<Block>> = HashMap()

    // возвращаем блоки
    private fun restoreArena() {
        TODO("Not yet implemented")
        TODO("восстановить арену асинхронно")
    }

    override fun onFinish() {
        TODO("Not yet implemented")
        restoreArena()
    }
    protected abstract fun onBukkitBlockBreak(arenaPlayer: ArenaPlayer, block: Block)
}