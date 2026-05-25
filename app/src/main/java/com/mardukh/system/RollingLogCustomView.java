package com.mardukh.system;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class RollingLogCustomView extends View {
    private final List<String> logCache = new ArrayList<>();
    private float scrollPositionOffset = 0f;
    private final Paint textPaint = new Paint();

    public RollingLogCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#FF3333"));
        textPaint.setTextSize(28f);
        textPaint.fontFamily = android.graphics.Typeface.MONOSPACE;
    }

    public void updateLogs(List<String> incomingIps) {
        synchronized (logCache) {
            logCache.clear();
            logCache.addAll(incomingIps);
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (logCache) {
            if (logCache.isEmpty()) {
                canvas.drawText("AWAITING INBOUND PACKETS...", 20, 50, textPaint);
                return;
            }

            float startY = 40f - scrollPositionOffset;
            for (String blockRecord : logCache) {
                if (startY > 0 && startY < getHeight() + 40) {
                    canvas.drawText(blockRecord, 10, startY, textPaint);
                }
                startY += 45; // Pixel space increment per raw packet string row
            }

            // Loop and advance text viewport coordinates smoothly over time
            scrollPositionOffset += 1.2f;
            if (scrollPositionOffset > (logCache.size() * 45)) {
                scrollPositionOffset = 0f;
            }
        }
        postInvalidateOnAnimation(); // Hooks loop context directly to Android's hardware frame refresh pulse
    }
}
