package io.github.TheRealFrogman.arenaLib.Arena.Core.Facets

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer.ArenaPlayer

interface ISessionedArena {
    fun start(players: MutableSet<ArenaPlayer>)
}
