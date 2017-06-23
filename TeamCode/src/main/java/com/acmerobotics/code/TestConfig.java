package com.acmerobotics.code;

import com.acmerobotics.library.configuration.OpModeConfiguration;
import com.acmerobotics.library.dashboard.Config;
import com.acmerobotics.library.dashboard.Persist;

/**
 * @author Ryan
 */

@Config("Config")
@Persist("config")
public class TestConfig {
    public static OpModeConfiguration.AllianceColor allianceColor;
    public static OpModeConfiguration.ParkDest parkDest;
    public static int numBalls;
    public static int delay;
    public static OpModeConfiguration.MatchType matchType;
    public static int matchNumber;
    public static double lastHeading;
}
