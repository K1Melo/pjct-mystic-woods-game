package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.World
import com.github.k1melo.mysticwoods.component.CollisionComponent
import com.github.k1melo.mysticwoods.component.ImageComponent
import com.github.k1melo.mysticwoods.component.PhysicComponent
import com.github.k1melo.mysticwoods.component.TiledComponent
import com.github.quillraven.fleks.*
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

@AllOf([PhysicComponent::class, ImageComponent::class])
class PhysicSystem (
    private val physicWorld: World,
    private val imageComponent: ComponentMapper<ImageComponent>,
    private val physicComponent: ComponentMapper<PhysicComponent>,
    private val tiledComponent: ComponentMapper<TiledComponent>,
    private val collisionComponent: ComponentMapper<CollisionComponent>,
) : ContactListener , IteratingSystem(interval = Fixed(1/60f)) {

    init {
        physicWorld.setContactListener(this)
    }

    private val Fixture.entity : Entity
        get() = this.body.userData as Entity

    override fun beginContact(contact: Contact) {
        val entityA : Entity = contact.fixtureA.entity
        val entityB : Entity = contact.fixtureB.entity

        when {
            (entityA in tiledComponent && contact.fixtureA.isSensor) && (entityB in collisionComponent && !contact.fixtureB.isSensor) -> {
                tiledComponent[entityA].nearbyEntities+= entityB
            }

            (entityB in tiledComponent && contact.fixtureB.isSensor) && (entityA in collisionComponent && !contact.fixtureA.isSensor) -> {
                tiledComponent[entityB].nearbyEntities += entityA
            }
        }
    }

    override fun endContact(contact: Contact) {
        val entityA : Entity = contact.fixtureA.entity
        val entityB : Entity = contact.fixtureB.entity

        when {
            (entityA in tiledComponent && contact.fixtureA.isSensor) && !contact.fixtureB.isSensor -> {
                tiledComponent[entityA].nearbyEntities -= entityB
            }

            (entityB in tiledComponent && contact.fixtureB.isSensor) && !contact.fixtureA.isSensor -> {
                tiledComponent[entityB].nearbyEntities -= entityA
            }
        }
    }

    private fun Fixture.isStaticBody() = this.body.type == BodyDef.BodyType.StaticBody
    private fun Fixture.isDynamicBody() = this.body.type == BodyDef.BodyType.DynamicBody

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        contact.isEnabled = (contact.fixtureA.isStaticBody() && contact.fixtureB.isDynamicBody()) || (contact.fixtureB.isDynamicBody() && contact.fixtureA.isStaticBody())
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) = Unit

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
