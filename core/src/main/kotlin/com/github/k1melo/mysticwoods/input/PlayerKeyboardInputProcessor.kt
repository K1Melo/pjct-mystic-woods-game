package com.github.k1melo.mysticwoods.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.github.k1melo.mysticwoods.component.MoveComponent
import com.github.k1melo.mysticwoods.component.PlayerComponent
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import ktx.app.KtxInputAdapter
import kotlin.math.cos

class PlayerKeyboardInputProcessor(
    world: World,
    private val moveComponent : ComponentMapper<MoveComponent>,
) : KtxInputAdapter {

    private var playerSeno = 0f
    private var playerCosseno = 0f

    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class))

    init {
        Gdx.input.inputProcessor = this
    }

    private fun Int.isMovementKey() : Boolean {
        return this == Input.Keys.UP || this == Input.Keys.DOWN || this == Input.Keys.LEFT || this == Input.Keys.RIGHT || this == Input.Keys.W || this == Input.Keys.S || this == Input.Keys.A || this == Input.Keys.D
    }

    private fun updatePlayerMovement() {
        playerEntities.forEach {player ->
            with(moveComponent[player]) {
                cos = playerCosseno
                sin = playerSeno
            }
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if(keycode.isMovementKey()) {
            when(keycode) {
                Input.Keys.W,
                Input.Keys.UP -> playerSeno = 1f

                Input.Keys.S,
                Input.Keys.DOWN -> playerSeno = -1f

                Input.Keys.D,
                Input.Keys.RIGHT -> playerCosseno = 1f

                Input.Keys.A,
                Input.Keys.LEFT -> playerCosseno = -1f
            }
            updatePlayerMovement()
            return true
        }

        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        if(keycode.isMovementKey()) {

            when(keycode) {
                Input.Keys.W,
                Input.Keys.UP -> playerSeno = if(Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) -1f else 0f

                Input.Keys.S,
                Input.Keys.DOWN -> playerSeno = if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) 1f else 0f

                Input.Keys.D,
                Input.Keys.RIGHT -> playerCosseno = if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) -1f else 0f

                Input.Keys.A,
                Input.Keys.LEFT -> playerCosseno = if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) 1f else 0f
            }
            updatePlayerMovement()
            return true
        }

        return false
    }
}
