package com.acmerobotics.library.dashboard.draw;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanbrott on 8/4/17.
 */

public class Canvas {
    private List<CanvasOp> ops;

    public Canvas() {
        ops = new ArrayList<>();
    }

    public void strokeCircle(double x, double y, double radius) {
        ops.add(new Circle(x, y, radius, true));
    }

    public void fillCircle(double x, double y, double radius) {
        ops.add(new Circle(x, y, radius, false));
    }

    public void strokePolygon(double[] xPoints, double[] yPoints) {
        ops.add(new Polygon(xPoints, yPoints, true));
    }

    public void fillPolygon(double[] xPoints, double[] yPoints) {
        ops.add(new Polygon(xPoints, yPoints, false));
    }

    public void strokePolyline(double[] xPoints, double[] yPoints) {
        ops.add(new Polyline(xPoints, yPoints));
    }

    public void strokeLine(double x1, double y1, double x2, double y2) {
        strokePolyline(new double[] { x1, x2 }, new double[] { y1, y2 });
    }

    public void fillRect(double x, double y, double width, double height) {
        fillPolygon(new double[] { x, x + width, x + width, x }, new double[] { y, y, y + height, y + height });
    }

    public void strokeRect(double x, double y, double width, double height) {
        strokePolygon(new double[] { x, x + width, x + width, x }, new double[] { y, y, y + height, y + height });
    }

    public void setFill(String color) {
        ops.add(new Fill(color));
    }

    public void setStroke(String color) {
        ops.add(new Stroke(color));
    }

    public void setStrokeWidth(int width) {
        ops.add(new StrokeWidth(width));
    }

    public List<CanvasOp> getOperations() {
        return ops;
    }

    public void clear() {
        this.ops.clear();
    }
}
