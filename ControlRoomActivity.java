package com.mardukh.system;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ControlRoomActivity extends AppCompatActivity {

    private TextView tvLiveEngineStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_room);

        tvLiveEngineStatus = findViewById(R.id.tvLiveEngineStatus);
        Button btnStart = findViewById(R.id.btnStartEngine);
        Button btnStop = findViewById(R.id.btnStopEngine);
        Button btnReport = findViewById(R.id.btnViewTelemetry);

        btnStart.setOnClickListener(v -> {
            tvLiveEngineStatus.setText("ENGAGED // ACTIVE");
            tvLiveEngineStatus.setTextColor(Color.GREEN);
            Toast.makeText(this, "Biological Rejection Layer activated.", Toast.LENGTH_SHORT).show();
        });

        btnStop.setOnClickListener(v -> {
            tvLiveEngineStatus.setText("HALTED // DISABLED");
            tvLiveEngineStatus.setTextColor(Color.RED);
            Toast.makeText(this, "Ingress filter benches powered down.", Toast.LENGTH_WARNING).show();
        });

        // Moves straight onto Page 4: Visual Report & Dynamic Speed Meter Dial
        btnReport.setOnClickListener(v -> {
            Intent intent = new Intent(ControlRoomActivity.this, TelemetryDashboardActivity.class);
            startActivity(intent);
        });
    }
}
