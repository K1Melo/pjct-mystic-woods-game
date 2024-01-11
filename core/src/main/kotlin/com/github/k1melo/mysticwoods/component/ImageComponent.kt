package com.github.k1melo.mysticwoods.component
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.k1melo.mysticwoods.actor.FlipImage

class ImageComponent : Comparable<ImageComponent>{

    lateinit var image: FlipImage
    override fun compareTo(other: ImageComponent): Int {
        val yDiff = other.image.y.compareTo(image.y)

        return if(yDiff != 0) {
            yDiff
        } else {
            other.image.x.compareTo(image.x)
        }
    }

}
