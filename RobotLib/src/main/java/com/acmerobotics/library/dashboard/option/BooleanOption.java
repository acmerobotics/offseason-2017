package com.acmerobotics.library.dashboard.option;

import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author Ryan
 */

public class BooleanOption extends Option<Boolean> {
    public BooleanOption(String key, SharedPreferences prefs) {
        super(key, prefs);
        this.value = prefs.getBoolean(key, false);
    }

    @Override
    public void setValue(Boolean value) {
        if (this.value != value) {
            editor.putBoolean(key, value);
            editor.commit();
        }
        super.setValue(value);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject obj = new JsonObject();
        obj.add("key", new JsonPrimitive(key));
        obj.add("type", new JsonPrimitive("boolean"));
        obj.add("value", new JsonPrimitive(value));
        return obj;
    }

    @Override
    public void updateFromJson(JsonObject obj) {
        setValue(obj.get("value").getAsBoolean());
    }
}
