package io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer

import java.util.UUID

class ArenaPlayerRepository() {

    val playerStore = mutableMapOf<UUID, ArenaPlayer>()

    fun addArenaPlayer(arenaPlayer: ArenaPlayer) {
        playerStore[arenaPlayer.uniqueId] = arenaPlayer
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