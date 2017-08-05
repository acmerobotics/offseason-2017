package com.acmerobotics.code;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.acmerobotics.library.dashboard.RobotDashboard;
import com.acmerobotics.library.dashboard.draw.Canvas;
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
    private Canvas overlay;

    @Override
    public void init() {
        Log.i("DashboardThread", Thread.currentThread().getName());
        dashboard = RobotDashboard.getInstance();
        SensorManager manager = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        lastReading = new float[3];
        overlay = dashboard.getFieldOverlay();
    }

    @Override
    public void loop() {
        dashboard.addTelemetry("time", lastTime);
        dashboard.addTelemetry("first", lastReading[0]);
        dashboard.addTelemetry("second", lastReading[1]);
        dashboard.addTelemetry("third", lastReading[2]);
        dashboard.updateTelemetry();

//        overlay.setFill("blue");
//        overlay.setStroke("goldenrod");
//        overlay.setStrokeWidth(3);
//        overlay.fillRect(0.4, 0.4, 0.2, 0.2);
//        overlay.strokeCircle((System.currentTimeMillis() % Constants.maxSpeed) / Constants.maxSpeed, 0.5, 10);
//        overlay.setFill("yellow");
//        overlay.fillRect(0.45, 0.45, 0.1, 0.1);
//        overlay.setStroke("red");
//        overlay.strokeLine(0, 0, 1, 1);

        overlay.setFill("blue");
        overlay.fillCircle(0.5 * lastReading[0] + 0.5, 0.5 * lastReading[1] + 0.5, 5);
        dashboard.drawOverlay();

        try {
            Thread.sleep(5);
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
