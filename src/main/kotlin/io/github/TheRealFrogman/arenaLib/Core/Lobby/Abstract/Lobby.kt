package io.github.TheRealFrogman.arenaLib.Core.Lobby.Abstract

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.ArenaComponent
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

abstract class Lobby(
    arena: ArenaBase,
    protected val minPlayers: Int,
    protected val maxPlayers: Int,
    protected var plugin: JavaPlugin,
) : ArenaComponent(arena) {

    protected val players = mutableListOf<ArenaPlayer>()

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