package com.acmerobotics.offseason.dashboard;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.acmerobotics.library.dashboard.RobotDashboard;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * @author Ryan
 */

@TeleOp(name = "Rotation Sensor Test", group = "Test")
public class RotationSensorTest extends OpMode implements SensorEventListener {
    private RobotDashboard dashboard;
    private Sensor sensor;

    private float[] lastReading;

    @Override
    public void init() {
        Log.i("DashboardThread", Thread.currentThread().getName());
        dashboard = RobotDashboard.getInstance();
        SensorManager manager = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        lastReading = new float[3];
    }

    @Override
    public void loop() {
        dashboard.addTelemetry("first", lastReading[0]);
        dashboard.addTelemetry("second", lastReading[1]);
        dashboard.addTelemetry("third", lastReading[2]);
        dashboard.updateTelemetry();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        lastReading = sensorEvent.values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
