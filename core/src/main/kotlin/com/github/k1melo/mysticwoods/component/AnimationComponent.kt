package com.github.k1melo.mysticwoods.component

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

data class AnimationComponent(
    var atlasKey : String = "",
    var stateTime : Float = 0f,
    var playMode: PlayMode = PlayMode.LOOP
) {
    lateinit var animation: Animation<TextureRegionDrawable>
    var nextAnimation : String = NO_ANIMATION
    fun nextAnimation(atlasKey: String, animationType: AnimationType) {
        this.atlasKey = atlasKey
        nextAnimation = "$atlasKey/${animationType.atlasKey}"
    }

    companion object {
        const val NO_ANIMATION = ""
    }
}
