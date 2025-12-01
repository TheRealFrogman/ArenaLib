package io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent

class Team(
    arena: ArenaBase,
    players: MutableList<ArenaPlayer>,
) : ArenaComponent(arena) {

    var players: MutableList<ArenaPlayer> = players
        private set


}