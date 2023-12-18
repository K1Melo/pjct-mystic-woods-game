package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.k1melo.mysticwoods.component.ImageComponent
import com.github.k1melo.mysticwoods.event.MapChangeEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.collection.compareEntity
import ktx.graphics.use
import ktx.tiled.forEachLayer

@AllOf([ImageComponent::class])
class RenderSystem(
    private val stage: Stage,
    private val imageComponents: ComponentMapper<ImageComponent>
) : EventListener, IteratingSystem(
    comparator = compareEntity {
    entity, entity2 -> imageComponents[entity].compareTo(imageComponents[entity2])
    }
) {

    private val bgdLayers = mutableListOf<TiledMapTileLayer>()
    private val fgdLayers = mutableListOf<TiledMapTileLayer>()
    private val mapRenderer = OrthogonalTiledMapRenderer(null, 1/16f, stage.batch)
    private val orthoCam = stage.camera as OrthographicCamera

    override fun handle(event: Event): Boolean {
        when(event) {
             is MapChangeEvent -> {
                 bgdLayers.clear()
                 fgdLayers.clear()

                event.map.forEachLayer<TiledMapTileLayer> { layer ->
                    if (layer.name.startsWith("fgd_")) {
                        fgdLayers.add(layer)
                    } else {
                        bgdLayers.add(layer)
                    }
                }
                return true
            }
        }
        return false
    }

    override fun onTick() {
        super.onTick()

        with(stage) {
            viewport.apply()

            AnimatedTiledMapTile.updateAnimationBaseTime()
            mapRenderer.setView(orthoCam)

            if(bgdLayers.isNotEmpty()) {
                stage.batch.use(orthoCam.combined) {
                    bgdLayers.forEach {
                        mapRenderer.renderTileLayer(it)
                    }
                }
            }

            act(deltaTime)
            draw()

            if(fgdLayers.isNotEmpty()) {
                stage.batch.use(orthoCam.combined) {
                    fgdLayers.forEach {
                        mapRenderer.renderTileLayer(it)
                    }
                }
            }

        }
    }
    override fun onTickEntity(entity: Entity) {
        imageComponents[entity].image.toFront()
    }
}
