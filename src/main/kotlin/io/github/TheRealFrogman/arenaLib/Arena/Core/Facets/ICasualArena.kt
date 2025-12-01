package io.github.TheRealFrogman.arenaLib.Arena.Core.Facets

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer.ArenaPlayer

//todo починить декоратор ниже
//@ConflictsWith(ISessionedArena.class)
interface ICasualArena {
    fun addPlayer(player: ArenaPlayer)
    fun removePlayer(player: ArenaPlayer)
}
