package com.example.knowledgelabs.ch02

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.knowledgelabs.R

class KotlinToolboxActivity : Activity() {
    private data class ScoreSummary(
        val count: Int,
        val average: Double,
        val highest: Int,
        val passed: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_toolbox)

        val kilometerInput = findViewById<EditText>(R.id.etKilometers)
        val convertResult = findViewById<TextView>(R.id.tvConvertResult)
        findViewById<Button>(R.id.btnConvert).setOnClickListener {
            val kilometers = kilometerInput.text.toString().toDoubleOrNull()
            convertResult.text = kilometers?.let { value ->
                "${format(value)} 公里 = ${format(value * 1000)} 米"
            } ?: "请输入有效数字"
        }

        val scoreInput = findViewById<EditText>(R.id.etScores)
        val scoreResult = findViewById<TextView>(R.id.tvScoreResult)
        findViewById<Button>(R.id.btnAnalyze).setOnClickListener {
            val scores = scoreInput.text.toString()
                .split(',')
                .mapNotNull { item -> item.trim().toIntOrNull() }
                .filter { score -> score in 0..100 }
            val summary = summarize(scores)
            if (summary == null) {
                Toast.makeText(this, "请输入至少一个 0-100 的成绩", Toast.LENGTH_SHORT).show()
            } else {
                scoreResult.text = "人数 ${summary.count} · 平均 ${format(summary.average)}"
                    .plus(" · 最高 ${summary.highest} · 及格 ${summary.passed}")
            }
        }
    }

    private fun summarize(scores: List<Int>): ScoreSummary? = scores.takeIf { it.isNotEmpty() }?.let {
        ScoreSummary(
            count = it.size,
            average = it.average(),
            highest = it.maxOrNull() ?: 0,
            passed = it.count { score -> score >= 60 }
        )
    }

    private fun format(value: Double): String = "%.2f".format(value)
}

