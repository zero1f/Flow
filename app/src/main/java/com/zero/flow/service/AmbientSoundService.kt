package com.zero.flow.service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.zero.flow.domain.model.AmbientSoundType
import kotlinx.coroutines.flow.MutableStateFlow

class AmbientSoundService : Service() {

    private lateinit var player: ExoPlayer
    private val binder = AmbientSoundBinder()

    val isPlaying = MutableStateFlow(false)

    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                this@AmbientSoundService.isPlaying.value = isPlaying
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    fun playSound(soundType: AmbientSoundType) {
        val rawResId = soundType.toRawResId()
        if (rawResId != null) {
            val uri = Uri.parse("android.resource://$packageName/$rawResId")
            val mediaItem = MediaItem.fromUri(uri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } else {
            stopSound()
        }
    }

    fun stopSound() {
        player.stop()
    }

    fun setVolume(volume: Float) {
        player.volume = volume
    }

    inner class AmbientSoundBinder : Binder() {
        fun getService(): AmbientSoundService = this@AmbientSoundService
    }
}