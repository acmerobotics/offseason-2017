package com.acmerobotics.offseason.dashboard;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.acmerobotics.library.dashboard.MultipleTelemetry;
import com.acmerobotics.library.dashboard.RobotDashboard;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.Arrays;

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
        telemetry = new MultipleTelemetry(Arrays.asList(telemetry, dashboard.getTelemetry()));
        SensorManager manager = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        lastReading = new float[3];

        telemetry.log().add("Spin the phone around!");
    }

    @Override
    public void loop() {
        telemetry.addData("first", lastReading[0]);
        telemetry.addData("second", lastReading[1]);
        telemetry.addData("third", lastReading[2]);
        telemetry.addLine()
            .addData("test", "\u263A\u263A\u263A")
            .addData("test2", "hello!");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        lastReading = sensorEvent.values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
