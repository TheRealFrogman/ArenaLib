package io.github.TheRealFrogman.arenaLib

import io.github.TheRealFrogman.arenaLib.Integration.Listeners.RegisterArenaPlayerListener
import org.bukkit.plugin.java.JavaPlugin


class ArenaLib : JavaPlugin() {

    override fun onEnable() {
        RegisterArenaPlayerListener(this)
    }

    override fun onDisable() {

    }
}