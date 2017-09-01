package com.acmerobotics.offseason;

import android.app.Activity;
import android.media.ImageReader;
import android.util.Log;

import com.acmerobotics.library.camera2.Camera2Fragment;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.R;

/**
 * Created by ryanbrott on 8/21/17.
 */

@TeleOp(name = "Camera2 Test", group = "Test")
public class CameraTest extends OpMode {

    private Camera2Fragment fragment;
    private Activity activity;

    @Override
    public void init() {
        this.activity = (Activity) hardwareMap.appContext;
        this.fragment = Camera2Fragment.newInstance(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imageReader) {
                Log.i("CameraTest", "onImageAvailable");
                // pretend to consume the image
                imageReader.acquireLatestImage().close();
            }
        }, R.layout.fragment_camera2);

        this.activity.getFragmentManager().beginTransaction()
                .replace(R.id.cameraMonitorViewId, fragment)
                .commit();
    }

    @Override
    public void loop() {

    }

    @Override
    public void stop() {
        this.activity.getFragmentManager().beginTransaction()
                .remove(fragment)
                .commit();
    }
}
