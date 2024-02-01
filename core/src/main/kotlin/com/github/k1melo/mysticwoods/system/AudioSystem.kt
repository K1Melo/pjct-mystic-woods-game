package com.github.k1melo.mysticwoods.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.k1melo.mysticwoods.event.EntityAttackEvent
import com.github.k1melo.mysticwoods.event.EntityDeathEvent
import com.github.k1melo.mysticwoods.event.EntityLootEvent
import com.github.k1melo.mysticwoods.event.MapChangeEvent
import com.github.quillraven.fleks.IntervalSystem
import ktx.assets.disposeSafely
import ktx.log.logger
import ktx.tiled.propertyOrNull

class AudioSystem : EventListener, IntervalSystem() {

    private val musicCache = mutableMapOf<String, Music>()
    private val soundCache = mutableMapOf<String, Sound>()
    private val soundRequest = mutableMapOf<String, Sound>()

    override fun handle(event: Event): Boolean {
        when(event) {
            is MapChangeEvent -> {
                event.map.propertyOrNull<String>("music")?.let { path ->
                    log.debug { "Changing music to $path" }
                    val music = musicCache.getOrPut(path) {
                        Gdx.audio.newMusic(Gdx.files.internal(path)).apply {
                            isLooping = true
                        }
                    }

                    music.play()
                }

                return true
            }

            is EntityAttackEvent -> queueSound("audio/${event.model.atlasKey}_attack.wav")
            is EntityDeathEvent -> queueSound("audio/${event.model.atlasKey}_death.wav")
            is EntityLootEvent -> queueSound("audio/${event.model.atlasKey}_open.wav")
        }

        return false
    }

    private fun queueSound(soundPath: String) {
        log.debug { "Queuing sound $soundPath" }
        if(soundPath in soundRequest) {
          return
        }

        val sound = soundCache.getOrPut(soundPath) {
            Gdx.audio.newSound(Gdx.files.internal(soundPath))
        }

        soundRequest[soundPath] = sound
    }

    override fun onDispose() {
        musicCache.values.forEach { it.disposeSafely() }
        soundCache.values.forEach { it.disposeSafely() }
    }

    override fun onTick() {
        if(soundRequest.isEmpty()) {
            return
        }

        soundRequest.values.forEach { it.play(1f) }
        soundRequest.clear()
    }

    companion object {
        private val log = logger<AudioSystem>()
    }
}
