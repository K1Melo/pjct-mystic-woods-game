package com.github.k1melo.mysticwoods.component


const val DEFAULT_SPEED = 3f

data class SpawnConfiguration (
    val model : AnimationModel,
    val speedScaling : Float = 1f,
)


