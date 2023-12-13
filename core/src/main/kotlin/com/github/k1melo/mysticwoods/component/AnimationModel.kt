package com.github.k1melo.mysticwoods.component

enum class AnimationModel {
    PLAYER, SLIME, CHEST, UNDEFINED;

    val atlasKey : String = this.toString().lowercase()
}
