package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.IntervalSystem
import ktx.assets.disposeSafely
import ktx.graphics.use

class DebugSystem(
    private val physicWorld: World,
    private val stage : Stage
) : IntervalSystem(enabled = false) {

    private lateinit var physicRenderer : Box2DDebugRenderer
    private lateinit var shapeRenderer: ShapeRenderer

    init {
        if (enabled) {
            physicRenderer = Box2DDebugRenderer()
            shapeRenderer = ShapeRenderer()
        }
    }

    override fun onTick() {
        physicRenderer.render(physicWorld, stage.camera.combined)
        shapeRenderer.use(ShapeRenderer.ShapeType.Line, stage.camera.combined) {
            it.setColor(1f, 0f, 0f, 0f)
            it.rect(AttackSystem.AABB_RECT.x, AttackSystem.AABB_RECT.y, AttackSystem.AABB_RECT.width - AttackSystem.AABB_RECT.x, AttackSystem.AABB_RECT.height - AttackSystem.AABB_RECT.y)
        }
    }

    override fun onDispose() {
        if (enabled) {
            physicRenderer.disposeSafely()
            shapeRenderer.disposeSafely()
        }
    }
}
