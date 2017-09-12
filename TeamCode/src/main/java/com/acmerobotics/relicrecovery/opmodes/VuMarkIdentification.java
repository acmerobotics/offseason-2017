/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.acmerobotics.relicrecovery.opmodes;

import android.os.Environment;
import android.util.Log;

import com.acmerobotics.library.dashboard.MultipleTelemetry;
import com.acmerobotics.library.dashboard.RobotDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

@Autonomous(name="VuMark Id", group ="Concept")
public class VuMarkIdentification extends LinearOpMode {

    public static final String TAG = "Vuforia VuMark Sample";

    OpenGLMatrix lastLocation = null;
    private Mat raw, rgb;
    private byte[] imgData;
    private CountDownLatch openCvInitialized = new CountDownLatch(1);
    private File imageSaveLocation;
    VuforiaLocalizer vuforia;

    BlockingQueue<VuforiaLocalizer.CloseableFrame> frameQueue;

    @Override public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(Arrays.asList(telemetry, RobotDashboard.getInstance().getTelemetry()));

        imageSaveLocation = new File(Environment.getExternalStorageDirectory(), "ACME");
        imageSaveLocation.mkdirs();

        final BaseLoaderCallback loaderCallback = new BaseLoaderCallback(hardwareMap.appContext) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.i(TAG, "OpenCV loaded successfully");
                        openCvInitialized.countDown();
                    } break;
                    default:
                    {
                        super.onManagerConnected(status);
                    } break;
                }
            }
        };

        AppUtil.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, hardwareMap.appContext, loaderCallback);
            }
        });

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // OR...  Do Not Activate the Camera Monitor View, to save power
        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = "AaNzdGn/////AAAAGVCiwQaxg01ft7Lw8kYMP3aE00RU5hyTkE1CNeaYi16CBF0EC/LWi50VYsSMdJITYz6jBTmG6UGJNaNXhzk1zVIggfVmGyEZFL5doU6eVaLdgLyVmJx6jLgNzSafXSLnisXnlS+YJlCaOh1pwk08tWM8Oz+Au7drZ4BkO8j1uluIkwiewRu5zDZGlbNliFfYeCRqslBEZCGxiuH/idcsD7Q055Bwj+f++zuG3x4YlIGJCHrTpVjJUWEIbdJzJVgukc/vVOz21UNpY6WoAwH5MSeh4/U6lYwMZTQb4icfk0o1EiBdOPJKHsxyVF9l00r+6Mmdf6NJcFTFLoucvPjngWisD2T/sjbtq9N+hHnKRpbK\n";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        parameters.useExtendedTracking = false;
        parameters.fillCameraMonitorViewParent = true;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        vuforia.setFrameQueueCapacity(10);

        frameQueue = vuforia.getFrameQueue();

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicVuMark = relicTrackables.get(0);
        relicVuMark.setName("relicVuMark");

        openCvInitialized.await();

        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();

        relicTrackables.activate();

        while (opModeIsActive()) {
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicVuMark);

            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
                telemetry.addData("VuMark", "%s visible", vuMark);

                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener)relicVuMark.getListener()).getPose();
                telemetry.addData("Pose", format(pose));

                /* We further illustrate how to decompose the pose into useful rotational and
                 * translational components */
                if (pose != null) {
                    VectorF trans = pose.getTranslation();
                    Orientation rot = Orientation.getOrientation(pose, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

                    // Extract the X, Y, and Z components of the offset of the target relative to the robot
                    double tX = trans.get(0);
                    double tY = trans.get(1);
                    double tZ = trans.get(2);

                    // Extract the rotational components of the target relative to the robot
                    double rX = rot.firstAngle;
                    double rY = rot.secondAngle;
                    double rZ = rot.thirdAngle;
                }
            } else {
                telemetry.addData("VuMark", "not visible");
            }

            // Grab frames
            telemetry.addData("num_frames", frameQueue.size());
            if (!frameQueue.isEmpty()) {
                VuforiaLocalizer.CloseableFrame frame = frameQueue.take();
                for (int i = 0; i < frame.getNumImages(); i++) {
                    Image image = frame.getImage(i);
                    if (image.getFormat() == PIXEL_FORMAT.RGB565) {
                        ByteBuffer byteBuffer = image.getPixels();
                        if (imgData == null || imgData.length != byteBuffer.capacity()) {
                            imgData = new byte[byteBuffer.capacity()];
                        }
                        byteBuffer.get(imgData);
                        if (raw == null || raw.width() != image.getWidth() || raw.height() != image.getHeight()) {
                            raw = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC2);
                            rgb = new Mat();
                        }
                        raw.put(0, 0, imgData);
                        Imgproc.cvtColor(raw, rgb, Imgproc.COLOR_BGR5652BGR);

                        // do things
                        MatOfInt params = new MatOfInt();
                        params.fromArray(Imgcodecs.CV_IMWRITE_JPEG_QUALITY, 100);
                        Imgcodecs.imwrite(new File(imageSaveLocation, System.currentTimeMillis() + "_" + i + ".jpg").getPath(), rgb, params);

                        rgb.release();
                        raw.release();
                    }
                }
                frame.close();
            }

            telemetry.update();
        }
    }

    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
}
