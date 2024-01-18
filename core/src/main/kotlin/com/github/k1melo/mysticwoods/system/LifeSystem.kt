package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.github.k1melo.mysticwoods.component.*
import com.github.quillraven.fleks.*
import ktx.assets.disposeSafely

@AllOf([LifeComponent::class])
@NoneOf([DeadComponent::class])
class LifeSystem(
    private val lifeComponent: ComponentMapper<LifeComponent>,
    private val deadComponent: ComponentMapper<DeadComponent>,
    private val playerComponent: ComponentMapper<PlayerComponent>,
    private val physicComponent: ComponentMapper<PhysicComponent>,
) : IteratingSystem() {

    private val damageFont = BitmapFont(Gdx.files.internal("damage.fnt"))
    private val floatingTextStyle = LabelStyle(damageFont, Color.WHITE)
    override fun onTickEntity(entity: Entity) {
        val lifeComp = lifeComponent[entity]
        lifeComp.life = (lifeComp.life + (lifeComp.regeneration * deltaTime)).coerceAtMost(lifeComp.max)

        if (lifeComp.takeDamage > 0f) {
            val physicCmp = physicComponent[entity]
            lifeComp.life -= lifeComp.takeDamage

            floatingText(lifeComp.takeDamage.toInt().toString(), physicCmp.body.position, physicCmp.size)
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

    private fun floatingText(text: String, position: Vector2, size: Vector2) {
        world.entity {
            add<FloatingTextComponent> {
                txtLocation.set(position.x, position.y + (size.y * 0.5f))
                lifeSpan = 1.5f
                label = Label(text, floatingTextStyle)
            }
        }
    }

    override fun onDispose() {
        damageFont.disposeSafely()
    }
}
