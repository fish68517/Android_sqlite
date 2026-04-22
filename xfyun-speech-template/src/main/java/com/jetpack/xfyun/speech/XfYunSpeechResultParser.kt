package com.jetpack.xfyun.speech

import org.json.JSONObject

class XfYunSpeechResultParser {
    fun parse(resultJson: String): String {
        val result = StringBuilder()
        val root = JSONObject(resultJson)
        val words = root.optJSONArray("ws") ?: return ""

        for (i in 0 until words.length()) {
            val wordItem = words.optJSONObject(i) ?: continue
            val candidates = wordItem.optJSONArray("cw") ?: continue
            val firstCandidate = candidates.optJSONObject(0) ?: continue
            result.append(firstCandidate.optString("w"))
        }

        return result.toString()
    }
}
