package com.github.k1melo.mysticwoods.component

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

data class SpawnComponent (
    var type : String = "",
    var location : Vector2 = vec2()
)
