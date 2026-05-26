package com.mardukh.system;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RollingLogCustomView extends View {

    private Paint textPaint;
    private ArrayList<String> logEntriesStack;
    private SimpleDateFormat timeFormat;

    public RollingLogCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLogEngine();
    }

    private void initLogEngine() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.GREEN); // Terminal green visual shell asset look
        textPaint.setTextSize(32f);
        
        logEntriesStack = new ArrayList<>();
        timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        
        logEntriesStack.add("[SYSTEM BOOT] Memory validation loop online.");
    }

    public void appendLog(String message) {
        String timestamp = timeFormat.format(new Date());
        logEntriesStack.add("[" + timestamp + "] " + message);
        
        // Prevent layout screen overflow memory leaks
        if (logEntriesStack.size() > 10) {
            logEntriesStack.remove(0);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK); // Clear layout to terminal canvas black

        float yOffset = 50f;
        for (String logLine : logEntriesStack) {
            canvas.drawText(logLine, 30f, yOffset, textPaint);
            yOffset += 45f; // Incremental spacing index loop calculations
        }
    }
}
