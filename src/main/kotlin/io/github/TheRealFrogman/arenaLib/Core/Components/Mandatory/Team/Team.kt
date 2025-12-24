package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.Arena
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Common.ArenaComponent

class Team(
    override val arena: Arena,
    val players: MutableList<ArenaPlayer>
) : ArenaComponent {

    init {
        players.forEach { it.setCurrentTeam(this) }
    }

}