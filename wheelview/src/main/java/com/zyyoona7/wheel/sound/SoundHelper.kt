package com.zyyoona7.wheel.sound

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build

import androidx.annotation.FloatRange
import androidx.annotation.RawRes

/**
 * SoundPool 辅助类
 */
class SoundHelper private constructor() {

    private var mSoundPool: SoundPool? = null
    private var mSoundId: Int = 0
    /**
     * 获取音量
     *
     * @return 音频播放音量 range 0.0-1.0
     */
    /**
     * 设置音量
     *
     * @param playVolume 音频播放音量 range 0.0-1.0
     */
    var playVolume: Float = 0.toFloat()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = SoundPool.Builder().build()
        } else {
            mSoundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        }
    }

    /**
     * 加载音频资源
     *
     * @param context 上下文
     * @param resId   音频资源 [RawRes]
     */
    fun load(context: Context, @RawRes resId: Int) {
        if (mSoundPool != null) {
            mSoundId = mSoundPool!!.load(context, resId, 1)
        }
    }

    /**
     * 播放声音效果
     */
    fun playSoundEffect() {
        if (mSoundPool != null && mSoundId != 0) {
            mSoundPool!!.play(mSoundId, playVolume, playVolume, 1, 0, 1f)
        }
    }

    /**
     * 释放SoundPool
     */
    fun release() {
        if (mSoundPool != null) {
            mSoundPool!!.release()
            mSoundPool = null
        }
    }

    companion object {

        /**
         * 初始化 SoundHelper
         *
         * @return SoundHelper 对象
         */
        fun obtain(): SoundHelper {
            return SoundHelper()
        }
    }
}