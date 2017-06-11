package com.acmerobotics.library.dashboard.option;

import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author Ryan
 */

public class DoubleOption extends Option<Double> {
    public static final double EPSILON = Math.pow(10, -8);

    public DoubleOption(String key, SharedPreferences prefs) {
        super(key, prefs);
        this.value = (double) prefs.getFloat(key, 0);
    }

    @Override
    public void setValue(Double value) {
        if (Math.abs(value - this.value) < EPSILON) {
            editor.putFloat(key, (float) value.doubleValue());
            editor.commit();
        }
        super.setValue(value);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject obj = new JsonObject();
        obj.add("key", new JsonPrimitive(key));
        obj.add("type", new JsonPrimitive("double"));
        obj.add("value", new JsonPrimitive(value));
        return obj;
    }

    @Override
    public void updateFromJson(JsonObject obj) {
        setValue(obj.get("value").getAsDouble());
    }
}
