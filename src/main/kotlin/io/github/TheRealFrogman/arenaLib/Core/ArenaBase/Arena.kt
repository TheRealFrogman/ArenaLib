package io.github.TheRealFrogman.arenaLib.Core.ArenaBase

import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Common.ArenaComponentRegistry
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round.RoundManager
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round.ROUND_MANAGER_KEY
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPointManager
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SPAWN_POINT_MANAGER_KEY
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

abstract class Arena(
    val name: String,
    val region: ArenaRegion,
    val spawnPoints: MutableList<SpawnPoint>,
    val componentRegistry: ArenaComponentRegistry,
    val plugin: JavaPlugin,
) {
    val uuid = UUID.randomUUID()
    val id: String get() {
        check(whenStarted != 0L) { "Arena not started" }
        check(whenFinished != 0L) { "Arena not finished" }

        return name + "_" + whenStarted + "_" + whenFinished
    }
    var whenStarted: Long = 0
        private set
    var whenFinished: Long = 0
        private set

    val players = mutableSetOf<ArenaPlayer>()
    val teams = mutableSetOf<Team>()

    init {
        val players = teams.map { it.players }
            .flatten()
            .distinct()
            .toMutableList()

        this.players.addAll(players)

        componentRegistry.register(SPAWN_POINT_MANAGER_KEY, SpawnPointManager(this, spawnPoints))
        componentRegistry.register(ROUND_MANAGER_KEY, RoundManager(this))
    }

    protected val isPlayersEnough: Boolean
        get() = this.players.size == minPlayers && this.players.size <= maxPlayers

    abstract val minPlayers: Int
    abstract val maxPlayers: Int

    fun isPlayerInArena(player: ArenaPlayer) = this.players.contains(player)

    fun isPlayerInTeam(player: ArenaPlayer) =
        this.teams.any { it.players.contains(player) }

    enum class State {
        READY,
        RUNNING,
        FINISHED
    }

    enum class ArenaEvent {
        STARTED,
        FINISHED,
    }

    enum class PlayerEvent {
        PLAYER_ADD,
        PLAYER_REMOVE
    }

    private var state = State.READY

    private val arenaListeners = mutableMapOf<ArenaEvent, MutableList<Arena.() -> Unit>>()
    private val playerListeners = mutableMapOf<PlayerEvent, MutableList<Arena.(ArenaPlayer) -> Unit>>()

    fun addListener(event: ArenaEvent, listener: Arena.() -> Unit) {
        arenaListeners.computeIfAbsent(event) { mutableListOf() }.add(listener)
    }

    fun addListener(event: PlayerEvent, listener: Arena.(ArenaPlayer) -> Unit) {
        playerListeners.computeIfAbsent(event) { mutableListOf() }.add(listener)
    }

    fun start() {
        forceInvariants()

        check(state == State.READY) { "Arena should be ready" }

        state = State.RUNNING
        whenStarted = System.currentTimeMillis()

        componentRegistry.get(ROUND_MANAGER_KEY)
            .startRoundSequence()

        arenaListeners[ArenaEvent.STARTED]?.forEach { it() }
    }

    fun finish() {
        check(state == State.RUNNING) { "Arena is not running" }

        state = State.FINISHED
        whenFinished = System.currentTimeMillis()

        players.forEach { it.setCurrentArena(null) }
        players.forEach { it.restoreAll() }

        arenaListeners[ArenaEvent.FINISHED]?.forEach { it() }
    }

    fun addPlayer(player: ArenaPlayer, team: Team) {
        this.players.add(player)
        player.setCurrentArena(this)
        playerListeners[PlayerEvent.PLAYER_ADD]?.forEach { it(player) }
    }

    fun removePlayer(player: ArenaPlayer) {
        teams
            .filter { it.players.contains(player) }
            .forEach { it.players.remove(player) }

        this.players.remove(player)

        player.restoreAll()
        player.setCurrentArena(null)

        playerListeners[PlayerEvent.PLAYER_REMOVE]?.forEach { it(player) }
    }

    fun addTeam(team: Team) {
        teams.add(team)
    }

    fun removeTeam(team: Team) {
        teams.remove(team)
    }

    private fun forceInvariants() {
        check(teams.isNotEmpty()) { "Teams should not be empty" }
        check(players.isNotEmpty()) { "Players should not be empty" }
        check(teams.all { it.players.distinct().size == it.players.size }) { "Duplicate players in teams" }

        check(minPlayers > 0) { "Min players should be > 0" }
        check(maxPlayers > 0) { "Max players should be > 0" }
        check(minPlayers <= maxPlayers) { "Min players should be <= max players" }

        check(spawnPoints.isNotEmpty()) { "SpawnPoints should not be empty" }

        check(this.players.size > maxPlayers) { "Too many players in arena" }
        check(this.players.size < minPlayers) { "Not enough players in arena" }

        check(this.isPlayersEnough) { "Not enough players to start" }
    }
}