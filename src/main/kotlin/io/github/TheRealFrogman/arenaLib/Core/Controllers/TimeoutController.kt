package io.github.TheRealFrogman.arenaLib.Core.Controllers

import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer

interface TimeoutController {
    fun timeout(player: ArenaPlayer, time: Long)
}