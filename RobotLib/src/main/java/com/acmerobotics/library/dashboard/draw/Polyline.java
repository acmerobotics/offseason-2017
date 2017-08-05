package com.acmerobotics.library.dashboard.draw;

/**
 * Created by ryanbrott on 8/4/17.
 */

public class Polyline extends CanvasInstruction {
    private double[] xPoints, yPoints;

    public Polyline(double[] xPoints, double[] yPoints) {
        super(Type.POLYLINE);

        this.xPoints = xPoints;
        this.yPoints = yPoints;
    }
}
