package com.jetpack.xfyun.speech

import android.content.Context
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility

object XfYunSpeechModule {
    @Volatile
    private var initialized = false

    fun initialize(context: Context, config: XfYunSpeechConfig) {
        if (initialized) return
        SpeechUtility.createUtility(
            context.applicationContext,
            SpeechConstant.APPID + "=" + config.appId
        )
        initialized = true
    }

    fun isInitialized(): Boolean = initialized
}
