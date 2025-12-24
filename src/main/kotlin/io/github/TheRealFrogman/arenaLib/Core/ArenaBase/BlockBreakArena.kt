package io.github.TheRealFrogman.arenaLib.Core.ArenaBase

import io.github.TheRealFrogman.arenaLib.ArenaLib
import io.github.TheRealFrogman.arenaLib.Core.Facets.RestorableFacet
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Common.ArenaComponentRegistry
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round.ROUND_MANAGER_KEY
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round.Round
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round.RoundManager
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
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
    componentRegistry: ArenaComponentRegistry,
    plugin: JavaPlugin,
) : Arena(name, region, spawnPoints, componentRegistry, plugin), RestorableFacet {

    val eventHook = object : Listener {

        @EventHandler
        private fun onBlockBreak(event: BlockBreakEvent) {
            val arenaPlayer = ArenaLib.context.arenaPlayerRepository.getArenaPlayer(event.player.uniqueId) ?: return

            val isfRegionContainsBlock = region.contains(event.block.location)

            val valid = isPlayerInArena(arenaPlayer) && isfRegionContainsBlock && event.block.type in blocksAllowedToBreak

            if (valid) {
                brokenBlocks.computeIfAbsent(arenaPlayer) { mutableListOf() }.add(event.block)
                blockBreakArenaListeners[BlockBreakArenaEvent.BLOCK_BREAK]?.forEach { it(arenaPlayer, event.block) }
            }
        }
    }

    init {
        check(blocksAllowedToBreak.isNotEmpty()) { "Blocks allowed to break should not be empty" }

        plugin.server
            .pluginManager
            .registerEvents(this.eventHook, plugin)

        val roundManager = componentRegistry.get(ROUND_MANAGER_KEY)

        roundManager.addListener(RoundManager.Event.INIT) {
            lastRound!!.addListener(Round.Event.FINISH) { restore() }
        }
    }

    protected abstract val blocksAllowedToBreak: List<Material>
    override val brokenBlocks: MutableMap<ArenaPlayer, MutableList<Block>> = HashMap()

    enum class BlockBreakArenaEvent {
        BLOCK_BREAK
    }

    private val blockBreakArenaListeners = mutableMapOf<BlockBreakArenaEvent, MutableList<Arena.(arenaPlayer: ArenaPlayer, block: Block) -> Unit>>()

    fun addListener(event: BlockBreakArenaEvent, listener: Arena.(arenaPlayer: ArenaPlayer, block: Block) -> Unit) {
        blockBreakArenaListeners.computeIfAbsent(event) { mutableListOf() }.add(listener)
    }

    fun removeListener(event: BlockBreakArenaEvent, listener: Arena.(arenaPlayer: ArenaPlayer, block: Block) -> Unit) {
        blockBreakArenaListeners[event]?.remove(listener)
    }

}