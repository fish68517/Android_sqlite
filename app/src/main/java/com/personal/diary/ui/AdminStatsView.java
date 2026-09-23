package com.personal.diary.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.personal.diary.R;

public class AdminStatsView extends View {
    private final Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint interactionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String contentTitle = "";
    private String trendTitle = "";
    private String interactionTitle = "";
    private String interactionDetailTitle = "";
    private String[] barLabels = new String[0];
    private int[] barValues = new int[0];
    private String[] dayLabels = new String[0];
    private int[] trendValues = new int[0];
    private int[] interactionValues = new int[0];
    private String[] interactionLabels = new String[0];
    private int[] interactionBars = new int[0];

    public AdminStatsView(Context context) {
        super(context);
        init();
    }

    public AdminStatsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        titlePaint.setColor(getResources().getColor(R.color.diary_text, null));
        titlePaint.setTextSize(dp(15));
        titlePaint.setFakeBoldText(true);
        textPaint.setColor(getResources().getColor(R.color.diary_text_soft, null));
        textPaint.setTextSize(dp(10));
        textPaint.setTextAlign(Paint.Align.CENTER);
        axisPaint.setColor(getResources().getColor(R.color.diary_bg_deep, null));
        axisPaint.setStrokeWidth(dp(1));
        barPaint.setColor(getResources().getColor(R.color.diary_primary, null));
        trendPaint.setColor(Color.rgb(70, 116, 170));
        trendPaint.setStrokeWidth(dp(3));
        trendPaint.setStyle(Paint.Style.STROKE);
        interactionPaint.setColor(Color.rgb(210, 103, 49));
        interactionPaint.setStrokeWidth(dp(3));
        interactionPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStyle(Paint.Style.FILL);
    }

    public void setData(String contentTitle, String trendTitle, String interactionTitle, String interactionDetailTitle,
                        String[] barLabels, int[] barValues, String[] dayLabels,
                        int[] trendValues, int[] interactionValues, String[] interactionLabels, int[] interactionBars) {
        this.contentTitle = contentTitle;
        this.trendTitle = trendTitle;
        this.interactionTitle = interactionTitle;
        this.interactionDetailTitle = interactionDetailTitle;
        this.barLabels = barLabels;
        this.barValues = barValues;
        this.dayLabels = dayLabels;
        this.trendValues = trendValues;
        this.interactionValues = interactionValues;
        this.interactionLabels = interactionLabels;
        this.interactionBars = interactionBars;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBars(canvas, dp(18), dp(24), getWidth() - dp(18), dp(205));
        drawLineChart(canvas, trendTitle, trendValues, trendPaint, dp(18), dp(270), getWidth() - dp(18), dp(420), true);
        drawLineChart(canvas, interactionTitle, interactionValues, interactionPaint, dp(18), dp(475), getWidth() - dp(18), dp(600), false);
        drawInteractionBars(canvas, dp(18), dp(670), getWidth() - dp(18), dp(765));
    }

    private void drawBars(Canvas canvas, int left, int top, int right, int bottom) {
        canvas.drawText(contentTitle, left, top, titlePaint);
        int max = max(barValues);
        canvas.drawLine(left, bottom, right, bottom, axisPaint);
        if (barValues.length == 0) {
            return;
        }
        float slot = (right - left) / (float) barValues.length;
        float barWidth = slot * 0.48f;
        for (int i = 0; i < barValues.length; i++) {
            float centerX = left + slot * i + slot / 2f;
            float barHeight = (bottom - top - dp(30)) * (barValues[i] / (float) max);
            float barTop = bottom - barHeight;
            canvas.drawRoundRect(new RectF(centerX - barWidth / 2f, barTop, centerX + barWidth / 2f, bottom), dp(7), dp(7), barPaint);
            canvas.drawText(String.valueOf(barValues[i]), centerX, Math.max(top + dp(24), barTop - dp(5)), textPaint);
            canvas.drawText(barLabels[i], centerX, bottom + dp(20), textPaint);
        }
    }

    private void drawLineChart(Canvas canvas, String title, int[] values, Paint linePaint, int left, int top, int right, int bottom, boolean showDays) {
        canvas.drawText(title, left, top - dp(18), titlePaint);
        canvas.drawLine(left, bottom, right, bottom, axisPaint);
        canvas.drawLine(left, top, left, bottom, axisPaint);
        if (values.length == 0) {
            return;
        }
        int max = max(values);
        float slot = (right - left) / (float) Math.max(1, values.length - 1);
        Path path = new Path();
        for (int i = 0; i < values.length; i++) {
            float x = left + slot * i;
            float y = bottom - (bottom - top) * (values[i] / (float) max);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            pointPaint.setColor(linePaint.getColor());
            canvas.drawCircle(x, y, dp(4), pointPaint);
            canvas.drawText(String.valueOf(values[i]), x, y - dp(8), textPaint);
            if (showDays && i < dayLabels.length) {
                canvas.drawText(dayLabels[i], x, bottom + dp(18), textPaint);
            }
        }
        canvas.drawPath(path, linePaint);
    }

    private void drawInteractionBars(Canvas canvas, int left, int top, int right, int bottom) {
        canvas.drawText(interactionDetailTitle, left, top - dp(20), titlePaint);
        canvas.drawLine(left, bottom, right, bottom, axisPaint);
        if (interactionBars.length == 0) {
            return;
        }
        int max = max(interactionBars);
        float slot = (right - left) / (float) interactionBars.length;
        float barWidth = slot * 0.42f;
        for (int i = 0; i < interactionBars.length; i++) {
            float centerX = left + slot * i + slot / 2f;
            float barHeight = (bottom - top) * (interactionBars[i] / (float) max);
            float barTop = bottom - barHeight;
            canvas.drawRoundRect(new RectF(centerX - barWidth / 2f, barTop, centerX + barWidth / 2f, bottom), dp(7), dp(7), interactionPaint);
            canvas.drawText(String.valueOf(interactionBars[i]), centerX, Math.max(top + dp(10), barTop - dp(5)), textPaint);
            if (i < interactionLabels.length) {
                canvas.drawText(interactionLabels[i], centerX, bottom + dp(18), textPaint);
            }
        }
    }

    private int max(int[] values) {
        int max = 1;
        for (int value : values) {
            max = Math.max(max, value);
        }
        return max;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
