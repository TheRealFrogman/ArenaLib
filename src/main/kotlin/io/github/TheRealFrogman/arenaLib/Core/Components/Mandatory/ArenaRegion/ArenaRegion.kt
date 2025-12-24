package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion

import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.Region
import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.Arena
import io.github.TheRealFrogman.arenaLib.Core.Components.Common.ArenaComponent
import org.bukkit.Location

class ArenaRegion (override val arena: Arena, private val region: Region) : ArenaComponent {
    fun contains(location: Location): Boolean = region.contains(
        BlockVector3(
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ()
        )
    )

    val world get() = region.world
}