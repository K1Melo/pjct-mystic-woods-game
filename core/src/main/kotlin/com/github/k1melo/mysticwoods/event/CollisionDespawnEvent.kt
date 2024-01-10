package com.github.k1melo.mysticwoods.event

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.scenes.scene2d.Event

class CollisionDespawnEvent(val cell:Cell) : Event() {
}
