package io.github.TheRealFrogman.arenaLib.Core.Facets

import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer

interface ISessionedArena {
    fun start(players: MutableSet<ArenaPlayer>)
}
