package io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.Team

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.ArenaComponent

class Team(
    arena: ArenaBase,
    players: MutableList<ArenaPlayer>,
) : ArenaComponent(arena) {

    var players: MutableList<ArenaPlayer> = players
        private set


}