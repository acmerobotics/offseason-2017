package com.acmerobotics.offseason.dashboard;

import com.acmerobotics.library.dashboard.MultipleTelemetry;
import com.acmerobotics.library.dashboard.RobotDashboard;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.Arrays;

/**
 * @author Ryan
 */

@TeleOp(name = "PID Test", group = "Test")
public class PIDTest extends OpMode {
    private RobotDashboard dashboard;
    private DcMotor motor;
    private PIDController controller;
    private long lastTime;
    private double lastSpeed, maxRpm, ticksPerRev;
    private Looper looper;

    @Override
    public void init() {
        dashboard = RobotDashboard.getInstance();
        dashboard.resetConfigurationForOpMode();
        telemetry = new MultipleTelemetry(Arrays.asList(telemetry, dashboard.getTelemetry()));
        motor = hardwareMap.dcMotor.get("launcherLeft");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        maxRpm = motor.getMotorType().getMaxRPM();
        ticksPerRev = motor.getMotorType().getTicksPerRev();
        controller = new PIDController(Constants.testPid);

        looper = new Looper(40, new Looper.Loop() {
            @Override
            public void onLoop() {
                double speed = getSpeed();
                double power = (60 * speed) / (maxRpm * ticksPerRev);
                motor.setPower(Math.abs(power) > 0.2 ? power : 0);
                telemetry.addData("power", power);
                telemetry.addData("maxRpm", maxRpm);
                telemetry.update();
            }
        });
//        controller.setMaxSum(25);
    }

    public double getSpeed() {
        long time = System.currentTimeMillis();
        double speed = 0;
        if (lastTime != 0) {
            double dt = (time - lastTime) / 1000.0;
            speed = Math.min(Math.abs(lastSpeed) + Constants.maxAccel * dt, Constants.maxSpeed);

            int position = motor.getCurrentPosition();
            int distanceLeft = Constants.setpoint - position;
            double maxSpeed = Math.sqrt(2 * Constants.maxAccel * Math.abs(distanceLeft));

            speed = Math.min(speed, maxSpeed) * Math.signum(distanceLeft);
            speed += controller.update(distanceLeft);

            if (Math.abs(distanceLeft) <= 2) {
                speed = 0;
            }

            telemetry.addData("position", position);
            telemetry.addData("setpoint", Constants.setpoint);
            telemetry.addData("speed", speed);
            telemetry.addData("maxSpeed", maxSpeed);
            telemetry.addData("distanceLeft", distanceLeft);
        }
        lastTime = time;
        lastSpeed = speed;
        return speed;
    }

    @Override
    public void loop() {
        if (!looper.isAlive()) {
            looper.start();
        }
    }

    @Override
    public void internalPostLoop() {
        // do nothing
    }

    @Override
    public void stop() {
        looper.terminate();
    }
}
