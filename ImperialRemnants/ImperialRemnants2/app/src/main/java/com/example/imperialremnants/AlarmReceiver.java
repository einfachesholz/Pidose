package com.example.imperialremnants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract reminder message from intent
        String reminderMessage = intent.getStringExtra("Take Your Medicine");

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ImperialRemnance")
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Medicine Reminder")
                .setContentText(reminderMessage != null ? reminderMessage : "It's time to take your medicine!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(123, builder.build());
    }
}
