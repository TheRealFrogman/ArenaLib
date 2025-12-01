package io.github.TheRealFrogman.arenaLib

import io.github.TheRealFrogman.arenaLib.Listeners.RegisterArenaPlayerListener
import org.bukkit.plugin.java.JavaPlugin


class ArenaLib : JavaPlugin() {

    companion object {
        // These act like static members
        val context = PluginContext()
    }

    override fun onEnable() {
        RegisterArenaPlayerListener(this)
    }

    override fun onDisable() {

    }
}