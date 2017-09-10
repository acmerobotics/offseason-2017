package com.acmerobotics.relicrecovery.opmodes;

import com.acmerobotics.velocityvortex.opmodes.StickyGamepad;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * @author Ryan
 */

@TeleOp
public class GrabberTest extends OpMode {

    public static final double LEFT_OPEN = 0.500;
    public static final double RIGHT_OPEN = 0.350;
    public static final double LEFT_CLOSED = 0.210;
    public static final double RIGHT_CLOSED = 0.860;

    private Servo leftGrabber, rightGrabber;
    private DcMotor motor;

    private StickyGamepad stickyGamepad1;
    private boolean closed = false;

    @Override
    public void init() {
        stickyGamepad1 = new StickyGamepad(gamepad1);

        motor = hardwareMap.dcMotor.get("motor");

        leftGrabber = hardwareMap.servo.get("leftGrabber");
        rightGrabber = hardwareMap.servo.get("rightGrabber");
    }

    @Override
    public void loop() {
        stickyGamepad1.update();

        if (stickyGamepad1.a) {
            closed = !closed;
        }

        if (closed) {
            leftGrabber.setPosition(LEFT_CLOSED);
            rightGrabber.setPosition(RIGHT_CLOSED);
        } else {
            leftGrabber.setPosition(LEFT_OPEN);
            rightGrabber.setPosition(RIGHT_OPEN);
        }

        motor.setPower(-gamepad1.left_stick_y);
    }
}
