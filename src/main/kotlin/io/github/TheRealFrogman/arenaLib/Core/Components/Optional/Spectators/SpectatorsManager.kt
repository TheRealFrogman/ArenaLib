package io.github.TheRealFrogman.arenaLib.Core.Components.Optional.Spectators

import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.Arena
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Common.ArenaComponent
import org.bukkit.GameMode

class SpectatorsManager(override val arena: Arena) : ArenaComponent {

    var spectators: MutableList<ArenaPlayer> = mutableListOf()

    fun addSpectator(ap: ArenaPlayer): Boolean {
        val result = this.spectators.add(ap)
        ap.changeGamemodeWithBackup(GameMode.SPECTATOR)
        return result
    }

    fun removeSpectator(ap: ArenaPlayer): Boolean {
        val result = this.spectators.remove(ap)
        ap.restoreGamemode()
        return result
    }
}