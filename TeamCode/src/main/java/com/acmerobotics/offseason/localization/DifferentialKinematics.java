package com.acmerobotics.offseason.localization;

/**
 * Forward kinematics for a differential drive robot
 *
 * Raw equations: (TODO: check Ryan's napkin calculations)
 * d_l = distance traveled by left side
 * d_r = distance traveled by right side
 * R = (d_l + d_r) / (2 * dtheta)
 * x' = x + R * (1 - cos(dtheta))
 * y' = y + R * sin(dtheta)
 * theta' = theta + dtheta
 */
public class DifferentialKinematics {
    public static void updatePose(Pose2d pose, double leftDistance, double rightDistance, double currentHeading) {
        double headingChange = Math.toRadians(wrapHeading(currentHeading - pose.heading));

        double avgDistance = (leftDistance + rightDistance) / 2;
        if (headingChange == 0) {
            pose.x += avgDistance * Math.sin(headingChange);
            pose.y += avgDistance * Math.cos(headingChange);
        } else {
            double radiusOfCurvature = avgDistance / headingChange;

            pose.x += radiusOfCurvature * (1 - Math.cos(headingChange));
            pose.y += radiusOfCurvature * Math.sin(headingChange);
        }

        pose.heading = currentHeading;
    }

    public static double wrapHeading(double rawHeading) {
        double heading = rawHeading;
        while (Math.abs(heading) > 180) {
            heading -= 360 * Math.signum(heading);
        }
        return heading;
    }
}
