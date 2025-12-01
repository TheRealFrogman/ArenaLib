package io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Optional.Scoreboard

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.ArenaComponent
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.Team.Team

class TeamScoreboard(
    arena: ArenaBase,
    private val onScoreChanged: OnScoreChanged
) : ArenaComponent(arena) {

    fun interface OnScoreChanged {
        fun execute(team: Team, score: Int)
    }
}