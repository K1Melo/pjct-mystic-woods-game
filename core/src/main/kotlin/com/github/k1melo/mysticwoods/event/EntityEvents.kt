package com.github.k1melo.mysticwoods.event

import com.badlogic.gdx.scenes.scene2d.Event
import com.github.k1melo.mysticwoods.component.AnimationModel

class EntityAttackEvent(val model : AnimationModel) : Event()

class EntityDeathEvent(val model : AnimationModel) : Event()

class EntityLootEvent(val model : AnimationModel) : Event()

