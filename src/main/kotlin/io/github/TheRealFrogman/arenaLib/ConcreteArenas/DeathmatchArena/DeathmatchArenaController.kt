package io.github.TheRealFrogman.arenaLib.ConcreteArenas.DeathmatchArena

import io.github.TheRealFrogman.arenaLib.Core.Controllers.PlayerController
import io.github.TheRealFrogman.arenaLibrary.Arena.ConcreteArenas.DeathmatchArena.DeathmatchArena

class DeathmatchArenaController(arena: DeathmatchArena)
    : PlayerController by arena