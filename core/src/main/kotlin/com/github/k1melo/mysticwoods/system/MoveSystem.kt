package com.github.k1melo.mysticwoods.system

import com.github.k1melo.mysticwoods.component.ImageComponent
import com.github.k1melo.mysticwoods.component.MoveComponent
import com.github.k1melo.mysticwoods.component.PhysicComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.math.component1
import ktx.math.component2

@AllOf([MoveComponent::class, PhysicComponent::class])
class MoveSystem(
    private val moveComponent: ComponentMapper<MoveComponent>,
    private val physicComponent: ComponentMapper<PhysicComponent>,
    private val imageComponent: ComponentMapper<ImageComponent>
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val moveComponent = moveComponent[entity]
        val physicComponent = physicComponent[entity]
        val mass = physicComponent.body.mass
        val (velX, velY) = physicComponent.body.linearVelocity

        if ((moveComponent.cos == 0f && moveComponent.sin == 0f) || moveComponent.root) {
            physicComponent.impulse.set(
                mass * (0f - velX),
                mass * (0f - velY),
            )
            return
        }

        physicComponent.impulse.set(
            mass * (moveComponent.speed * moveComponent.cos - velX),
            mass * (moveComponent.speed * moveComponent.sin - velY)
        )

        imageComponent.getOrNull(entity)?.let { imageComponent ->
            if (moveComponent.cos != 0f) {
                imageComponent.image.flipX = moveComponent.cos < 0
            }
        }
    }
}
