package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.Region
import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent
import org.bukkit.Location

class ArenaRegion (arena: ArenaBase, private val region: Region) : ArenaComponent(arena) {
    fun contains(location: Location): Boolean = region.contains(
        BlockVector3(
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ()
        )
    )

    val world get() = region.world
}