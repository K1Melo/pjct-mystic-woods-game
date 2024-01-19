package com.github.k1melo.mysticwoods.screens

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.EventListener

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.k1melo.mysticwoods.component.*
import com.github.k1melo.mysticwoods.event.MapChangeEvent
import com.github.k1melo.mysticwoods.event.fire
import com.github.k1melo.mysticwoods.input.PlayerKeyboardInputProcessor
import com.github.k1melo.mysticwoods.system.*
import com.github.quillraven.fleks.World
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.math.vec2

class GameScreen : KtxScreen {

    private val gameStage: Stage = Stage(ExtendViewport(16f, 9f))
    private val uiStage: Stage = Stage(ExtendViewport(1280f, 720f))
    private val textureAtlas: TextureAtlas = TextureAtlas("graphics/gameFrames.atlas")
    private var currentMap : TiledMap? = null;
    private val physicWorld = createWorld(
        gravity = vec2()
    ).apply {
        autoClearForces = false
    }

    private val entityWorld: World = World{
        inject(gameStage)
        inject("uiStage", uiStage)
        inject(textureAtlas)
        inject(physicWorld)

        componentListener<ImageComponentListener>()
        componentListener<PhysicComponent.Companion.PhysicComponentListener>()
        componentListener<FloatingTextComponent.Companion.FloatingTextComponentListener>()
        componentListener<StateComponent.Companion.StateComponentListener>()

        system<EntitySpawnSystem>()
        system<CollisionSpawnSystem>()
        system<CollisionDespawnSystem>()
        system<MoveSystem>()
        system<AttackSystem>()
        system<LootSystem>()
        system<DeadSystem>()
        system<LifeSystem>()
        system<PhysicSystem>()
        system<AnimationSystem>()
        system<StateSystem>()
        system<CameraSystem>()
        system<FloatingTextSystem>()
        system<RenderSystem>()
        system<DebugSystem>()
    }

    override fun show() {
        log.debug { "GameScreen gets Shown" }

        entityWorld.systems.forEach { system ->
            if (system is EventListener) {
                gameStage.addListener(system)
            }
        }

        currentMap = TmxMapLoader().load("map/map1.tmx")
        gameStage.fire(MapChangeEvent(currentMap!!))

        PlayerKeyboardInputProcessor(entityWorld)
    }

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        entityWorld.update(delta.coerceAtMost(0.25f))
    }

    override fun dispose() {
        gameStage.disposeSafely()
        uiStage.disposeSafely()
        textureAtlas.disposeSafely()
        entityWorld.dispose()
        currentMap?.disposeSafely()
        physicWorld.disposeSafely()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
