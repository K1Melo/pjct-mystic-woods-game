package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.k1melo.mysticwoods.ai.DefaultState
import com.github.k1melo.mysticwoods.component.AnimationComponent
import com.github.k1melo.mysticwoods.component.DeadComponent
import com.github.k1melo.mysticwoods.component.LifeComponent
import com.github.k1melo.mysticwoods.component.StateComponent
import com.github.k1melo.mysticwoods.event.EntityDeathEvent
import com.github.k1melo.mysticwoods.event.fire
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([DeadComponent::class])
class DeadSystem(
    private val deadComponent: ComponentMapper<DeadComponent>,
    private val lifeComponent: ComponentMapper<LifeComponent>,
    private val animationComponents: ComponentMapper<AnimationComponent>,
    private val stage: Stage,
) : IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        val deadComp = deadComponent[entity]

        if (deadComp.reviveTime == 0f) {
            stage.fire(EntityDeathEvent(animationComponents[entity].model))

            world.remove(entity)
            return
        }

        deadComp.reviveTime -= deltaTime

        if (deadComp.reviveTime <= 0f) {
            with(lifeComponent[entity]) {life = max}
            configureEntity(entity){ deadComponent.remove(entity) }
        }

    }
}

