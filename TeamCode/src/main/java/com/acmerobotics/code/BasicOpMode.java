package com.acmerobotics.code;

import android.content.Context;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.acmerobotics.library.dashboard.RobotDashboard;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * @author Ryan
 */

@TeleOp(name="Basic")
public class BasicOpMode extends OpMode implements SensorEventListener {
    private RobotDashboard dashboard;
    private Sensor sensor;

    private long lastTime;
    private float[] lastReading;

    @Override
    public void init() {
        dashboard = RobotDashboard.getInstance();
        SensorManager manager = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void loop() {
        dashboard.addTelemetry("time", lastTime);
        dashboard.addTelemetry("first", lastReading[0]);
        dashboard.addTelemetry("second", lastReading[1]);
        dashboard.addTelemetry("third", lastReading[2]);
        dashboard.updateTelemetry();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        lastTime = System.currentTimeMillis();
        lastReading = sensorEvent.values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
