package io.github.TheRealFrogman.arenaLib.Core.Facets

import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer

interface WinnableFacet {
    enum class Event {
        WIN
    }

    val winnableListeners: MutableMap<Event, MutableList<WinnableFacet.(winners: List<ArenaPlayer>) -> Unit>>
        get() = mutableMapOf()

    fun addListener(event: Event, listener: WinnableFacet.(winners: List<ArenaPlayer>) -> Unit) {
        winnableListeners.computeIfAbsent(event) { mutableListOf() }.add(listener)
    }

    fun win(winners: List<ArenaPlayer>) {
        this.winners.addAll(winners)

        this.winnableListeners[Event.WIN]?.forEach { it(winners) }
    }

    val winners: MutableList<ArenaPlayer>
        get() = mutableListOf()

}