package com.jetpack.xfyun.speech

data class XfYunSpeechConfig(
    val appId: String,
    val language: String = "zh_cn",
    val accent: String = "mandarin",
    val domain: String = "iat",
    val vadBos: String = "4000",
    val vadEos: String = "1000",
    val asrPtt: String = "1"
)
