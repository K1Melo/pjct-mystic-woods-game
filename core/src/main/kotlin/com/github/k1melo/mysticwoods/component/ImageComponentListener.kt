package com.github.k1melo.mysticwoods.component

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity

class ImageComponentListener(
    private val stage: Stage
) : ComponentListener<ImageComponent> {
    override fun onComponentAdded(entity: Entity, component: ImageComponent) {
        stage.addActor(component.image)
    }

    override fun onComponentRemoved(entity: Entity, component: ImageComponent) {
        stage.root.removeActor(component.image)
    }

}
