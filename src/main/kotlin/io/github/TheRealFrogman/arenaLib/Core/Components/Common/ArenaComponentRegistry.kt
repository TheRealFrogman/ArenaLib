package io.github.TheRealFrogman.arenaLib.Core.Components.Common

class ArenaComponentRegistry(

    vararg components: Pair<ArenaComponentKey<ArenaComponent>, ArenaComponent>) {

    init {
        components.forEach { register(it.first, it.second) }
    }

    private val components = mutableMapOf<ArenaComponentKey<*>, ArenaComponent>()

    fun <T : ArenaComponent>register(key : ArenaComponentKey<T>, component: T){
        components[key] = component
    }

    fun <T : ArenaComponent>getOrNull(key: ArenaComponentKey<T>): T? {
        return components[key] as T?
    }

    fun <T : ArenaComponent>get(key: ArenaComponentKey<T>): T {
        return components[key]  as? T ?: throw IllegalStateException("Component $key not found")
    }
}