package com.github.k1melo.mysticwoods.ai

import com.badlogic.gdx.graphics.g2d.Animation
import com.github.k1melo.mysticwoods.component.AnimationType

enum class DefaultState : EntityState{
    IDLE{
        override fun enter(p0: AiEntity) {
            p0.animation(AnimationType.IDLE)
        }

        override fun update(p0: AiEntity) {
            when {
                p0.wantsToAttack -> p0.state(ATTACK)
                p0.wantsToRun -> p0.state(RUN)
            }
        }
    },
    RUN {
        override fun enter(p0: AiEntity) {
            p0.animation(AnimationType.RUN)
        }

        override fun update(p0: AiEntity) {
            when {
                p0.wantsToAttack -> p0.state(ATTACK)
                !p0.wantsToRun -> p0.state(IDLE)
            }
        }
    },
    ATTACK {
        override fun enter(p0: AiEntity) {
            p0.animation(AnimationType.ATTACK, Animation.PlayMode.NORMAL)
            p0.root(true)
            p0.startAttack()
        }

        override fun exit(p0: AiEntity) {
            p0.root(false)
        }

        override fun update(p0: AiEntity) {
            val attackComponent = p0.attackComponent
            if (attackComponent.isReady && !attackComponent.doAttack) {
                p0.changeToPreviousState()
            } else if(attackComponent.isReady) {
                p0.animation(AnimationType.ATTACK, Animation.PlayMode.NORMAL, true)
                p0.startAttack()
            }
        }
    },
    DEAD {
        override fun enter(p0: AiEntity) {
            p0.root(true)
        }

        override fun update(p0: AiEntity) {
            if(!p0.isDead) {
                p0.state(RESURRECT)
            }
        }
    },
    RESURRECT {
        override fun enter(p0: AiEntity) {
            p0.enableGlobalState(false)
            p0.animation(AnimationType.DEATH, Animation.PlayMode.REVERSED, true)
        }

        override fun update(p0: AiEntity) {
            if (p0.isAnimationDone) {
                p0.state(IDLE)
                p0.root(false)
            }
        }
    },

}

enum class DefaultGlobalState : EntityState {
    CHECK_ALIVE {
        override fun update(p0: AiEntity) {
            if (p0.isDead) {
                p0.enableGlobalState(false)
                p0.state(DefaultState.DEAD, true)
            }
        }
    }
}
