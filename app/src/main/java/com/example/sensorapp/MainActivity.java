package com.example.sensorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// Main activity implementing SensorEventListener to receive sensor updates
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // Sensor manager and sensors
    private SensorManager sensorManager;
    private Sensor accelerometer, lightSensor, proximitySensor;

    // UI elements to display sensor data
    private TextView accelText, lightText, proximityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        accelText = findViewById(R.id.accelData);
        lightText = findViewById(R.id.lightData);
        proximityText = findViewById(R.id.proximityData);

        // Initialize Sensor Manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Get default sensors
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Debug log for proximity sensor
        Log.d("SensorApp", "Proximity sensor: " +
                (proximitySensor != null ? proximitySensor.getName() : "NULL"));

        // Check sensor availability
        if (accelerometer == null) accelText.setText(R.string.sensor_not_available);
        if (lightSensor == null) lightText.setText(R.string.sensor_not_available);
        if (proximitySensor == null) proximityText.setText(R.string.sensor_not_available);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register listeners when app is active
        if (accelerometer != null)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (lightSensor != null)
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (proximitySensor != null)
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister listeners to save battery
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // Handle Accelerometer data (X, Y, Z axes)
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            accelText.setText(getString(R.string.accel_format, x, y, z));
        }

        // Handle Light sensor data (intensity)
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float light = event.values[0];
            lightText.setText(getString(R.string.light_format, light));
        }

        // Handle Proximity sensor data (distance)
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];

            Log.d("SensorApp", "Proximity change: " + distance);
            proximityText.setText(getString(R.string.proximity_format, distance));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not required for this application
    }
}