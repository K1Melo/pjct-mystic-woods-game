package com.github.k1melo.mysticwoods.component

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.k1melo.mysticwoods.MysticWoods
import com.github.k1melo.mysticwoods.system.CollisionSpawnSystem
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateCfg
import ktx.app.gdxError
import ktx.box2d.BodyDefinition
import ktx.box2d.body
import ktx.box2d.circle
import ktx.box2d.loop
import ktx.math.vec2

class PhysicComponent {
    val prevPos = vec2()
    val impulse = vec2()
    lateinit var body : Body

    companion object {
        fun EntityCreateCfg.physicComponentFromShape2d(world: World, x: Int, y: Int, shape: Shape2D): PhysicComponent {
            when(shape) {
                is Rectangle -> {
                    val bodyX = x + shape.x * MysticWoods.UNIT_SCALE
                    val bodyY = y + shape.y * MysticWoods.UNIT_SCALE
                    val bodyW = shape.width * MysticWoods.UNIT_SCALE
                    val bodyH = shape.height * MysticWoods.UNIT_SCALE

                    return add {
                        body = world.body(BodyType.StaticBody) {
                            position.set(bodyX, bodyY)
                            fixedRotation = true
                            allowSleep = false
                            loop(
                                vec2(0f, 0f),
                                vec2(bodyW, 0f),
                                vec2(bodyW, bodyH),
                                vec2(0f, bodyH)
                            )
                            circle(CollisionSpawnSystem.SPAWN_AREA_SIZE+2f) {
                                isSensor = true
                            }
                        }
                    }
                }
                else -> gdxError("Shape is not supported")
            }
        }

        fun EntityCreateCfg.physicComponentFromImage(
            world: World,
            image: Image,
            bodyType: BodyType,
            fixtureAction: BodyDefinition.(PhysicComponent, Float, Float) -> Unit
        ) : PhysicComponent {
            val x = image.x
            val y = image.y
            val w = image.width
            val h = image.height

            return add<PhysicComponent> {
                body = world.body(bodyType) {
                    position.set((x+w*0.5f), (y+h*0.5f))
                    fixedRotation = true
                    allowSleep = false
                    this.fixtureAction(this@add, w, h)
                }

            }
        }

        class PhysicComponentListener : ComponentListener<PhysicComponent> {
            override fun onComponentAdded(entity: Entity, component: PhysicComponent) {
                component.body.userData = entity
            }

            override fun onComponentRemoved(entity: Entity, component: PhysicComponent) {
                component.body.world.destroyBody(component.body)
                component.body.userData = null
            }
        }
    }
}
