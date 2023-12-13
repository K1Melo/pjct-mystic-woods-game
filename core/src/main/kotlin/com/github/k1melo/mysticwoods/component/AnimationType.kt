package com.github.k1melo.mysticwoods.component

enum class AnimationType {
    IDLE, RUN, ATTACK, DEATH, OPEN;

    val atlasKey : String = this.toString().lowercase()
}
