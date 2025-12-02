package io.github.TheRealFrogman.arenaLib.Core.ArenaBase

import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Round.RoundManager
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPointManager
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.KClass

abstract class ArenaBase(
    val name: String,
    val region: ArenaRegion,
    val spawnPoints: MutableList<SpawnPoint>,
    val teamsInitializers: MutableList<Team.Initializer>,
    val plugin: JavaPlugin
) {
    val uuid = UUID.randomUUID()
    val id: String get() {
        if (whenStarted == 0L)
            throw IllegalStateException("Arena not started")
        if (whenFinished == 0L)
            throw IllegalStateException("Arena not finished")

        return name + "_" + whenStarted + "_" + whenFinished
    }
    var whenStarted: Long = 0
        private set
    var whenFinished: Long = 0
        private set

    abstract val maxPlayersPerTeam: Int
    abstract val minPlayersPerTeam: Int

    val players = teamsInitializers.flatMap { it.players }.distinct().toMutableSet()
    val teams = teamsInitializers.map { Team(this, it) }

    //mandatory components
    protected var spawnPointManager = SpawnPointManager(this, spawnPoints)
    protected var roundManager = RoundManager(this)

    init {
        check(teams.isNotEmpty()) { "Teams should not be empty" }
        check(teams.all { it.players.size >= minPlayersPerTeam }) { "Not enough players in teams" }
        check(teams.all { it.players.size <= maxPlayersPerTeam }) { "Too many players in teams" }

        check(spawnPoints.isNotEmpty()) { "SpawnPoints should not be empty" }

        check(this.players.size > maxPlayers) { "Too many players in arena" }
        check(this.players.size < minPlayers) { "Not enough players in arena" }

        check(this.isPlayersEnough) { "Not enough players to start" }

        this.players.forEach(Consumer { player: ArenaPlayer -> player.setCurrentArena(this) })
    }

    enum class ArenaState {
        READY,
        RUNNING,
        FINISHED
    }

    private var state = ArenaState.READY

    fun start() {
        try {
            check(state == ArenaState.READY) { "Arena should be ready" }

            state = ArenaState.RUNNING
            whenStarted = System.currentTimeMillis()

            onStart()
        } catch (e: Exception) {
            catch(this::class, ::start)

            e.printStackTrace()
        }
    }

    fun finish() {
        try {
            check(state == ArenaState.RUNNING) { "Arena is not running" }

            state = ArenaState.FINISHED
            whenFinished = System.currentTimeMillis()

            this.players.forEach(Consumer { player: ArenaPlayer -> player.setCurrentArena(null) })

            onFinish()
        } catch (e: Exception) {
            catch(this::class, ::finish)

            e.printStackTrace()
        }
    }

    protected abstract fun onStart()
    protected abstract fun onFinish()

    protected val isPlayersEnough: Boolean
        get() = this.players.size == minPlayers && this.players.size <= maxPlayers

    abstract val minPlayers: Int
    abstract val maxPlayers: Int

    fun isPlayerInArena(p: ArenaPlayer) = this.players.stream()
            .anyMatch { it.bukkitPlayerUniquieId == p.bukkitPlayerUniquieId }

    open fun leave(arenaPlayer: ArenaPlayer) {
        this.players.remove(arenaPlayer)
        onLeave(arenaPlayer)
    }

    //сюда написать например наказание за лив или выведение результата матча на момент лива
    abstract fun onLeave(arenaPlayer: ArenaPlayer)

    open fun catch(arenaClass: KClass<*>, method: Function<*>) {
        Bukkit.getLogger().severe("Class ${arenaClass.simpleName} errored in method " + method.toString())
    }
}