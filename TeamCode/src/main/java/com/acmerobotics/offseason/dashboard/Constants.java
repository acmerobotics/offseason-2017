package com.acmerobotics.offseason.dashboard;

import com.acmerobotics.library.dashboard.Config;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

/**
 * @author Ryan
 */

@Config("Constants")
public class Constants {
    public static double maxSpeed = 2500;
    public static double maxAccel = 5000;
    public static int setpoint = 0;
    public static PIDCoefficients testPid = new PIDCoefficients(0, 0, 0);
}
