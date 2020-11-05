package com.example.schedulerjob;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int JOB_ID = 0;
    private JobScheduler scheduler;
    private int selectedNetwork;

    private RadioGroup radioGroup;
    private SwitchCompat idle, charging;

    private TextView progress;
    private SeekBar seekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = findViewById(R.id.network_options);
        idle = findViewById(R.id.idle);
        charging = findViewById(R.id.charging);
        progress = findViewById(R.id.progress);
        seekBar = findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 0)
                    MainActivity.this.progress.setText(progress + "s");
                else
                    MainActivity.this.progress.setText("Not Set");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void scheduleTask(View view) {
        scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        selectedNetwork = radioGroup.getCheckedRadioButtonId();
        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;

        switch (selectedNetwork) {
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifiNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }

        int progress = MainActivity.this.seekBar.getProgress();

        Boolean state = selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE
                || idle.isChecked() || charging.isChecked() || progress > 0;

        ComponentName serviceName = new ComponentName(getApplication(), JobNotification.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName);

        builder.setRequiredNetworkType(selectedNetworkOption)
                .setRequiresCharging(charging.isChecked())
                .setRequiresDeviceIdle(idle.isChecked());
        if (seekBar != null)
            builder.setOverrideDeadline(progress * 1000);

        if (state) {
            JobInfo info = builder.build();
            scheduler.schedule(info);
            Toast.makeText(this, "Job is scheduled. It will run when the constraints are et.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Select at least one contrainst to work.", Toast.LENGTH_SHORT).show();
        }


    }

    public void cancelTask(View view) {
        if (scheduler != null) {
//            scheduler.cancel(JOB_ID);
            scheduler.cancelAll();
            scheduler = null;
            Toast.makeText(this, "Scheduled task cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}