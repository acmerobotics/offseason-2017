package com.acmerobotics.offseason.localization;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

/**
 * Created by ryanbrott on 8/18/17.
 */
public class Pose2d {
    public static final double EPSILON = 0.00001;

    public double x, y, heading;

    public Pose2d(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    public Pose2d(Pose2d other) {
        new Pose2d(other.x, other.y, other.heading);
    }

    public static Pose2d fromTransformMatrix(OpenGLMatrix matrix) {
        // Create a translation and rotation vector
        VectorF trans = matrix.getTranslation();
        Orientation rot = Orientation.getOrientation(matrix, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

        // position is defined by the standard Matrix translation (x and y)
        float x = trans.get(0);
        float y = trans.get(1);

        // heading (in +vc CCW cartesian system) is defined by the standard Matrix z rotation
        float heading = rot.thirdAngle;

        return new Pose2d(x, y, heading);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pose2d) {
            Pose2d other = (Pose2d) o;

            return (Math.abs(x - other.x) < EPSILON)
                    && (Math.abs(y - other.y) < EPSILON)
                    && (Math.abs(heading - other.heading) < EPSILON);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("(x=%f,y=%f,heading=%f)", x, y, heading);
    }
}
