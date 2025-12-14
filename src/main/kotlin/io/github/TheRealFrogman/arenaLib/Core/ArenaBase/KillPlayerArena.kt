package io.github.TheRealFrogman.arenaLib.Core.ArenaBase

import io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer.ArenaPlayer
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.ArenaRegion.ArenaRegion
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.SpawnPoint.SpawnPoint
import io.github.TheRealFrogman.arenaLib.Core.Components.Mandatory.Team.Team
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
typealias Damagers = MutableList<Damager>

typealias Victim = ArenaPlayer
typealias Victims = MutableList<Victim>

abstract class KillPlayerArena(
    name: String,
    region: ArenaRegion,
    spawnPoints: MutableList<SpawnPoint>,
    teamsInitializers: MutableList<Team.Initializer>,
    private val timeAllottedToBeAssisterSeconds: Long,
    plugin: JavaPlugin,
) : WinnableArena(name, region, spawnPoints, teamsInitializers, plugin) {


    data class DamagerToVictim(
        val damager: Damager,
        val victim: Victim
    ) {
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
    class DamageRegistry() {

        val metadata: MutableMap<DamagerToVictim, MutableList<DamageMetadata>> = mutableMapOf()
        val victimsByDamager: MutableMap<Damager, Victims>  = mutableMapOf()
        val damagersByVictim: MutableMap<Victim, Damagers> = mutableMapOf()

        fun recordDamage(damager: Damager, victim: Victim, metadata: DamageMetadata) {
            victimsByDamager.computeIfAbsent(damager) { mutableListOf() }.add(victim)
            damagersByVictim.computeIfAbsent(victim) { mutableListOf() }.add(damager)

            val maybeValue = this.metadata.entries.stream()
                .filter { entry -> entry.key.damager == damager && entry.key.victim == victim }
                .findFirst()
                .getOrNull()
                ?.key
            if (maybeValue != null) {
                this.metadata[maybeValue]!!.add(metadata)
            } else {
                this.metadata[DamagerToVictim(damager, victim)] = mutableListOf(metadata)
            }
        }
        fun deleteAllAboutPlayer(player: ArenaPlayer) {

            val victims = victimsByDamager.remove(player)
            val damagers = damagersByVictim.remove(player)

            victims ?: return
            damagers ?: return

            victims.forEach { victim -> damagersByVictim[victim]?.removeIf { damager -> damager == player } }
            damagers.forEach { damager -> victimsByDamager[damager]?.removeIf { victim -> victim == player } }


            val iterator = metadata.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.key.damager == player || entry.key.victim == player) {
                    iterator.remove()
                }
            }
        }

        fun deleteExpiredData() {

            TODO("ПРОЙТИСЬ ПО ВСЕЙ МЕТАДАТЕ И ОЧИСТИТЬ ПРОТУХШУЮ" +
                    "" +
                    "если список метадаты пустой" +
                    "ПОТОМ ВЗЯТЬ ВСЕХ ВИКТИМОВ ИЗ УДАЛЕННОЙ МЕТАДАТЫ И У НИХ УДАЛИТЬ ДАМАГЕРА" +
                    "ПОТОМ ВЗЯТЬ ВСЕХ ДАМАГЕРОВ И УДАЛИТЬ В НИХ ВИКТИМОВ")

            //так сравнивать протухшие данные
            //if (System.currentTimeMillis() - metadata.whenDamaged > timeAllottedToBeAssisterSeconds * 1000)
            //      iterator.remove()

        }

        fun getEarliestDamageMetadata(damager: Damager, victim: Victim): DamageMetadata? {
            return metadata.entries.stream()
                .filter { entry -> entry.key.damager == damager && entry.key.victim == victim }
                .map { entry -> entry.value.minByOrNull { it.whenDamaged } }
                .filter { it != null }
                .findFirst()
                .orElseGet { null }
        }

        fun deleteAllAboutVictim(victim: Victim) {
            val damagers = damagersByVictim.remove(victim) ?: return

            damagers.forEach { damager -> victimsByDamager[damager]?.removeIf { victim2 -> victim2 == victim } }

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

            val arenaDamager = players.stream()
                .filter { arenaPlayer -> arenaPlayer.uniqueId == damager.uniqueId }
                .findFirst()
                .getOrNull() ?: return

            val arenaVictim = players.stream()
                .filter { arenaPlayer -> arenaPlayer.uniqueId == victim.uniqueId }
                .findFirst()
                .getOrNull() ?: return

            //переменная отдельная потому что вдруг я захочу брать урон не финальный а начальный
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

            if (victim.isDead == false)
                return

            if (e.damageSource.causingEntity !is Player)
                return
            val killer = e.damageSource.causingEntity as Player

            if (killer.location.world != victim.location.world)
                throw Exception("Worlds don't match")

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

            try {

                onBukkitKill(
                    KillMetadata(
                        arenaKiller,
                        arenaVictim,
                        System.currentTimeMillis(),
                        killingTime,
                        e.isCritical,
                        weapon, //может вернуть air
                        distance,
                        assistedBy
                    ),
                    e
                )

            } catch (e: Exception) {

                catch(this::class, ::onBukkitKill)
                e.printStackTrace()

            }
        }
    }

    protected abstract fun onBukkitKill(metadata: KillMetadata, killingDamageEvent: EntityDamageByEntityEvent)

    init {
        val pluginManager = plugin.server.pluginManager

        pluginManager.registerEvents(this.killEventHook, plugin)
        pluginManager.registerEvents(this.damageEventHook, plugin)
        pluginManager.registerEvents(this.quitEventHook, plugin)
        pluginManager.registerEvents(this.healEventHook, plugin)
    }
}
