package com.acmerobotics.relicrecovery.opmodes;

import com.acmerobotics.velocityvortex.opmodes.StickyGamepad;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * @author Ryan
 */

@TeleOp
public class GrabberTest extends OpMode {

    public static final double LEFT_RELEASED = 0.327;
    public static final double RIGHT_RELEASED = 0;
    public static final double LEFT_CONTRACTED = 0.227;
    public static final double RIGHT_CONTRACTED = 0.1;

    private Servo leftGrabber, rightGrabber;

    private StickyGamepad stickyGamepad1;
    private boolean contracted = false;

    @Override
    public void init() {
        stickyGamepad1 = new StickyGamepad(gamepad1);

        leftGrabber = hardwareMap.servo.get("leftGrabber");
        rightGrabber = hardwareMap.servo.get("rightGrabber");
    }

    @Override
    public void loop() {
        stickyGamepad1.update();

        if (stickyGamepad1.a) {
            contracted = !contracted;
        }

        if (contracted) {
            leftGrabber.setPosition(LEFT_CONTRACTED);
            rightGrabber.setPosition(RIGHT_CONTRACTED);
        } else {
            leftGrabber.setPosition(LEFT_RELEASED);
            rightGrabber.setPosition(RIGHT_RELEASED);
        }
    }
}
