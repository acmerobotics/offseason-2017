package com.acmerobotics.library.dashboard.draw;

/**
 * Created by ryanbrott on 8/4/17.
 */

public class Circle extends CanvasInstruction {
    private double x, y, radius;
    private boolean stroke;

    public Circle(double x, double y, double radius, boolean stroke) {
        super(Type.CIRCLE);

        this.x = x;
        this.y = y;
        this.radius = radius;
        this.stroke = stroke;
    }
}
