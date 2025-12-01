package io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Scoreboard

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team

class TeamScoreboard(
    arena: ArenaBase,
    private val onScoreChanged: OnScoreChanged
) : ArenaComponent(arena) {

    fun interface OnScoreChanged {
        fun execute(team: Team, score: Int)
    }
}