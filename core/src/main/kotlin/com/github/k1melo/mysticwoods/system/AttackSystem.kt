package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.k1melo.mysticwoods.component.*
import com.github.k1melo.mysticwoods.event.EntityAttackEvent
import com.github.k1melo.mysticwoods.event.fire
import com.github.quillraven.fleks.*
import ktx.box2d.query
import ktx.math.component1
import ktx.math.component2

@AllOf([AttackComponent::class, PhysicComponent::class, ImageComponent::class])
class AttackSystem(
    private val attackComponents: ComponentMapper<AttackComponent>,
    private val physicComponents: ComponentMapper<PhysicComponent>,
    private val imageComponents: ComponentMapper<ImageComponent>,
    private val lifeComponents: ComponentMapper<LifeComponent>,
    private val lootComponents: ComponentMapper<LootComponent>,
    private val animationComponents: ComponentMapper<AnimationComponent>,
    private val playerComponents: ComponentMapper<PlayerComponent>,
    private val physicWorld : World,
    private val stage: Stage,
) : IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        val attackComponent = attackComponents[entity]

        if (attackComponent.isReady && !attackComponent.doAttack) {
            return
        }

        if (attackComponent.isPrepared && attackComponent.doAttack) {
            attackComponent.doAttack = false

            attackComponent.state = AttackState.ATTACKING
            attackComponent.delay = attackComponent.maxDelay

            stage.fire(EntityAttackEvent(animationComponents[entity].model))

            return
        }

        attackComponent.delay -= deltaTime

        if (attackComponent.delay <= 0f && attackComponent.isAttacking) {

            attackComponent.state = AttackState.DEAL_DAMAGE

            val image = imageComponents[entity].image
            val physicComponent = physicComponents[entity]

            val attackLeft = image.flipX
            val (x, y) = physicComponent.body.position
            val (offX, offY) = physicComponent.offset
            val (w, h) = physicComponent.size
            val  halfW = w * 0.5f
            val  halfH = h * 0.5f

            if (attackLeft) {
                AABB_RECT.set(
                    x + offX - halfW - attackComponent.extraRange,
                    y + offY - halfH,
                    x + offX + halfW,
                    y + offY + halfH,
                )
            } else {
                AABB_RECT.set(
                    x + offX - halfW,
                    y + offY - halfH,
                    x + offX + halfW + attackComponent.extraRange,
                    y + offY + halfH,
                )
            }

            physicWorld.query(AABB_RECT.x, AABB_RECT.y, AABB_RECT.width, AABB_RECT.height) { fixture ->
                if (fixture.userData != EntitySpawnSystem.HIT_BOX_SENSOR) {
                    return@query true
                }

                val fixtureEntity = fixture.entity
                if (fixtureEntity == entity) {
                    return@query true
                }

                configureEntity(fixtureEntity) {
                    lifeComponents.getOrNull(it)?.let { lifeComponent ->
                        lifeComponent.takeDamage += attackComponent.damage * MathUtils.random(0.9f, 1.1f)
                    }

                    if (entity in playerComponents) {
                        lootComponents.getOrNull(it)?.let { lootComponent ->
                            lootComponent.interactEntity = entity
                        }
                    }
                }

                return@query true
            }

        }

        val isDone = animationComponents.getOrNull(entity)?.isAnimationDone ?: true
        if(isDone) {
            attackComponent.state = AttackState.READY
        }
    }

    companion object {
        val AABB_RECT = Rectangle()
    }
}
