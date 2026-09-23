package com.jetpack.xfyun.speech

interface XfYunSpeechListener {
    fun onReady()
    fun onPartialResult(text: String)
    fun onFinalResult(text: String)
    fun onError(code: Int, message: String)
    fun onVolumeChanged(volume: Int)
}
