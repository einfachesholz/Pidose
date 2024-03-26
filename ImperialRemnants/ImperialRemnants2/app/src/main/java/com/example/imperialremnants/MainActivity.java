package com.example.imperialremnants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.imperialremnants.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private ActivityMainBinding binding;
    private MaterialTimePicker timePicker;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        createNotificationChannel();

        binding.selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Select Medicine Time")
                        .build();

                timePicker.show(getSupportFragmentManager(), "ImperialRemnance");
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (timePicker.getHour() > 12){
                            binding.selectTime.setText(
                                    String.format("%02d",(timePicker.getHour()-12)) +":"+ String.format("%02d", timePicker.getMinute())+"PM"
                            );
                        } else  {
                            binding.selectTime.setText(timePicker.getHour()+":" + timePicker.getMinute()+ "AM");
                        }

                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                        calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                    }
                });
            }
        });

        binding.setReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calendar == null) {
                    Toast.makeText(MainActivity.this, "Please select a time first", Toast.LENGTH_SHORT).show();
                    return;
                }

                String selectedTime = binding.selectTime.getText().toString(); // Example: "12:00 PM"

                // Call the method to send selected time to Raspberry Pi
                NetworkService.sendTimeToRaspberryPi(selectedTime);

                // Create an intent for the AlarmReceiver
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                // Add reminder message if needed
                intent.putExtra("reminder_message", "Take your medicine now!");

                // Create a PendingIntent for the alarm
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                // Get the AlarmManager
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                try {
                    // Schedule the alarm to trigger at the selected time
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    Toast.makeText(MainActivity.this, "Reminder Set", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed to set reminder", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.cancelReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent for the AlarmReceiver
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);

                // Create a PendingIntent for the alarm
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                // Get the AlarmManager
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if (alarmManager != null) {
                    // Cancel the alarm
                    alarmManager.cancel(pendingIntent);
                    Toast.makeText(MainActivity.this, "Reminder Canceled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to cancel reminder", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Request notification policy access when the activity is created

    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Imperial Remnance";
            String desc = "Take Your Medicine!";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ImperialRemnance", name, imp);
            channel.setDescription(desc);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
