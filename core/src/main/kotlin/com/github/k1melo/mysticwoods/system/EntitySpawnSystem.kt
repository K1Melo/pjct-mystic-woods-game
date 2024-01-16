package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.github.k1melo.mysticwoods.MysticWoods
import com.github.k1melo.mysticwoods.actor.FlipImage
import com.github.k1melo.mysticwoods.component.*
import com.github.k1melo.mysticwoods.component.PhysicComponent.Companion.physicComponentFromImage
import com.github.k1melo.mysticwoods.event.MapChangeEvent
import com.github.k1melo.mysticwoods.screens.GameScreen
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.app.gdxError
import ktx.box2d.box
import ktx.math.vec2
import ktx.tiled.layer
import ktx.tiled.type
import ktx.tiled.x
import ktx.tiled.y
import kotlin.math.max
import kotlin.math.roundToInt

@AllOf([SpawnComponent::class])
class EntitySpawnSystem(
    private val physicWorld: World,
    private val atlas : TextureAtlas,
    private val spawnComponent : ComponentMapper<SpawnComponent>
) : EventListener, IteratingSystem() {

    private val cachedConfigurations = mutableMapOf<String, SpawnConfiguration>()
    private val cachedSizes = mutableMapOf<AnimationModel, Vector2>()
    override fun onTickEntity(entity: Entity) {
        with(spawnComponent[entity]) {
            val config = spawnConfiguration(type)
            val relativeSize = size(config.model)

            world.entity {
                val imageComp = add<ImageComponent> {
                    image = FlipImage().apply {
                        setPosition(location.x, location.y)
                        setSize(relativeSize.x, relativeSize.y)
                        setScaling(Scaling.fill)
                    }
                }

                add<AnimationComponent> {
                    nextAnimation(config.model, AnimationType.IDLE)
                }

                physicComponentFromImage(physicWorld, imageComp.image, config.bodyType) { physicComponent, width, height ->
                    val w = width * config.physicScaling.x
                    val h = height * config.physicScaling.y
                    physicComponent.offset.set(config.physicOffset)
                    physicComponent.size.set(w, h)

                    box(w, h, config.physicOffset) {
                        isSensor = config.bodyType != BodyDef.BodyType.StaticBody
                        userData = HIT_BOX_SENSOR
                    }

                    if (config.bodyType != BodyDef.BodyType.StaticBody) {
                        val collH = h * 0.4f
                        val collOffset = vec2().apply {set(config.physicOffset) }

                        collOffset.y -= h * 0.5f - collH * 0.5f

                        box(w, collH, collOffset)
                    }
                }

                if (config.speedScaling > 0f) {
                    add<MoveComponent> {
                        speed = DEFAULT_SPEED * config.speedScaling
                    }
                }

                if (config.canAttack) {
                    add<AttackComponent> {
                        maxDelay = config.attackDelay
                        damage = (DEFAULT_ATTACK_DAMAGE * config.attackScaling).roundToInt()
                        extraRange = config.attackExtraRange
                    }
                }

                if (config.lifeScaling > 0f) {
                    add<LifeComponent> {
                        max = DEFAULT_LIFE * config.lifeScaling
                        life = max
                    }
                }

                if (type == "Player") {
                    add<PlayerComponent>()
                }

                if (config.bodyType != BodyDef.BodyType.StaticBody) {
                    add<CollisionComponent>()
                }

            }
        }

        world.remove(entity)
    }

    private fun spawnConfiguration(type : String) : SpawnConfiguration = cachedConfigurations.getOrPut(type) {
        when(type) {
            "Player" -> SpawnConfiguration(
                AnimationModel.PLAYER,
                attackExtraRange = 0.6f,
                attackScaling = 1.25f,
                physicScaling = vec2(0.3f, 0.3f),
                physicOffset = vec2(0f, -10f * MysticWoods.UNIT_SCALE)
            )
            "Slime" -> SpawnConfiguration(
                AnimationModel.SLIME,
                lifeScaling = 0.75f,
                physicScaling = vec2(0.3f, 0.3f),
                physicOffset = vec2(0f, -2f * MysticWoods.UNIT_SCALE)
            )
            "Chest" -> SpawnConfiguration(
                AnimationModel.CHEST,
                speedScaling = 0f,
                bodyType = BodyDef.BodyType.StaticBody,
                canAttack = false,
                lifeScaling = 0f
            )
            else -> gdxError("Type $type has no Spawn setup")
        }
    }

    private fun size(model: AnimationModel) = cachedSizes.getOrPut(model) {
        val regions = atlas.findRegions("${model.atlasKey}/${AnimationType.IDLE.atlasKey}")
        if(regions.isEmpty) {
            gdxError("No regions for $model")
        }

        val firstFrame = regions.first()
        vec2((firstFrame.originalWidth * MysticWoods.UNIT_SCALE), (firstFrame.originalHeight * MysticWoods.UNIT_SCALE))
    }

    override fun handle(event: Event): Boolean {
        when(event) {
            is MapChangeEvent -> {
                val entityLayer = event.map.layer("entities")
                entityLayer.objects.forEach { mapObject ->
                    val type = mapObject.type ?: gdxError(mapObject.name)
                    world.entity {
                        add<SpawnComponent> {
                            this.type = type
                            this.location.set((mapObject.x * MysticWoods.UNIT_SCALE), (mapObject.y * MysticWoods.UNIT_SCALE))
                        }
                    }

                }
                return true
            }
        }

        return false
    }

    companion object {
        const val HIT_BOX_SENSOR = "Hitbox"
    }
}
