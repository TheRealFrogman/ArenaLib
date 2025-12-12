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

    val metadataByDamager: MutableMap<Damager, MutableList<DamageMetadata>> = mutableMapOf()
    val metadataByVictim: MutableMap<Victim, MutableList<DamageMetadata>> = mutableMapOf()

    val victimsByDamager = mutableMapOf<Damager, Victims>()
    val damagersByVictim: MutableMap<Victim, Damagers> = mutableMapOf()

    private fun recordDamage(damager: Damager, victim: Victim, metadata: DamageMetadata) {
        victimsByDamager.computeIfAbsent(damager) { mutableListOf() }.add(victim)
        damagersByVictim.computeIfAbsent(victim) { mutableListOf() }.add(damager)

        metadataByDamager.computeIfAbsent(damager) { mutableListOf() }.add(metadata)
        metadataByVictim.computeIfAbsent(victim) { mutableListOf() }.add(metadata)
    }

    private fun deleteAllAboutPlayer(player: ArenaPlayer) {
        metadataByDamager.remove(player)
        metadataByVictim.remove(player)
        victimsByDamager.remove(player)
        damagersByVictim.remove(player)
    }

    private fun deleteExpiredData() {

        val metadataByVictimIterator = metadataByVictim.entries.iterator()

        while (metadataByVictimIterator.hasNext()) {
            val entry = metadataByVictimIterator.next()
            val metadataList = entry.value

            val iterator = metadataList.iterator()
            while (iterator.hasNext()) {

                val metadata = iterator.next()
                if (System.currentTimeMillis() - metadata.whenDamaged > timeAllottedToBeAssisterSeconds * 1000)
                    iterator.remove()

            }

            if (metadataList.isEmpty())
                metadataByVictimIterator.remove()

        }

        TODO("ПО АНАЛОГИИ С ВЕРХНИМ КОДОМ")
        val metadataByDamagerIterator = metadataByDamager.entries.iterator()

        TODO("ТАКЖЕ ТУТ УДАЛИТЬ КТО ДАННЫЕ О ЖЕРТВАХ В ДРУГИХ МАПАХ")
    }

    private fun getEarliestDamageMetadataByVictim(victim: Victim): DamageMetadata? {
        return metadataByVictim[victim]?.minByOrNull { it.whenDamaged }
    }

    private fun getEarliestDamageMetadataByDamager(damager: Victim): DamageMetadata? {
        return metadataByDamager[damager]?.minByOrNull { it.whenDamaged }
    }

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

            deleteExpiredData()

            val arenaDamager = players.stream()
                .filter { arenaPlayer -> arenaPlayer.uniqueId == damager.uniqueId }
                .findFirst()
                .getOrNull() ?: return

            val arenaVictim = players.stream()
                .filter { arenaPlayer -> arenaPlayer.uniqueId == victim.uniqueId }
                .findFirst()
                .getOrNull() ?: return

            // если жертва этого дамагера та же самая, удаляем прошлые данные о ней
//            TODO("НО ЭТОГО ДЕЛАТЬ НЕ СТОИТ ЧТОБЫ ПОСЧИТАТЬ ВРЕМЯ НА УБИЙСТВО")
//            deleteVictimByDamager(arenaVictim, arenaDamager)

            //переменная отдельная потому что вдруг я захочу брать урон не финальный а начальный
            val damage = e.finalDamage

            val damageMetadata = DamageMetadata(
                arenaVictim,
                arenaDamager,
                System.currentTimeMillis(),
                damage
            )

            recordDamage(arenaDamager, arenaVictim, damageMetadata)
       }
    }

    private fun deleteVictimByDamager(victim: Victim, damager: Damager) {
        val victimsList = victimsByDamager[damager] ?: return

        TODO("ИСПОЛЬЗОВАТЬ ПРИЕМУЩЕСТВА ОБРАТНОГО ИНДЕКСИНГА")


        val victimsListIterator = victimsList.iterator()
        while (victimsListIterator.hasNext()) {

            val damageMetadata = victimsListIterator.next()
            if (damageMetadata.victim.uniqueId == victim.uniqueId)
                victimsListIterator.remove()

        }

        if (victimsList.isEmpty())
            victimsByDamager.remove(damager)

        val damagersList = damagersByVictim.getOrElse(victim) { null } ?: return

        val damagersListIterator = damagersList.iterator()
        while (damagersListIterator.hasNext()) {

            val internalDamager = damagersListIterator.next()

            if (internalDamager.uniqueId == damager.uniqueId)
                damagersListIterator.remove()

        }
    }

    private fun deleteDamagerByVictim(damager: Damager, victim: Victim) {

        TODO("ИСПОЛЬЗОВАТЬ ПРИЕМУЩЕСТВА ОБРАТНОГО ИНДЕКСИНГА")
    }

    private fun deleteAllAboutVictim(victim: Victim) {

        metadataByVictim.remove(victim)

        val damagers = damagersByVictim.remove(victim) ?: return

        for (damager in damagers) {
            victimsByDamager[damager]?.remove(victim)
            metadataByDamager[damager]?.removeAll { it.victim.uniqueId == victim.uniqueId }
        }
    }

    private fun deleteAllAboutDamager(damager: Damager) {
        TODO("Not yet implemented")
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

            deleteAllAboutVictim(arenaPlayerWhoRegainedHealth)
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

            deleteAllAboutPlayer(arenaPlayer)
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
            val killingTime = System.currentTimeMillis() - (getEarliestDamageMetadataByVictim(arenaVictim)?.whenDamaged ?: 0L)
            val weapon = killer.inventory.getItem(EquipmentSlot.HAND)
            val distance = killer.location.distance(victim.location)
            val assistedBy = damagersByVictim[arenaVictim]?.toList() ?: emptyList()

            //удаляем данные о жертве потому что она была убита
            deleteAllAboutVictim(arenaVictim)

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
                    )
                )

            } catch (e: Exception) {

                catch(this::class, ::onBukkitKill)
                e.printStackTrace()

            }
        }
    }

    protected abstract fun onBukkitKill(metadata: KillMetadata)

    init {
        val pluginManager = plugin.server.pluginManager

        pluginManager.registerEvents(this.killEventHook, plugin)
        pluginManager.registerEvents(this.damageEventHook, plugin)
        pluginManager.registerEvents(this.quitEventHook, plugin)
        pluginManager.registerEvents(this.healEventHook, plugin)
    }
}
