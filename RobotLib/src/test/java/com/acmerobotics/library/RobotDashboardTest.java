package com.acmerobotics.library;

import com.acmerobotics.library.dashboard.Config;
import com.acmerobotics.library.dashboard.Constants;
import com.acmerobotics.library.dashboard.RobotDashboard;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.robotcore.external.Const;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Ryan
 */

public class RobotDashboardTest {

    @Test
    public void testSerialization() throws IllegalAccessException, NoSuchFieldException {
        System.out.println(RobotDashboard.getConfigJson(Config.class).toString());
        JsonArray arr = RobotDashboard.getConfigJson(Constants.class);
        System.out.println(arr.toString());
        arr.get(0).getAsJsonObject().add("value", new JsonPrimitive(150));
        RobotDashboard.updateClassWithJson(Constants.class, arr);
        System.out.println(Constants.p);
        System.out.println(RobotDashboard.getConfigJson(Arrays.asList(Config.class, Constants.class)).toString());
    }

}
