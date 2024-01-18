package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.graphics.g2d.Animation
import com.github.k1melo.mysticwoods.component.AnimationComponent
import com.github.k1melo.mysticwoods.component.AnimationType
import com.github.k1melo.mysticwoods.component.LootComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([LootComponent::class])
class LootSystem(
    private val lootComponents: ComponentMapper<LootComponent>,
    private val animationComponents: ComponentMapper<AnimationComponent>,
) : IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        with(lootComponents[entity]) {
            if (interactEntity == null) {
                return
            }
        }

        configureEntity(entity) {
            lootComponents.remove(it)
        }

        animationComponents.getOrNull(entity)?.let { animationComponent ->
            animationComponent.nextAnimation(AnimationType.OPEN)
            animationComponent.playMode = Animation.PlayMode.NORMAL
        }
    }
}
