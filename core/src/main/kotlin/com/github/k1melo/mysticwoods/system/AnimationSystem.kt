package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.k1melo.mysticwoods.component.AnimationComponent
import com.github.k1melo.mysticwoods.component.AnimationComponent.Companion.NO_ANIMATION
import com.github.k1melo.mysticwoods.component.ImageComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.app.gdxError
import ktx.collections.map
import ktx.log.logger

@AllOf([AnimationComponent::class, ImageComponent::class])
class AnimationSystem(
    private val textureAtlas: TextureAtlas,
    private val animationComponent: ComponentMapper<AnimationComponent>,
    private val  imageCmps: ComponentMapper<ImageComponent>
) : IteratingSystem(){

    private val cachedAnimations = mutableMapOf<String, Animation<TextureRegionDrawable>>()

    override fun onTickEntity(entity: Entity) {
        val animationComponent = animationComponent[entity]

        if (animationComponent.nextAnimation == NO_ANIMATION) {
            animationComponent.stateTime += deltaTime
        } else {
            animationComponent.animation = animation(animationComponent.nextAnimation)
            animationComponent.stateTime = 0f
            animationComponent.nextAnimation = NO_ANIMATION
        }

        animationComponent.animation.playMode = animationComponent.playMode
        imageCmps[entity].image.drawable = animationComponent.animation.getKeyFrame(animationComponent.stateTime)
    }

    private fun animation(atlasKey: String): Animation<TextureRegionDrawable> {
        return cachedAnimations.getOrPut(atlasKey) {
            log.debug { "New animation is created for '$atlasKey" }

            val regions = textureAtlas.findRegions(atlasKey)
            if (regions.isEmpty) {
                gdxError("There are no texture regions for $atlasKey")
            }
            Animation(DEFAULT_FRAME_DURATION , regions.map { TextureRegionDrawable(it) })
        }
    }

    companion object {
        private val log = logger<AnimationSystem>()
        private const val DEFAULT_FRAME_DURATION = 1/8f
    }
}
