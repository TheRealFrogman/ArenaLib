package io.github.TheRealFrogman.arenaLib.Core.Facets

import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import org.bukkit.block.Block

interface RestorableFacet {
    val brokenBlocks: MutableMap<ArenaPlayer, MutableList<Block>>

    fun restore() {
        TODO("Not yet implemented")
        TODO("восстановить арену асинхронно")
    }
}