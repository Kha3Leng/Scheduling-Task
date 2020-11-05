package com.example.schedulerjob;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class JobNotification extends JobService {

    private String ACTION = "notification_task_schedule";
    private static int NOTI_ID = 0;

    private static NotificationManager manager;
    private NotificationChannel channel;

    @Override
    public boolean onStartJob(JobParameters params) {
        createChannel();
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTI_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ACTION)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("Job Schedule")
                .setContentText("This is the job description for jobs")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pendingIntent);

        manager.notify(NOTI_ID, builder.build());
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void createChannel(){
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(ACTION, "Job Scheduling Task", NotificationManager.IMPORTANCE_HIGH);

            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setDescription("Notification from the job");
            manager.createNotificationChannel(channel);
        }

    }
    
    public static void cancel(){
        if (manager != null){
            manager.cancel(NOTI_ID);
        }
    }
}
