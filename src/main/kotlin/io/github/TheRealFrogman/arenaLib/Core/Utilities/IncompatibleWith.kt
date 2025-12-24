package io.github.TheRealFrogman.arenaLib.Core.Utilities

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class IncompatibleWith(val interfaceClass: KClass<*>)