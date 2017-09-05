package com.acmerobotics.offseason.localization;

import com.acmerobotics.library.dashboard.RobotDashboard;
import com.acmerobotics.library.dashboard.draw.Canvas;
import com.qualcomm.hardware.adafruit.AdafruitBNO055IMU;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.configuration.MotorConfigurationType;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanbrott on 8/17/17.
 */

@TeleOp(name = "Localization Demo")
public class LocalizationDemo extends OpMode {

    public static final String VUFORIA_LICENSE_KEY = "AaNzdGn/////AAAAGVCiwQaxg01ft7Lw8kYMP3aE00RU5hyTkE1CNeaYi16CBF0EC/LWi50VYsSMdJITYz6jBTmG6UGJNaNXhzk1zVIggfVmGyEZFL5doU6eVaLdgLyVmJx6jLgNzSafXSLnisXnlS+YJlCaOh1pwk08tWM8Oz+Au7drZ4BkO8j1uluIkwiewRu5zDZGlbNliFfYeCRqslBEZCGxiuH/idcsD7Q055Bwj+f++zuG3x4YlIGJCHrTpVjJUWEIbdJzJVgukc/vVOz21UNpY6WoAwH5MSeh4/U6lYwMZTQb4icfk0o1EiBdOPJKHsxyVF9l00r+6Mmdf6NJcFTFLoucvPjngWisD2T/sjbtq9N+hHnKRpbK";
    public static final String TAG = "Localization Demo";
    public static final Pose2d STARTING_POSE = new Pose2d(0, 0, 0);

    public static final float MM_PER_INCH = 25.4f;
    public static final float MM_BOT_WIDTH = 18 * MM_PER_INCH;// the FTC field is ~11'10" center-to-center of the glass panels
    public static final float MM_FTC_FIELD_WIDTH = (12 * 12 - 2) * MM_PER_INCH;
    public static final float MM_TARGET_FLOOR_OFFSET = 1.5f * MM_PER_INCH;
    public static final float MM_TARGET_HEIGHT = 8.5f * MM_PER_INCH;

    public static final double MM_WHEEL_DIAMETER = 4 * MM_PER_INCH;

    private List<VuforiaTrackable> allTrackables;
    private VuforiaLocalizer vuforia;

    private Pose2d vuforiaLocation;
    private Pose2d deadReckoningLocation;

    private RobotDashboard dashboard;

    private BNO055IMU imu;

    private DcMotor leftFront, leftBack, rightFront, rightBack;
    private double lastLeftPosition, lastRightPosition;

    private long lastLoopTime;

    @Override
    public void init() {
        dashboard = RobotDashboard.getInstance();

        imu = new AdafruitBNO055IMU(hardwareMap.i2cDeviceSynch.get("imu"));
        BNO055IMU.Parameters imuParams = new BNO055IMU.Parameters();
        imuParams.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        imu.initialize(imuParams);

        leftFront = hardwareMap.dcMotor.get("leftFront");
        leftBack = hardwareMap.dcMotor.get("leftBack");
        rightFront = hardwareMap.dcMotor.get("rightFront");
        rightBack = hardwareMap.dcMotor.get("rightBack");

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        vuforiaLocation = new Pose2d(STARTING_POSE);
        deadReckoningLocation = new Pose2d(STARTING_POSE);

        setupVuforia();
    }

    private void setupVuforia() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        parameters.vuforiaLicenseKey = VUFORIA_LICENSE_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables velocityVortex = this.vuforia.loadTrackablesFromAsset("velocityVortex");

        VuforiaTrackable legos = velocityVortex.get(0);
        VuforiaTrackable wheels = velocityVortex.get(1);
        VuforiaTrackable tools = velocityVortex.get(2);
        VuforiaTrackable gears = velocityVortex.get(3);

        allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(velocityVortex);

        OpenGLMatrix wheelsLocationOnField = OpenGLMatrix
                .translation(MM_FTC_FIELD_WIDTH / 12, MM_FTC_FIELD_WIDTH / 2, MM_TARGET_FLOOR_OFFSET + MM_TARGET_HEIGHT / 2)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        RobotLog.ii(TAG, "wheels=%s", format(wheelsLocationOnField));

