package com.acmerobotics.code;

import com.acmerobotics.library.dashboard.Config;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

/**
 * @author Ryan
 */

@Config("Constants")
public class Constants {
    public static int testNum = 3;
    public static PIDCoefficients drivePid = new PIDCoefficients(0.22, 0.1, 0);
}
