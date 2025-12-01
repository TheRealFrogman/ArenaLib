package io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaBase

import com.google.common.collect.ImmutableList
import io.github.TheRealFrogman.arenaLib.Arena.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.Round.Round
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.Round.RoundManager
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.SpawnPoint.SpawnPointManager
import io.github.TheRealFrogman.arenaLib.Arena.Core.Components.Mandatory.Team.Team
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.KClass

abstract class ArenaBase(
    val name: String,
    val region: ArenaRegion,
    val teams: MutableList<Team>,
    val plugin: JavaPlugin
) {
    val uuid = UUID.randomUUID()
    val storeId get() = name + "_" + whenStarted + "_" + whenFinished
    var whenStarted: Long = 0
        private set
    var whenFinished: Long = 0
        private set

    abstract val maxPlayersPerTeam: Int
    abstract val minPlayersPerTeam: Int

    val players: MutableSet<ArenaPlayer> get(){

    }
    //todo спавн поинты надо в конструктор передавать наверно
//    abstract val spawnPoints: MutableList<SpawnPoint>

    //todo раунды буду создавать в менеджере раундов
//    abstract val rounds: ImmutableList<Round>

    //mandatory components
    protected var spawnPointManager = SpawnPointManager(this, spawnPoints)
    protected var roundManager = RoundManager(this, rounds)

    init {
        check(teams.isNotEmpty()) { "Teams should not be empty" }
        check(teams.all { it.players.size >= minPlayersPerTeam }) { "Not enough players in teams" }
        check(teams.all { it.players.size <= maxPlayersPerTeam }) { "Too many players in teams" }
        check(rounds.isNotEmpty()) { "Rounds should not be empty" }
        check(spawnPoints.isNotEmpty()) { "SpawnPoints should not be empty" }
    }

    enum class ArenaState {
        READY,
        RUNNING,
        FINISHED
    }

    private var state = ArenaState.READY

    init {
        check(this.players.size > maxPlayers) { "Too many players in arena" }
        check(this.players.size < minPlayers) { "Not enough players in arena" }
        this.players.forEach(Consumer { player: ArenaPlayer? -> player!!.setCurrentArena(this) })
    }

    fun start() {
        try {
            check(state == ArenaState.READY) { "Arena should be ready" }
            checkNotNull(region) { "Arena should have a region" }

            check(this.isPlayersEnough) { "Not enough players to start" }

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

    fun isPlayerInList(p: ArenaPlayer): Boolean {
        return this.players.stream()
            .anyMatch { player: ArenaPlayer -> player.bukkitPlayerUniquieId == p.bukkitPlayerUniquieId }
    }

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