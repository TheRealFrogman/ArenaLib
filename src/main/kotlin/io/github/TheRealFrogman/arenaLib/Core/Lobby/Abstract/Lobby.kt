package io.github.TheRealFrogman.arenaLib.Core.Lobby.Abstract

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.Arena
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import org.bukkit.plugin.java.JavaPlugin

abstract class Lobby (
    arena: Arena,
    protected val minPlayers: Int,
    protected val maxPlayers: Int,
    protected var plugin: JavaPlugin,
) {

    protected val players = mutableSetOf<ArenaPlayer>()

    fun join(player: ArenaPlayer) {
        players.add(player)
        onJoinHook(player)
    }

    open fun onJoinHook(player: ArenaPlayer) {}

    fun leave(player: ArenaPlayer) {
        players.remove(player)
        onLeaveHook(player)
    }

    open fun onLeaveHook(player: ArenaPlayer) {}
}