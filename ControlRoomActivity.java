package com.mardukh.system;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class ControlRoomActivity extends AppCompatActivity {

    private TextView tvLiveEngineStatus, tvTelemetryData;
    private Handler handler = new Handler();
    private boolean isRunning = false;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_room);

        tvLiveEngineStatus = findViewById(R.id.tvLiveEngineStatus);
        tvTelemetryData = findViewById(R.id.tvTelemetryData);
        Button btnStart = findViewById(R.id.btnStartEngine);
        Button btnStop = findViewById(R.id.btnStopEngine);
        Button btnReport = findViewById(R.id.btnViewTelemetry);

        btnStart.setOnClickListener(v -> {
            isRunning = true;
            tvLiveEngineStatus.setText("ENGAGED // ACTIVE");
            tvLiveEngineStatus.setTextColor(Color.GREEN);
            Toast.makeText(this, "Biological Rejection Layer activated.", Toast.LENGTH_SHORT).show();
            startTelemetry();
        });

        btnStop.setOnClickListener(v -> {
            isRunning = false;
            tvLiveEngineStatus.setText("HALTED // DISABLED");
            tvLiveEngineStatus.setTextColor(Color.RED);
            Toast.makeText(this, "Ingress filter benches powered down.", Toast.LENGTH_LONG).show();
            tvTelemetryData.setText("Telemetry stopped.");
        });

        btnReport.setOnClickListener(v -> {
            Intent intent = new Intent(ControlRoomActivity.this, TelemetryDashboardActivity.class);
            startActivity(intent);
        });
    }

    private void startTelemetry() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!isRunning) return;
                int total = random.nextInt(8000) + 2000;      // 2000-10000 bytes
                int rejected = total - random.nextInt(total / 20); // ~95-100% rejection
                double rejectionRate = (rejected * 100.0) / total;
                double energySaved = total * 0.0001;
                String data = String.format("Total: %d bytes | Rejected: %d (%.1f%%) | Energy: %.4f kWh",
                        total, rejected, rejectionRate, energySaved);
                tvTelemetryData.setText(data);
                handler.postDelayed(this, 4000);
            }
        });
    }
}
