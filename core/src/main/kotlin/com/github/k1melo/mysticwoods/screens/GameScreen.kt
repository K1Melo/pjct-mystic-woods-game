package com.github.k1melo.mysticwoods.screens

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.k1melo.mysticwoods.component.ImageComponent
import com.github.k1melo.mysticwoods.component.ImageComponentListener
import com.github.k1melo.mysticwoods.system.RenderSystem
import com.github.quillraven.fleks.World
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.log.logger

class GameScreen : KtxScreen {

    private val textureAtlas: TextureAtlas = TextureAtlas("assets/graphics/gameObjects.atlas")

    private val stage: Stage = Stage(ExtendViewport(16f, 9f))
    private val world: World = World{
        inject(stage)

        componentListener<ImageComponentListener>()

        system<RenderSystem>()
    }

    override fun show() {
        log.debug { "GameScreen gets Shown" }

        world.entity {
            add<ImageComponent> {
                image = Image(TextureRegion(textureAtlas.findRegion("player"), 0, 48, 48, 48)).apply {
                    setPosition(1f, 1f)
                    setSize(4f, 4f)
                }
            }
        }

        world.entity {
            add<ImageComponent> {
                image = Image(TextureRegion(textureAtlas.findRegion("slime"), 0, 0, 32, 32)).apply {
                    setPosition(12f, 1f)
                    setSize(2f, 2f)
                }
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
