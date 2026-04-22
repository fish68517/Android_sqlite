package com.jetpack.xfyun.speech

import android.content.Context
import com.iflytek.cloud.ErrorCode
import com.iflytek.cloud.InitListener
import com.iflytek.cloud.RecognizerListener
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechError
import com.iflytek.cloud.SpeechRecognizer
import com.iflytek.cloud.RecognizerResult
import android.os.Bundle

class XfYunSpeechRecognizer(
    private val context: Context,
    private val config: XfYunSpeechConfig
) {
    private var recognizer: SpeechRecognizer? = null
    private val parser = XfYunSpeechResultParser()

    fun startListening(listener: XfYunSpeechListener) {
        if (!XfYunSpeechModule.isInitialized()) {
            listener.onError(-1, "讯飞语音模块尚未初始化。")
            return
        }

        recognizer = SpeechRecognizer.createRecognizer(context) { code ->
            if (code != ErrorCode.SUCCESS) {
                listener.onError(code, "讯飞识别器初始化失败。")
            }
        }

        val speechRecognizer = recognizer ?: run {
            listener.onError(-2, "无法创建讯飞识别器。")
            return
        }

        resetParameters(speechRecognizer)
        listener.onReady()

        val startCode = speechRecognizer.startListening(object : RecognizerListener {
            private val resultBuffer = StringBuilder()

            override fun onBeginOfSpeech() = Unit

            override fun onError(error: SpeechError) {
                listener.onError(error.errorCode, error.errorDescription)
            }

            override fun onEndOfSpeech() = Unit

            override fun onResult(result: RecognizerResult, isLast: Boolean) {
                val text = parser.parse(result.resultString)
                if (text.isNotBlank()) {
                    resultBuffer.append(text)
                    listener.onPartialResult(resultBuffer.toString())
                }
                if (isLast) {
                    listener.onFinalResult(resultBuffer.toString())
                }
            }

            override fun onVolumeChanged(volume: Int, data: ByteArray?) {
                listener.onVolumeChanged(volume)
            }

            override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) = Unit
        })

        if (startCode != ErrorCode.SUCCESS) {
            listener.onError(startCode, "启动讯飞语音识别失败。")
        }
    }

    fun stopListening() {
        recognizer?.stopListening()
    }

    fun cancel() {
        recognizer?.cancel()
    }

    fun release() {
        recognizer?.cancel()
        recognizer?.destroy()
        recognizer = null
    }

    private fun resetParameters(recognizer: SpeechRecognizer) {
        recognizer.setParameter(SpeechConstant.PARAMS, null)
        recognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
        recognizer.setParameter(SpeechConstant.DOMAIN, config.domain)
        recognizer.setParameter(SpeechConstant.LANGUAGE, config.language)
        recognizer.setParameter(SpeechConstant.ACCENT, config.accent)
        recognizer.setParameter(SpeechConstant.VAD_BOS, config.vadBos)
        recognizer.setParameter(SpeechConstant.VAD_EOS, config.vadEos)
        recognizer.setParameter(SpeechConstant.ASR_PTT, config.asrPtt)
        recognizer.setParameter(SpeechConstant.RESULT_TYPE, "json")
    }
}
