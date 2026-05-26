package com.mardukh.system;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Timer;
import java.util.TimerTask;

public class TelemetryDashboardActivity extends AppCompatActivity {

    // Decoupled runtime access keys hidden away from structural XML layouts
    private String currentAdminPin = "777"; 
    private boolean isAdministrator = false;

    private EditText logicInput;
    private Button authenticateBtn;
    private TextView loginErrorText;
    
    // Core Layout View Groups
    private LinearLayout layoutPublicReport;
    private LinearLayout layoutAdminDashboard;

    // Custom Component Anchors
    private TelemetryCustomView dialVoip;
    private TelemetryCustomView dialFiltration;
    private TelemetryCustomView dialEnergyStd;
    private TelemetryCustomView dialEnergyGreen;
    private RollingLogCustomView rollingLogsView;

    private Timer pipelineTimer;
    private Handler mainThreadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telemetry_dashboard);
        
        // Initialize main loop execution UI thread handler
        mainThreadHandler = new Handler(Looper.getMainLooper());

        // Bind Input Components
        logicInput = findViewById(R.id.logic_input);
        authenticateBtn = findViewById(R.id.btn_authenticate);
        loginErrorText = findViewById(R.id.login_error_msg);

        // Bind Layout Layers
        layoutPublicReport = findViewById(R.id.layout_public_report);
        layoutAdminDashboard = findViewById(R.id.layout_admin_dashboard);

        // Bind Custom Speed Dial Views
        dialVoip = findViewById(R.id.dial_voip);
        dialFiltration = findViewById(R.id.dial_filtration);
        dialEnergyStd = findViewById(R.id.dial_energy_std);
        dialEnergyGreen = findViewById(R.id.dial_energy_green);
        rollingLogsView = findViewById(R.id.rolling_logs_view);

        authenticateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Execute verification safely inside the main UI loop queue
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        verifyNativeLogicSequence();
                    }
                });
            }
        });

        startTelemetryPollingLoop();
    }

    private void verifyNativeLogicSequence() {
        String input = logicInput.getText().toString().trim();
        if (input.isEmpty()) return;

        // Force virtual software keyboard down to prevent touch tracking freezes
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) { /* Fallback fail-silent */ }

        if (input.equals(currentAdminPin)) {
            isAdministrator = true;
            loginErrorText.setVisibility(View.GONE);
            
            // FIX: Explicit container transition update with strict redraw invalidations
            layoutPublicReport.setVisibility(View.GONE);
            layoutAdminDashboard.setVisibility(View.VISIBLE);
            
            // Force the window hierarchy tree to instantly redraw the new page elements
            layoutAdminDashboard.requestLayout();
            layoutAdminDashboard.invalidate();
            
        } else {
            isAdministrator = false;
            loginErrorText.setVisibility(View.VISIBLE);
            loginErrorText.setText("Invalid Security Code");
            
            layoutAdminDashboard.setVisibility(View.GONE);
            layoutPublicReport.setVisibility(View.VISIBLE);
            
            layoutPublicReport.requestLayout();
            layoutPublicReport.invalidate();
        }
        
        logicInput.setText("");
    }

    private void startTelemetryPollingLoop() {
        pipelineTimer = new Timer();
        pipelineTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Ensure canvas math bindings update context strictly inside the Android UI Thread
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        float mockVoip = (float) (400 + Math.random() * 450);
                        float mockFilt = (float) (Math.random() * 5);

                        // Broadcast updates directly to active dial modules
                        if (dialVoip != null) dialVoip.updateValue(mockVoip, 1000f);
                        if (dialFiltration != null) dialFiltration.updateValue(mockFilt, 10f);

                        // Broadcast updates to the locked pages if authenticated
                        if (isAdministrator) {
                            if (dialEnergyStd != null) dialEnergyStd.updateValue(412f, 500f);
                            if (dialEnergyGreen != null) dialEnergyGreen.updateValue(89f, 500f);
                            if (rollingLogsView != null) {
                                rollingLogsView.appendLog("FILTRATION_DROP: Packet rejected.");
                            }
                        }
                    }
                });
            }
        }, 0, 1500);
    }

    public void updateAdminPinSequence(String newPin) {
        if(newPin != null && !newPin.trim().isEmpty()) {
            this.currentAdminPin = newPin.trim();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pipelineTimer != null) {
            pipelineTimer.cancel();
        }
    }
}
