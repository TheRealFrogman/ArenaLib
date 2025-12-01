package io.github.TheRealFrogman.arenaLib.Arena.Core.Components

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase.ArenaBase
import org.bukkit.entity.Player

class Players(arena: ArenaBase) : ArenaComponent(arena) {
    private val players: MutableSet<Player> = LinkedHashSet<Player>()
}
