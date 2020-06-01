package com.zyyoona7.wheel.sound

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import androidx.annotation.RawRes
import kotlin.math.max
import kotlin.math.min

/**
 * SoundPool 辅助类
 */
@Suppress("DEPRECATION")
class SoundHelper private constructor() {

    private val soundPool: SoundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        SoundPool.Builder().build()
    } else {
        SoundPool(1, AudioManager.STREAM_MUSIC, 0)
    }
    private var soundId: Int = 0
    /**
     * 音频播放音量 range 0.0-1.0
     */
    var soundPlayVolume: Float = 0f
        set(value) {
            field = min(1f, max(value, 0f))
        }

    companion object {

        /**
         * 初始化 SoundHelper
         *
         * @return SoundHelper 对象
         */
        @JvmStatic
        fun obtain(): SoundHelper {
            return SoundHelper()
        }
    }

    /**
     * 加载音频资源
     *
     * @param context 上下文
     * @param resId   音频资源 [RawRes]
     */
    fun load(context: Context, @RawRes resId: Int) {
        soundId = soundPool.load(context, resId, 1)
    }

    /**
     * 播放声音效果
     */
    fun playSoundEffect() {
        if (soundId != 0) {
            soundPool.play(soundId, soundPlayVolume, soundPlayVolume, 1, 0, 1f)
        }
    }

    /**
     * 释放SoundPool
     */
    fun release() {
        soundPool.release()
    }
}