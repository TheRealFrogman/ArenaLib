package io.github.TheRealFrogman.arenaLib.Core.Facets

import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer

interface CasualFacet {

    enum class Event {
        PLAYER_JOIN,
        PLAYER_LEAVE
    }

    val casualListeners: MutableMap<Event, MutableList<CasualFacet.(ArenaPlayer) -> Unit>>
        get() = mutableMapOf()

    fun addListener(event: Event, listener: CasualFacet.(ArenaPlayer) -> Unit) {
        casualListeners.computeIfAbsent(event) { mutableListOf() }.add(listener)
    }

    fun join(player: ArenaPlayer) {
        this.casualListeners[Event.PLAYER_JOIN]?.forEach { it(player) }
    }
    fun leave(player: ArenaPlayer) {
        this.casualListeners[Event.PLAYER_LEAVE]?.forEach { it(player) }
    }
}
