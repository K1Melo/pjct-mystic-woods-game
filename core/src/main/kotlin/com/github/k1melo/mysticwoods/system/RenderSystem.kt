package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.k1melo.mysticwoods.component.ImageComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.collection.compareEntity

@AllOf([ImageComponent::class])
class RenderSystem(
    private val stage: Stage,
    private val imageComponents: ComponentMapper<ImageComponent>
) : IteratingSystem(
    comparator = compareEntity {
    entity, entity2 -> imageComponents[entity].compareTo(imageComponents[entity2])
    }
) {

    override fun onTick() {
        super.onTick()

        with(stage) {
            viewport.apply()
            act(deltaTime)
            draw()
        }
    }
    override fun onTickEntity(entity: Entity) {
        imageComponents[entity].image.toFront()
    }
}
