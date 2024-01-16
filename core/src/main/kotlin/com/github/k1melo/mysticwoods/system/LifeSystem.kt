package com.github.k1melo.mysticwoods.system

import com.github.k1melo.mysticwoods.component.DeadComponent
import com.github.k1melo.mysticwoods.component.LifeComponent
import com.github.k1melo.mysticwoods.component.PlayerComponent
import com.github.quillraven.fleks.*

@AllOf([LifeComponent::class])
@NoneOf([DeadComponent::class])
class LifeSystem(
    private val lifeComponent: ComponentMapper<LifeComponent>,
    private val deadComponent: ComponentMapper<DeadComponent>,
    private val playerComponent: ComponentMapper<PlayerComponent>,
) : IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        val lifeComp = lifeComponent[entity]
        lifeComp.life = (lifeComp.life + (lifeComp.regeneration * deltaTime)).coerceAtMost(lifeComp.max)

        if (lifeComp.takeDamage > 0f) {
            lifeComp.life -= lifeComp.takeDamage
            lifeComp.takeDamage = 0f
        }

        if (lifeComp.isDead) {
            configureEntity(entity) {
                deadComponent.add(it) {
                    if (it in playerComponent) {
                        reviveTime = 7f
                    }
                }
            }
        }

    }
}
