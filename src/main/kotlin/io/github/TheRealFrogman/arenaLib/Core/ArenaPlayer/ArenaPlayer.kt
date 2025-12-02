package io.github.TheRealFrogman.arenaLib.Core.ArenaPlayer

import com.google.common.collect.ImmutableMap
import io.github.TheRealFrogman.arenaLib.Core.ArenaBase.ArenaBase
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID

class ArenaPlayer(private val bukkitPlayer: Player) {

    val bukkitPlayerUniquieId: UUID = bukkitPlayer.getUniqueId()

    private var currentArena: ArenaBase? = null
    fun setCurrentArena(currentArena: ArenaBase?) {
        this.currentArena = currentArena
        TODO("Not yet implemented")
    }

    fun teleport(location: Location) {
        bukkitPlayer.teleport(location)
    }

    var teleportBackup: Location? = null
    fun teleportWithBackup(teleportLocation: Location) {
        backupLocation(bukkitPlayer.location)
        bukkitPlayer.teleport(teleportLocation)
    }

    private fun backupLocation(location: Location) {
        teleportBackup = location
        TODO("сделать бэкап в файл")
    }

    private fun deleteBackupLocation() {
        teleportBackup = null
        TODO("удалить бэкап из файла")
    }

    fun restoreLocation(): Boolean {
        if (teleportBackup == null)
            return false

        bukkitPlayer.teleport(teleportBackup!!)
        deleteBackupLocation()

        return true
    }

    var gamemodeBackup: GameMode? = null
    fun changeGamemodeWithBackup(gamemode: GameMode) {
        gamemodeBackup = bukkitPlayer.gameMode
        bukkitPlayer.gameMode = gamemode
    }

    fun restoreGamemode() {
        if (gamemodeBackup != null)
            bukkitPlayer.gameMode = gamemodeBackup!!
    }

    val inventorySnapshot: Nothing = TODO("делать снапшот инвентаря")
    fun changeInventoryWithSnapshot(itemToSlot: ImmutableMap<Int, ItemStack>) {
        TODO("Not yet implemented")
        TODO("делать снапшот инвентаря и давать другие предметы")
    }
    fun restoreInventory() {
        TODO("Not yet implemented")
    }
}