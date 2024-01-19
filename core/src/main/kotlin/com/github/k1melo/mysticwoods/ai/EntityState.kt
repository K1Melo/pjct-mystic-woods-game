package com.github.k1melo.mysticwoods.ai

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram

interface EntityState: State<AiEntity> {
    override fun enter(p0: AiEntity) = Unit

    override fun update(p0: AiEntity) = Unit

    override fun exit(p0: AiEntity) = Unit

    override fun onMessage(p0: AiEntity?, p1: Telegram?) = false
}
