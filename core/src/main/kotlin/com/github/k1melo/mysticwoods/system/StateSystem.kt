package com.github.k1melo.mysticwoods.system

import com.github.k1melo.mysticwoods.component.StateComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([StateComponent::class])
class StateSystem(
    private val stateComponents: ComponentMapper<StateComponent>,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        with(stateComponents[entity]) {
            if (nextState != stateMachine.currentState) {
                stateMachine.changeState(nextState)
            }
            stateMachine.update()
        }
    }
}
