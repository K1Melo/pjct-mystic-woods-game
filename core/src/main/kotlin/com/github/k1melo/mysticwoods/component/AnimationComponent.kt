package com.github.k1melo.mysticwoods.component

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

data class AnimationComponent(
    var model: AnimationModel = AnimationModel.UNDEFINED,
    var stateTime : Float = 0f,
    var playMode: PlayMode = PlayMode.LOOP
) {
    lateinit var animation: Animation<TextureRegionDrawable>
    var nextAnimation : String = NO_ANIMATION

    val isAnimationDone : Boolean
        get() = animation.isAnimationFinished(stateTime)

    fun nextAnimation(model: AnimationModel, animationType: AnimationType) {
        this.model = model
        nextAnimation = "${model.atlasKey}/${animationType.atlasKey}"
    }


    fun nextAnimation(animationType: AnimationType) {
        nextAnimation = "${model.atlasKey}/${animationType.atlasKey}"
    }

    companion object {
        const val NO_ANIMATION = ""
    }
}
