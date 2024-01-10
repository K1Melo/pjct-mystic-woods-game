package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.k1melo.mysticwoods.component.CollisionComponent
import com.github.k1melo.mysticwoods.component.PhysicComponent
import com.github.k1melo.mysticwoods.component.PhysicComponent.Companion.physicComponentFromShape2d
import com.github.k1melo.mysticwoods.component.TiledComponent
import com.github.k1melo.mysticwoods.event.CollisionDespawnEvent
import com.github.k1melo.mysticwoods.event.MapChangeEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.box2d.body
import ktx.box2d.loop
import ktx.collections.GdxArray
import ktx.math.component1
import ktx.math.component2
import ktx.math.vec2
import ktx.tiled.*
import kotlin.math.max

@AllOf([PhysicComponent::class, CollisionComponent::class])
class CollisionSpawnSystem(
    private val physicWorld: World,
    private val physicComponent: ComponentMapper<PhysicComponent>
) : EventListener, IteratingSystem() {

    private val tiledLayers = GdxArray<TiledMapTileLayer>()
    private val processCells = mutableSetOf<Cell>()

    private fun TiledMapTileLayer.forEachCell(
        startX : Int,
        startY : Int,
        size : Int,
        action: (TiledMapTileLayer.Cell, Int, Int) -> Unit
    ) {
        for (x in startX-size .. startX + size) {
            for (y in startY-size .. startY + size) {
                this.getCell(x, y)?.let { action(it, x, y) }
            }
        }
    }

    override fun onTickEntity(entity: Entity) {
        val(entityX, entityY) = physicComponent[entity].body.position

        tiledLayers.forEach { layer ->
            layer.forEachCell(entityX.toInt(), entityY.toInt(), SPAWN_AREA_SIZE) { cell, x, y ->
                if(cell.tile.objects.isEmpty()) {
                    return@forEachCell
                }

                if (cell in processCells) {
                    return@forEachCell
                }

                processCells.add(cell)
                cell.tile.objects.forEach { mapObject ->
                    world.entity {
                        physicComponentFromShape2d(physicWorld, x, y, mapObject.shape)
                        add<TiledComponent> {
                            this.cell = cell
                            nearbyEntities.add(entity)
                        }
                    }
                }
            }
        }
    }

    override fun handle(event: Event): Boolean {
        when(event) {
            is MapChangeEvent -> {

                event.map.layers.getByType(TiledMapTileLayer::class.java, tiledLayers)

                world.entity {
                    val w = event.map.width.toFloat()
                    val h = event.map.height.toFloat()

                    add<PhysicComponent> {
                        body = physicWorld.body(BodyDef.BodyType.StaticBody) {
                            position.set(0f, 0f)
                            fixedRotation = true
                            allowSleep = false
                            loop(
                                vec2(0f, 0f),
                                vec2(w, 0f),
                                vec2(w, h),
                                vec2(0f, h),
                            )
                        }
                    }
                }

                return true
            }
            is CollisionDespawnEvent -> {
                processCells.remove(event.cell)
                return true
            }
            else -> return false
        }
    }

    companion object {
        const val SPAWN_AREA_SIZE = 3
    }
}
