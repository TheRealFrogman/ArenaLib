package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent

class Team(
    arena: ArenaBase,
    initializer: Initializer,
) : ArenaComponent(arena) {

    companion object {

    }

    init {
        initializer.players.forEach { it.setCurrentTeam(this) }
    }

    data class Initializer(val players: MutableList<ArenaPlayer>)

    var players: MutableList<ArenaPlayer> = initializer.players
        private set

}