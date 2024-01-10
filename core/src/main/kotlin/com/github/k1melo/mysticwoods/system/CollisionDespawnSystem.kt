package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.k1melo.mysticwoods.component.TiledComponent
import com.github.k1melo.mysticwoods.event.CollisionDespawnEvent
import com.github.k1melo.mysticwoods.event.fire
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([TiledComponent::class])
class CollisionDespawnSystem(
    private val tiledComponents: ComponentMapper<TiledComponent>,
    private val stage: Stage
) : IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        if (tiledComponents[entity].nearbyEntities.isEmpty()) {
            stage.fire(CollisionDespawnEvent(tiledComponents[entity].cell))

            world.remove(entity)
        }
    }
}