        OpenGLMatrix legosLocationOnField = OpenGLMatrix
                .translation(-MM_FTC_FIELD_WIDTH / 4, MM_FTC_FIELD_WIDTH / 2, MM_TARGET_FLOOR_OFFSET + MM_TARGET_HEIGHT / 2)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0));
        RobotLog.ii(TAG, "legos=%s", format(legosLocationOnField));

        OpenGLMatrix toolsLocationOnField = OpenGLMatrix
                .translation(-MM_FTC_FIELD_WIDTH / 2, MM_FTC_FIELD_WIDTH / 4, MM_TARGET_FLOOR_OFFSET + MM_TARGET_HEIGHT / 2)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZY,
                        AngleUnit.DEGREES, 90, 180, 0));
        RobotLog.ii(TAG, "tools=%s", format(toolsLocationOnField));

        OpenGLMatrix gearsLocationOnField = OpenGLMatrix
                .translation(-MM_FTC_FIELD_WIDTH / 2, -MM_FTC_FIELD_WIDTH / 12, MM_TARGET_FLOOR_OFFSET + MM_TARGET_HEIGHT / 2)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZY,
                        AngleUnit.DEGREES, 90, 180, 0));
        RobotLog.ii(TAG, "gears=%s", format(gearsLocationOnField));

        wheels.setLocation(wheelsLocationOnField);
        legos.setLocation(legosLocationOnField);
        tools.setLocation(toolsLocationOnField);
        gears.setLocation(gearsLocationOnField);

        OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
                .translation(MM_BOT_WIDTH / 2, 0, 0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZY,
                        AngleUnit.DEGREES, -90, 0, 0));
        RobotLog.ii(TAG, "phone=%s", format(phoneLocationOnRobot));

        ((VuforiaTrackableDefaultListener) wheels.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        ((VuforiaTrackableDefaultListener) legos.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        ((VuforiaTrackableDefaultListener) tools.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
        ((VuforiaTrackableDefaultListener) gears.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);

        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();

        velocityVortex.activate();
    }

    @Override
    public void loop() {
        // dead reckoning update
        long currentTime = System.nanoTime();

        double currentLeftPosition = ticksToMm(leftFront.getCurrentPosition() + leftBack.getCurrentPosition());
        double currentRightPosition = ticksToMm(rightFront.getCurrentPosition() + rightBack.getCurrentPosition());

        if (lastLoopTime != 0) {
            double leftDistance = currentLeftPosition - lastLeftPosition;
            double rightDistance = currentRightPosition - lastRightPosition;

            double heading = -imu.getAngularOrientation().firstAngle;

            DifferentialKinematics.updatePose(deadReckoningLocation, leftDistance, rightDistance, heading);
        }

        lastLeftPosition = currentLeftPosition;
        lastRightPosition = currentRightPosition;
        lastLoopTime = currentTime;

        // vuforia update
        for (VuforiaTrackable trackable : allTrackables) {
            telemetry.addData(trackable.getName(), ((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible() ? "Visible" : "Not Visible");    //

            OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
            if (robotLocationTransform != null) {
                vuforiaLocation = Pose2d.fromTransformMatrix(robotLocationTransform);
            }
        }

        // display both position estimates
        Canvas fieldOverlay = dashboard.getFieldOverlay();
        fieldOverlay
                .setFill("blue")
                .fillCircle(
                        Range.scale(vuforiaLocation.x, -MM_FTC_FIELD_WIDTH, MM_FTC_FIELD_WIDTH, 0, 1),
                        Range.scale(vuforiaLocation.y, -MM_FTC_FIELD_WIDTH, MM_FTC_FIELD_WIDTH, 0, 1),
                        5
                )
                .setFill("green")
                .fillCircle(
                        Range.scale(deadReckoningLocation.x, -MM_FTC_FIELD_WIDTH, MM_FTC_FIELD_WIDTH, 0, 1),
                        Range.scale(deadReckoningLocation.y, -MM_FTC_FIELD_WIDTH, MM_FTC_FIELD_WIDTH, 0, 1),
                        5
                );
        dashboard.drawOverlay();

        dashboard.addTelemetry("vuforia", vuforiaLocation.toString());
        dashboard.addTelemetry("dead_reckoning", deadReckoningLocation.toString());
        dashboard.updateTelemetry();

        // simple arcade drive
        double axial = -gamepad1.left_stick_y;
        double lateral = -gamepad1.right_stick_x;

        double sum = Math.abs(axial + lateral);
        if (sum > 1) {
            axial /= sum;
            lateral /= sum;
        }

        double leftSpeed = axial - lateral;
        double rightSpeed = axial + lateral;

        leftFront.setPower(leftSpeed);
        leftBack.setPower(leftSpeed);
        rightFront.setPower(rightSpeed);
        rightBack.setPower(rightSpeed);
    }

    private String format(OpenGLMatrix transformationMatrix) {
        return transformationMatrix.formatAsTransform();
    }

    private double ticksToMm(double ticks) {
        MotorConfigurationType motorType = leftFront.getMotorType();
        double revs = ticks / motorType.getTicksPerRev();
        return revs * Math.PI * MM_WHEEL_DIAMETER;
    }
}
