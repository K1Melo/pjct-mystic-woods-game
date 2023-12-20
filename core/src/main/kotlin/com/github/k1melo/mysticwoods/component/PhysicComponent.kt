package com.github.k1melo.mysticwoods.component

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateCfg
import ktx.box2d.BodyDefinition
import ktx.box2d.body

class PhysicComponent {
    lateinit var body : Body

    companion object {
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
