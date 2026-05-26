package com.mardukh.system;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Timer;
import java.util.TimerTask;

public class TelemetryDashboardActivity extends AppCompatActivity {

    private String currentAdminPin = "777"; 
    private boolean isAdministrator = false;

    private EditText logicInput;
    private Button authenticateBtn;
    private TextView loginErrorText;
    
    // Layout Pages 
    private LinearLayout layoutPublicReport;
    private LinearLayout layoutAdminDashboard;

    // Custom Canvas Views from your repository
    private TelemetryCustomView dialVoip;
    private TelemetryCustomView dialFiltration;
    private TelemetryCustomView dialEnergyStd;
    private TelemetryCustomView dialEnergyGreen;
    private RollingLogCustomView rollingLogsView;

    private Timer pipelineTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemetry_dashboard);

        // Bind Authorization views
        logicInput = findViewById(R.id.logic_input);
        authenticateBtn = findViewById(R.id.btn_authenticate);
        loginErrorText = findViewById(R.id.login_error_msg);

        // Bind App Pages
        layoutPublicReport = findViewById(R.id.layout_public_report);
        layoutAdminDashboard = findViewById(R.id.layout_admin_dashboard);

        // Bind Custom Drawing Components
        dialVoip = findViewById(R.id.dial_voip);
        dialFiltration = findViewById(R.id.dial_filtration);
        dialEnergyStd = findViewById(R.id.dial_energy_std);
        dialEnergyGreen = findViewById(R.id.dial_energy_green);
        rollingLogsView = findViewById(R.id.rolling_logs_view);

        authenticateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyNativeLogic();
            }
        });

        startTelemetryPollingLoop();
    }

    private void verifyNativeLogic() {
        String input = logicInput.getText().toString().trim();
        if (input.isEmpty()) return;

        if (input.equals(currentAdminPin)) {
            isAdministrator = true;
            loginErrorText.setVisibility(View.GONE);
            
            // UNLOCK PAGE 4 AND DIALS
            layoutPublicReport.setVisibility(View.GONE);
            layoutAdminDashboard.setVisibility(View.VISIBLE);
        } else {
            isAdministrator = false;
            loginErrorText.setVisibility(View.GONE);
            
            // DROP TO PUBLIC GENERAL TIER (PAGE 2)
            layoutAdminDashboard.setVisibility(View.GONE);
            layoutPublicReport.setVisibility(View.VISIBLE);
        }
        logicInput.setText("");
    }

    private void startTelemetryPollingLoop() {
        pipelineTimer = new Timer();
        pipelineTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Generate dynamic values for the dials
                        float mockVoip = (float) (400 + Math.random() * 450);
                        float mockFilt = (float) (Math.random() * 5);

                        // Push updates straight into the custom views
                        dialVoip.updateValue(mockVoip, 1000f);
                        dialFiltration.updateValue(mockFilt, 10f);

                        // Push data to administrative dials if unlocked
                        if (isAdministrator) {
                            dialEnergyStd.updateValue(412f, 500f);
                            dialEnergyGreen.updateValue(89f, 500f);
                            rollingLogsView.appendLog("FILTRATION_DROP: Mitigated request vector.");
                        }
                    }
                });
            }
        }, 0, 1500); // 1500ms pipeline loop execution
    }

    public void changeAdminPinSequence(String newPin) {
        if(newPin != null && !newPin.trim().isEmpty()) {
            this.currentAdminPin = newPin.trim();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pipelineTimer != null) pipelineTimer.cancel();
    }
}
