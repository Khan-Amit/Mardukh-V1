package com.mardukh.system;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TelemetryDashboardActivity extends AppCompatActivity {
    private static final String TARGET_RAW_DATA_ENDPOINT = "https://githubusercontent.com";
    
    private TelemetryCustomView telemetryRenderSurface;
    private RollingLogCustomView rollingLogSurface;
    private TextView tvBinaryCounters;
    private boolean isSyncPipelineActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemetry_dashboard);

        telemetryRenderSurface = findViewById(R.id.telemetryRenderSurface);
        rollingLogSurface = findViewById(R.id.rollingLogSurface);
        tvBinaryCounters = findViewById(R.id.tvBinaryCounters);

        beginRemoteDataHarvestPipeline();
    }

    private void beginRemoteDataHarvestPipeline() {
        new Thread(() -> {
            while (isSyncPipelineActive) {
                try {
                    URL targetUrl = new URL(TARGET_RAW_DATA_ENDPOINT);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(targetUrl.openStream()));
                    StringBuilder packetPayload = new StringBuilder();
                    String lineString;
                    while ((lineString = reader.readLine()) != null) {
                        packetPayload.append(lineString);
                    }
                    reader.close();

                    JSONObject rootNode = new JSONObject(packetPayload.toString());
                    JSONObject metricsNode = rootNode.getJSONObject("metrics");
                    JSONArray blockedIpsArray = rootNode.getJSONArray("blocked_ips");

                    // Process structural calculations inside background parameters
                    float speedPercent = (float) metricsNode.getDouble("flow_speed_percentage");
                    String energy = metricsNode.getString("energy_use_value");
                    String savings = "$" + metricsNode.getString("dollars_saved");
                    
                    String acceptedCount = metricsNode.getString("packets_accepted");
                    String rejectedCount = metricsNode.getString("packets_rejected");

                    List<String> formattedIps = new ArrayList<>();
                    for (int i = 0; i < blockedIpsArray.length(); i++) {
                        formattedIps.add("REJECTED AT MOUTH -> [IP: " + blockedIpsArray.getString(i) + "]");
                    }

                    // Safely dispatch calculated values back to the UI thread
                    runOnUiThread(() -> {
                        telemetryRenderSurface.flowSpeedPercentage = speedPercent;
                        telemetryRenderSurface.energyDraw = energy;
                        telemetryRenderSurface.dollarsSaved = savings;
                        telemetryRenderSurface.invalidate();

                        rollingLogSurface.updateLogs(formattedIps);

                        tvBinaryCounters.setText("ACCEPTED: " + acceptedCount + " | REJECTED: " + rejectedCount);
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> tvBinaryCounters.setText("NETWORK CONNECTION INTERRUPTED // OFFLINE HOOK"));
                }

                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSyncPipelineActive = false; // Gracefully kills network threads when user closes telemetry views
    }
}
