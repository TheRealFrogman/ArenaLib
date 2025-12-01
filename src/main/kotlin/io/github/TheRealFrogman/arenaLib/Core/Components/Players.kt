package io.github.TheRealFrogman.arenaLib.Core.Components

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import org.bukkit.entity.Player

class Players(arena: ArenaBase) : ArenaComponent(arena) {
    private val players: MutableSet<Player> = LinkedHashSet<Player>()
}
