package com.acmerobotics.offseason.dashboard;

import com.acmerobotics.velocityvortex.opmodes.StickyGamepad;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Ryan
 */

@Autonomous(name = "Time Test", group = "Test")
public class TimeTest extends LinearOpMode {
    public static final long TEST_NS = TimeUnit.MILLISECONDS.toNanos(5000);
    public static final long LOOP_MS = TimeUnit.MILLISECONDS.toNanos(2);
    public static final int NUM_READINGS = (int) (TEST_NS / LOOP_MS);

    private StickyGamepad stickyGamepad1;

    private <T extends HardwareDevice> T selectDeviceOfType(Class<? extends T> deviceType) {
        int index = 0;
        List<T> devices = hardwareMap.getAll(deviceType);
        int numDevices = devices.size();
        while (opModeIsActive()) {
            telemetry.addData("", "Please select a %s", deviceType.getSimpleName());
            telemetry.addData("", "\uD83C\uDFAE Use dpad \u21D5 and then press \u24B6");
            stickyGamepad1.update();
            if (stickyGamepad1.dpad_up) {
                index = (index + numDevices - 1) % numDevices;
            }
            if (stickyGamepad1.dpad_down) {
                index = (index + 1) % numDevices;
            }
            if (stickyGamepad1.a) {
                return devices.get(index);
            }
            for (int i = 0; i < numDevices; i++) {
                String deviceName = hardwareMap.getNamesOf(devices.get(i)).iterator().next();
                if (i == index) {
                    telemetry.addData("[>] ", deviceName);
                } else {
                    telemetry.addData("[ ] ", deviceName);
                }
            }
            telemetry.update();
            idle();
        }
        return null;
    }

    private List<Double> getValueChangeDeltas(double[] values, double timeInterval) {
        int start = 0;
        List<Double> deltas = new ArrayList<>();
        for (int i = 1; i < values.length; i++) {
            if (values[i] != values[i - 1]) {
                deltas.add((i - start) * timeInterval);
                start = i;
            }
        }
        deltas.add((values.length - start) * timeInterval);
        return deltas;
    }

    private double getMean(List<Double> values) {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    private double getVariance(List<Double> values, double mean) {
        double variance = 0;
        for (double value : values) {
            variance += Math.pow(value - mean, 2);
        }
        return variance;
    }

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();

        telemetry.setCaptionValueSeparator("");
        stickyGamepad1 = new StickyGamepad(gamepad1);
        DcMotor testMotor = selectDeviceOfType(DcMotor.class);
        if (testMotor == null) {
            return;
        }
        testMotor.setPower(1);

        Thread.sleep(1000);

        long startTime = System.nanoTime();
        double[] readings = new double[NUM_READINGS];
        for (int i = 0; i < readings.length; i++) {
            readings[i] = testMotor.getCurrentPosition();
            long loopEndTime = startTime + (i + 1) * TEST_NS;
            while (System.nanoTime() < loopEndTime) {
                Thread.sleep(0, 100);
            }
        }

        testMotor.setPower(0);

        List<Double> deltas = getValueChangeDeltas(readings, TEST_NS);
        double mean = getMean(deltas);
        double variance = getVariance(deltas, mean);
        double stddev = Math.sqrt(variance);
        telemetry.log().add("Motor test deltas\tmean: %fns\tstddev: %fns", mean, stddev);
        telemetry.update();
    }
}
