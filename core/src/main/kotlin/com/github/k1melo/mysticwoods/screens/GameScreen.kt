package com.github.k1melo.mysticwoods.screens

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.EventListener

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.k1melo.mysticwoods.component.*
import com.github.k1melo.mysticwoods.event.MapChangeEvent
import com.github.k1melo.mysticwoods.event.fire
import com.github.k1melo.mysticwoods.system.AnimationSystem
import com.github.k1melo.mysticwoods.system.RenderSystem
import com.github.quillraven.fleks.World
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.log.logger

class GameScreen : KtxScreen {

    private val textureAtlas: TextureAtlas = TextureAtlas("graphics/gameFrames.atlas")

    private val stage: Stage = Stage(ExtendViewport(16f, 9f))
    private val world: World = World{
        inject(stage)
        inject(textureAtlas)

        componentListener<ImageComponentListener>()

        system<AnimationSystem>()
        system<RenderSystem>()
    }

    override fun show() {
        log.debug { "GameScreen gets Shown" }

        world.systems.forEach { system ->
            if (system is EventListener) {
                stage.addListener(system)
            }
        }

        val tiledMap = TmxMapLoader().load("map/map1.tmx")
        stage.fire(MapChangeEvent(tiledMap))

        world.entity {
            add<ImageComponent> {
                image = Image().apply {
                    setPosition(1f, 1f)
                    setSize(4f, 4f)
                }
            }
            add<AnimationComponent> {
                nextAnimation(AnimationModel.PLAYER, AnimationType.IDLE)
            }
        }

        world.entity {
            add<ImageComponent> {
                image = Image().apply {
                    setPosition(12f, 1f)
                    setSize(2f, 2f)
                }
            }
            add<AnimationComponent> {
                nextAnimation(AnimationModel.SLIME, AnimationType.RUN)
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        world.update(delta)
    }

    override fun dispose() {
        stage.disposeSafely()
        textureAtlas.disposeSafely()
        world.dispose()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
