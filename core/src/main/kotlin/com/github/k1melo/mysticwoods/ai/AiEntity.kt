package com.github.k1melo.mysticwoods.ai

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.k1melo.mysticwoods.component.*
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

data class AiEntity(
    private val entity: Entity,
    private val world: World,
    private val animationComponents: ComponentMapper<AnimationComponent> = world.mapper(),
    private val moveComponents: ComponentMapper<MoveComponent> = world.mapper(),
    private val attackComponents: ComponentMapper<AttackComponent> = world.mapper(),
    private val stateComponents: ComponentMapper<StateComponent> = world.mapper(),
    private val lifeComponents: ComponentMapper<LifeComponent> = world.mapper(),
) {

    val wantsToAttack : Boolean
        get() = attackComponents.getOrNull(entity)?.doAttack ?: false

    val wantsToRun : Boolean
        get() {
            val moveComponent = moveComponents[entity]
            return moveComponent.cos != 0f || moveComponent.sin != 0f
        }

    val attackComponent:AttackComponent
        get() = attackComponents[entity]

    val isAnimationDone : Boolean
        get() = animationComponents[entity].isAnimationDone

    val isDead : Boolean
        get() = lifeComponents[entity].isDead
    fun animation(type: AnimationType, mode : PlayMode = PlayMode.LOOP, resetAnimation : Boolean = false) {
        with(animationComponents[entity]) {
            nextAnimation(type)
            playMode = mode
            if (resetAnimation) {
                stateTime = 0f
            }
        }
    }

    fun state(next : EntityState, immediateChange : Boolean = false) {
        with(stateComponents[entity]) {
            nextState = next
            if (immediateChange) {
                stateMachine.changeState(nextState)
            }
        }
    }

    fun enableGlobalState(enable: Boolean) {
        with(stateComponents[entity]) {
            if(enable) {
                stateMachine.globalState = DefaultGlobalState.CHECK_ALIVE
            } else {
                stateMachine.globalState = null
            }
        }
    }

    fun changeToPreviousState() {
        with(stateComponents[entity]) { nextState = stateMachine.previousState }
    }

    fun root(enable : Boolean) {
        with(moveComponents[entity]) { root = enable }
    }

    fun startAttack() {
        with(attackComponents[entity]) { startAttack() }
    }
}
