package com.acmerobotics.library.dashboard.draw;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ryanbrott on 8/4/17.
 */

public abstract class CanvasInstruction {
    public enum Type {
        @SerializedName("circle")
        CIRCLE,

        @SerializedName("polygon")
        POLYGON,

        @SerializedName("polyline")
        POLYLINE,

        @SerializedName("stroke")
        STROKE,

        @SerializedName("fill")
        FILL,

        @SerializedName("strokeWidth")
        STROKE_WIDTH;
    }

    private Type type;

    public CanvasInstruction(Type type) {
        this.type = type;
    }
}
