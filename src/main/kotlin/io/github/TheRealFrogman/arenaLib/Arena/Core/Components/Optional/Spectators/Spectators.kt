package io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Optional.Spectators

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase.ArenaBase
import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.ArenaComponent

class Spectators(
    arena: ArenaBase,
    var onSpectatorAdd: OnSpectatorAdd,
    var onSpectatorRemove: OnSpectatorRemove
) : ArenaComponent(arena) {

    fun interface OnSpectatorAdd {
        fun execute(player: ArenaPlayer)
    }

    fun interface OnSpectatorRemove {
        fun execute(player: ArenaPlayer)
    }

    var spectators: MutableSet<ArenaPlayer> = HashSet()

    fun addSpectator(ap: ArenaPlayer): Boolean {
        val result = this.spectators.add(ap)
        onSpectatorAdd.execute(ap)
        return result
    }

    fun removeSpectator(ap: ArenaPlayer): Boolean {
        val result = this.spectators.remove(ap)
        onSpectatorRemove.execute(ap)
        return result
    }
}