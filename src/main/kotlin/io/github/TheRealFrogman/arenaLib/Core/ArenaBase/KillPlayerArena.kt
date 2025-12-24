package io.github.TheRealFrogman.arenaLib.Core.ArenaBase

import io.github.TheRealFrogman.arenaLib.ArenaLib
import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.KillPlayerArena.KillMetadata
import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Common.ArenaComponentRegistry
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import kotlin.jvm.optionals.getOrNull

typealias Damager = ArenaPlayer
typealias Damagers = MutableSet<Damager>

typealias Victim = ArenaPlayer
typealias Victims = MutableSet<Victim>


abstract class KillPlayerArena(
    name: String,
    region: ArenaRegion,
    spawnPoints: MutableList<SpawnPoint>,
    private val timeAllottedToBeAssisterMs: Long, // milliseconds
    componentRegistry: ArenaComponentRegistry,
    plugin: JavaPlugin,
) : Arena(name, region, spawnPoints, componentRegistry, plugin) {

    data class DamagerToVictim(val damager: Damager, val victim: Victim) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is DamagerToVictim) return false

            if (damager != other.damager) return false
            if (victim != other.victim) return false

            return true
        }

        override fun hashCode(): Int {
            // Для разных типов нужно учесть, что хэш может быть разным
            // даже если значения равны по == (например, 1 и 1L)
            return damager.hashCode() xor victim.hashCode()
        }
    }

    data class KillMetadata(
        val killer: ArenaPlayer,
        val victim: ArenaPlayer,
        val whenKilled: Long,
        val killingTime: Long,
        val isCritical: Boolean,
        val weapon: ItemStack,
        val distance: Double,
        val assistedBy: List<ArenaPlayer>,
    )

    data class DamageMetadata(
        val victim: ArenaPlayer,
        val damager: Damager,
        val whenDamaged: Long,
        val damage: Double
    )

    inner class DamageRegistry() {

        val metadata: MutableMap<DamagerToVictim, MutableList<DamageMetadata>> = mutableMapOf()
        val victimsByDamager: MutableMap<Damager, Victims>  = mutableMapOf()
        val damagersByVictim: MutableMap<Victim, Damagers> = mutableMapOf()

        fun recordDamage(damager: Damager, victim: Victim, metadata: DamageMetadata) {
            victimsByDamager.computeIfAbsent(damager) { mutableSetOf() }.add(victim)
            damagersByVictim.computeIfAbsent(victim) { mutableSetOf() }.add(damager)

            val maybeKey = this.metadata.entries.stream()
                .filter { entry -> entry.key == DamagerToVictim(damager, victim) }
                .findFirst()
                .getOrNull()?.key

            if (maybeKey != null) {
                this.metadata[maybeKey]!!.add(metadata)
            } else {
                this.metadata[DamagerToVictim(damager, victim)] = mutableListOf(metadata)
            }
        }
        fun deleteAllAboutPlayer(player: ArenaPlayer) {

            val victims = victimsByDamager.remove(player)
            val damagers = damagersByVictim.remove(player)

            victims ?: return
            damagers ?: return

            victims.forEach { victim -> damagersByVictim[victim]!!.remove(player) }
            damagers.forEach { damager -> victimsByDamager[damager]!!.remove(player) }

            val iterator = metadata.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.key.damager == player || entry.key.victim == player) {
                    iterator.remove()
                }
            }
        }

        fun deleteExpiredData() {

            val mapIterator = metadata.entries.iterator()

            while (mapIterator.hasNext()) {

                val entry = mapIterator.next()
                val victim = entry.key.victim
                val damager = entry.key.damager

                val list = entry.value
                val listIterator = list.iterator()

                while (listIterator.hasNext()) {
                    val metadata = listIterator.next()
                    if (System.currentTimeMillis() - metadata.whenDamaged > timeAllottedToBeAssisterMs) {
                        listIterator.remove()
                    }

                    if (list.isEmpty()) {
                        mapIterator.remove()
                        victimsByDamager[damager]!!.remove(victim)
                        damagersByVictim[victim]!!.remove(damager)

                        if (victimsByDamager[damager]!!.isEmpty()) {
                            victimsByDamager.remove(damager)
                        }
                        if (damagersByVictim[victim]!!.isEmpty()) {
                            damagersByVictim.remove(victim)
                        }
                    }
                }
            }

        }

        fun getEarliestDamageMetadata(damager: Damager, victim: Victim): DamageMetadata? {
            return metadata.entries.stream()
                .filter { entry -> entry.key == DamagerToVictim(damager, victim) }
                .map { entry -> entry.value.minByOrNull { it.whenDamaged } }
                .filter { it != null }
                .findFirst()
                .orElseGet { null }
        }

        fun deleteAllAboutVictim(victim: Victim) {
            val damagers = damagersByVictim.remove(victim) ?: return

            damagers.forEach { damager ->
               val victims = victimsByDamager[damager]
                victims?.remove(victim)

                if (victims != null && victims.isEmpty()) {
                    victimsByDamager.remove(damager)
                }
            }

            metadata.entries.removeIf { entry -> entry.key.victim == victim }
        }
    }

    val damageRegistry = DamageRegistry()

    private val damageEventHook: Listener = object : Listener {
        @EventHandler
        fun onDamageEvent(e: EntityDamageByEntityEvent) {

            val victim = e.entity

            if (victim !is Player)
                return

            if (e.damageSource.causingEntity !is Player)
                return
            val damager = e.damageSource.causingEntity as Player;

            if (victim == damager)
                return

            damageRegistry.deleteExpiredData()

            val arenaDamager = ArenaLib.context.arenaPlayerRepository
                .getArenaPlayer(damager.uniqueId) ?: return

            val arenaVictim = ArenaLib.context.arenaPlayerRepository
                .getArenaPlayer(victim.uniqueId) ?: return

            val valid = isPlayerInArena(arenaDamager) && isPlayerInArena(arenaVictim)

            if (!valid)
                return

            //переменная отдельная потому что вдруг я захочу брать урон не финальный, а начальный
            val damage = e.finalDamage

            val damageMetadata = DamageMetadata(
                arenaVictim,
                arenaDamager,
                System.currentTimeMillis(),
                damage
            )

            damageRegistry.recordDamage(arenaDamager, arenaVictim, damageMetadata)
       }
    }

    private val healEventHook: Listener = object : Listener {
        @EventHandler
        fun onHealEvent(e: EntityRegainHealthEvent) {
            if (e.entity !is Player)
                return
            val player = e.entity as Player

            //ищем в арене человека, который пополнил здоровье
            val arenaPlayerWhoRegainedHealth = players.stream()
                .filter { arenaPlayer -> arenaPlayer.uniqueId == player.uniqueId }
                .findFirst()
                .getOrNull() ?: return

            damageRegistry.deleteAllAboutVictim(arenaPlayerWhoRegainedHealth)
        }
    }
    private val quitEventHook: Listener = object : Listener {
        @EventHandler
        fun onQuit(e: PlayerQuitEvent) {
            val bukkitPlayer = e.player

            val arenaPlayer = players.stream()
                .filter { arenaPlayer -> arenaPlayer.uniqueId == bukkitPlayer.uniqueId }
                .findFirst()
                .orElse(null) ?: return

            damageRegistry.deleteAllAboutPlayer(arenaPlayer)
        }
    }

    private val killEventHook: Listener = object : Listener {

        @EventHandler
        fun onKillEvent(e: EntityDamageByEntityEvent) {

            val victim = e.entity

            if (victim !is Player)
                return

            if (!victim.isDead)
                return

            if (e.damageSource.causingEntity !is Player)
                return
            val killer = e.damageSource.causingEntity as Player

            if (killer.location.world != victim.location.world)
                return

            val arenaKiller = players.stream()
                .filter { arenaPlayer -> arenaPlayer.uniqueId == killer.uniqueId }
                .findFirst()
                .getOrNull() ?: return

            val arenaVictim = players.stream()
                .filter { arenaPlayer -> arenaPlayer.uniqueId == victim.uniqueId }
                .findFirst()
                .getOrNull() ?: return

            // берем самый ранний урон и высчитываем время от него до убийства
            val killingTime = System.currentTimeMillis() - (damageRegistry.getEarliestDamageMetadata(arenaKiller,arenaVictim)?.whenDamaged ?: 0L)
            val weapon = killer.inventory.getItem(EquipmentSlot.HAND)
            val distance = killer.location.distance(victim.location)
            val assistedBy = damageRegistry.damagersByVictim[arenaVictim]?.toList() ?: emptyList()

            //удаляем данные о жертве потому что она была убита
            damageRegistry.deleteAllAboutVictim(arenaVictim)

            val killMetadata = KillMetadata(
                arenaKiller,
                arenaVictim,
                System.currentTimeMillis(),
                killingTime,
                e.isCritical,
                weapon, //может вернуть air
                distance,
                assistedBy
            )

            bukkitKillListeners[KillPlayerArenaEvent.ON_KILL]
                ?.forEach { it(killMetadata, e) }
        }
    }

    enum class KillPlayerArenaEvent {
        ON_KILL
    }

    private val bukkitKillListeners = mutableMapOf<KillPlayerArenaEvent, MutableList<KillPlayerArena.(metadata: KillMetadata, killingDamageEvent: EntityDamageByEntityEvent) -> Unit>>()

    fun addListener(event: KillPlayerArenaEvent, listener: KillPlayerArena.(metadata: KillMetadata, killingDamageEvent: EntityDamageByEntityEvent) -> Unit) {
        bukkitKillListeners.computeIfAbsent(event) { mutableListOf() }.add(listener)
    }

    init {
        val pluginManager = plugin.server.pluginManager

        pluginManager.registerEvents(this.killEventHook, plugin)
        pluginManager.registerEvents(this.damageEventHook, plugin)
        pluginManager.registerEvents(this.quitEventHook, plugin)
        pluginManager.registerEvents(this.healEventHook, plugin)
    }
}
