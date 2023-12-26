package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import com.github.k1melo.mysticwoods.component.ImageComponent
import com.github.k1melo.mysticwoods.component.PhysicComponent
import com.github.quillraven.fleks.*
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

@AllOf([PhysicComponent::class, ImageComponent::class])
class PhysicSystem (
    private val physicWorld: World,
    private val imageComponent: ComponentMapper<ImageComponent>,
    private val physicComponent: ComponentMapper<PhysicComponent>
) : IteratingSystem(interval = Fixed(1/60f)) {

    override fun onUpdate() {
        if(physicWorld.autoClearForces) {
            log.error { "AutoClearForces must be set to false to correct physic simulation" }
            physicWorld.autoClearForces = false
        }

        super.onUpdate()
        physicWorld.clearForces()
    }

    override fun onTick() {
        super.onTick()
        physicWorld.step(deltaTime, 6, 2)
    }

    override fun onTickEntity(entity: Entity) {
        val physicComponent = physicComponent[entity]


        physicComponent.prevPos.set(physicComponent.body.position)

        if (!physicComponent.impulse.isZero) {
            physicComponent.body.applyLinearImpulse(physicComponent.impulse, physicComponent.body.worldCenter, true)
            physicComponent.impulse.setZero()
        }

    }

    override fun onAlphaEntity(entity: Entity, alpha: Float) {
        val physicComponent = physicComponent[entity]
        val imageComponent = imageComponent[entity]

        val (prevX, prevY) = physicComponent.prevPos
         val (bodyX, bodyY) = physicComponent.body.position
        imageComponent.image.run {
            setPosition((MathUtils.lerp(prevX, bodyX, alpha) - width * 0.5f), (MathUtils.lerp(prevY, bodyY, alpha) - height * 0.5f))
        }
    }

    companion object {
        private val log = logger<PhysicSystem>()
    }

}
