package com.mardukh.system;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TelemetryCustomView extends View {
    public float flowSpeedPercentage = 0f;
    public String energyDraw = "0mA";
    public String dollarsSaved = "$0.00";

    private final Paint paint = new Paint();

    public TelemetryCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2.8f;
        float radius = getHeight() / 4.2f;

        paint.setAntiAlias(true);
        
        // 1. Render Circular Speed Meter Base Path
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20f);
        paint.setColor(Color.parseColor("#22FFFFFF"));
        canvas.drawCircle(cx, cy, radius, paint);

        // 2. Render Live Dynamic Flow Speed Swept Arc Indicator
        paint.setColor(Color.parseColor("#FFD700")); // Deep Gold
        float sweepAngle = (flowSpeedPercentage / 100f) * 360f;
        canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius, -90, sweepAngle, false, paint);

        // 3. Render the Tactical Square Metric Box
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#1A1A1A"));
        float boxTop = cy + radius + 30;
        canvas.drawRect(cx - 180, boxTop, cx + 180, boxTop + 110, paint);

        // Border edge highlight for square box
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.parseColor("#FFD700"));
        canvas.drawRect(cx - 180, boxTop, cx + 180, boxTop + 110, paint);

        // 4. Inject Dynamic Metrics Text directly inside the bounds
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(34f);
        paint.setColor(Color.WHITE);
        canvas.drawText("SAVED: " + dollarsSaved, cx, boxTop + 45, paint);
        
        paint.setTextSize(26f);
        paint.setColor(Color.parseColor("#88FFFFFF"));
        canvas.drawText("ENERGY DRAW: " + energyDraw, cx, boxTop + 90, paint);
    }
}
