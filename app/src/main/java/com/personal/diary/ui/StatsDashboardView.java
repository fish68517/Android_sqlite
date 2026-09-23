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

public class StatsDashboardView extends View {
    private final Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint moodPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String[] barLabels = new String[]{"日记", "瞬间", "树洞", "收藏"};
    private int[] barValues = new int[]{0, 0, 0, 0};
    private String[] trendLabels = new String[]{"D1", "D2", "D3", "D4", "D5", "D6", "D7"};
    private int[] trendValues = new int[]{0, 0, 0, 0, 0, 0, 0};
    private int[] moodValues = new int[]{3, 3, 3, 3, 3, 3, 3};

    public StatsDashboardView(Context context) {
        super(context);
        init();
    }

    public StatsDashboardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        titlePaint.setColor(getResources().getColor(R.color.diary_text, null));
        titlePaint.setTextSize(dp(15));
        titlePaint.setFakeBoldText(true);

        textPaint.setColor(getResources().getColor(R.color.diary_text_soft, null));
        textPaint.setTextSize(dp(11));
        textPaint.setTextAlign(Paint.Align.CENTER);

        axisPaint.setColor(getResources().getColor(R.color.diary_bg_deep, null));
        axisPaint.setStrokeWidth(dp(1));

        barPaint.setColor(getResources().getColor(R.color.diary_primary, null));

        linePaint.setColor(Color.rgb(77, 118, 166));
        linePaint.setStrokeWidth(dp(3));
        linePaint.setStyle(Paint.Style.STROKE);

        moodPaint.setColor(Color.rgb(210, 116, 55));
        moodPaint.setStrokeWidth(dp(3));
        moodPaint.setStyle(Paint.Style.STROKE);

        fillPaint.setColor(Color.argb(36, 77, 118, 166));
        fillPaint.setStyle(Paint.Style.FILL);

        pointPaint.setStyle(Paint.Style.FILL);
    }

    public void setData(String[] barLabels, int[] barValues, String[] trendLabels, int[] trendValues, int[] moodValues) {
        this.barLabels = barLabels;
        this.barValues = barValues;
        this.trendLabels = trendLabels;
        this.trendValues = trendValues;
        this.moodValues = moodValues;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBars(canvas);
        drawTrend(canvas);
        drawLegend(canvas);
    }

    private void drawBars(Canvas canvas) {
        int left = dp(18);
        int top = dp(18);
        int right = getWidth() - dp(18);
        int bottom = dp(185);
        int labelBottom = bottom + dp(28);
        int max = max(barValues, 1);

        canvas.drawText("内容数量统计", left, top, titlePaint);
        canvas.drawLine(left, bottom, right, bottom, axisPaint);

        float slot = (right - left) / (float) barValues.length;
        float barWidth = slot * 0.44f;
        for (int i = 0; i < barValues.length; i++) {
            float centerX = left + slot * i + slot / 2f;
            float available = bottom - top - dp(32);
            float barHeight = available * (barValues[i] / (float) max);
            float barTop = bottom - barHeight;
            RectF rect = new RectF(centerX - barWidth / 2f, barTop, centerX + barWidth / 2f, bottom);
            canvas.drawRoundRect(rect, dp(9), dp(9), barPaint);
            canvas.drawText(String.valueOf(barValues[i]), centerX, Math.max(top + dp(24), barTop - dp(6)), textPaint);
            canvas.drawText(barLabels[i], centerX, labelBottom, textPaint);
        }
    }

    private void drawTrend(Canvas canvas) {
        int left = dp(26);
        int top = dp(252);
        int right = getWidth() - dp(18);
        int bottom = getHeight() - dp(48);
        int maxTrend = max(trendValues, 1);

        titlePaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("最近 7 天记录趋势 / 心情曲线", dp(18), top - dp(18), titlePaint);
        canvas.drawLine(left, bottom, right, bottom, axisPaint);
        canvas.drawLine(left, top, left, bottom, axisPaint);

        drawLineSeries(canvas, trendValues, maxTrend, left, top, right, bottom, linePaint, true);
        drawMoodCurve(canvas, left, top, right, bottom);

        float slot = (right - left) / (float) Math.max(1, trendValues.length - 1);
        for (int i = 0; i < trendLabels.length; i++) {
            float x = left + slot * i;
            canvas.drawText(trendLabels[i], x, bottom + dp(20), textPaint);
        }
    }

    private void drawLineSeries(Canvas canvas, int[] values, int max, int left, int top, int right, int bottom, Paint paint, boolean fill) {
        if (values.length == 0) {
            return;
        }
        float slot = (right - left) / (float) Math.max(1, values.length - 1);
        Path path = new Path();
        Path fillPath = new Path();
        for (int i = 0; i < values.length; i++) {
            float x = left + slot * i;
            float y = bottom - (bottom - top) * (values[i] / (float) max);
            if (i == 0) {
                path.moveTo(x, y);
                fillPath.moveTo(x, bottom);
                fillPath.lineTo(x, y);
            } else {
                path.lineTo(x, y);
                fillPath.lineTo(x, y);
            }
            pointPaint.setColor(paint.getColor());
            canvas.drawCircle(x, y, dp(4), pointPaint);
        }
        if (fill) {
            fillPath.lineTo(right, bottom);
            fillPath.close();
            canvas.drawPath(fillPath, fillPaint);
        }
        canvas.drawPath(path, paint);
    }

    private void drawMoodCurve(Canvas canvas, int left, int top, int right, int bottom) {
        if (moodValues.length == 0) {
            return;
        }
        float slot = (right - left) / (float) Math.max(1, moodValues.length - 1);
        Path path = new Path();
        for (int i = 0; i < moodValues.length; i++) {
            float x = left + slot * i;
            float y = bottom - (bottom - top) * ((moodValues[i] - 1) / 4f);
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                float prevX = left + slot * (i - 1);
                float prevY = bottom - (bottom - top) * ((moodValues[i - 1] - 1) / 4f);
                float midX = (prevX + x) / 2f;
                path.cubicTo(midX, prevY, midX, y, x, y);
            }
            pointPaint.setColor(moodPaint.getColor());
            canvas.drawCircle(x, y, dp(4), pointPaint);
        }
        canvas.drawPath(path, moodPaint);
    }

    private void drawLegend(Canvas canvas) {
        int y = getHeight() - dp(14);
        pointPaint.setColor(linePaint.getColor());
        canvas.drawCircle(dp(36), y - dp(4), dp(4), pointPaint);
        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("记录数折线", dp(46), y, textPaint);
        pointPaint.setColor(moodPaint.getColor());
        canvas.drawCircle(dp(138), y - dp(4), dp(4), pointPaint);
        canvas.drawText("心情指数曲线", dp(148), y, textPaint);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private int max(int[] values, int fallback) {
        int max = fallback;
        for (int value : values) {
            max = Math.max(max, value);
        }
        return max;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
