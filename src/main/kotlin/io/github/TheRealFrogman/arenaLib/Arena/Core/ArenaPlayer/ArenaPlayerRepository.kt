package io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer

import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase.ArenaBase
import java.util.UUID

class ArenaPlayerRepository(val arena: ArenaBase) {

    val playerStore = mutableMapOf<UUID, ArenaPlayer>()

    fun addArenaPlayer(arenaPlayer: ArenaPlayer) {
        playerStore[arenaPlayer.bukkitPlayerUniquieId] = arenaPlayer
    }

    fun removeArenaPlayer(playerId: UUID) {
        playerStore.remove(playerId)
    }

    fun getArenaPlayer(playerId: UUID): ArenaPlayer? {
        return playerStore[playerId]
    }

    fun getAllArenaPlayers(): List<ArenaPlayer> {
        return playerStore.values.toList()
    }
}