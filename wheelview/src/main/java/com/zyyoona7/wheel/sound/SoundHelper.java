package com.zyyoona7.wheel.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import androidx.annotation.FloatRange;
import androidx.annotation.RawRes;

/**
 * SoundPool 辅助类
 */
public class SoundHelper {

    private SoundPool mSoundPool;
    private int mSoundId;
    private float mPlayVolume;

    private SoundHelper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder().build();
        } else {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 1);
        }
    }

    /**
     * 初始化 SoundHelper
     *
     * @return SoundHelper 对象
     */
    public static SoundHelper obtain() {
        return new SoundHelper();
    }

    /**
     * 加载音频资源
     *
     * @param context 上下文
     * @param resId   音频资源 {@link RawRes}
     */
    public void load(Context context, @RawRes int resId) {
        if (mSoundPool != null) {
            mSoundId = mSoundPool.load(context, resId, 1);
        }
    }

    /**
     * 设置音量
     *
     * @param playVolume 音频播放音量 range 0.0-1.0
     */
    public void setPlayVolume(@FloatRange(from = 0.0, to = 1.0) float playVolume) {
        this.mPlayVolume = playVolume;
    }

    /**
     * 获取音量
     *
     * @return 音频播放音量 range 0.0-1.0
     */
    public float getPlayVolume() {
        return mPlayVolume;
    }

    /**
     * 播放声音效果
     */
    public void playSoundEffect() {
        if (mSoundPool != null && mSoundId != 0) {
            mSoundPool.play(mSoundId, mPlayVolume, mPlayVolume, 1, 0, 1);
        }
    }

    /**
     * 释放SoundPool
     */
    public void release() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
    }
}