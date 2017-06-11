package com.acmerobotics.library.dashboard.option;

import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author Ryan
 */

public class IntegerOption extends Option<Integer> {
    public IntegerOption(String key, SharedPreferences prefs) {
        super(key, prefs);
        this.value = prefs.getInt(key, 0);
    }

    @Override
    public void setValue(Integer value) {
        if (this.value != value) {
            editor.putInt(key, value);
            editor.commit();
        }
        super.setValue(value);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject obj = new JsonObject();
        obj.add("key", new JsonPrimitive(key));
        obj.add("type", new JsonPrimitive("int"));
        obj.add("value", new JsonPrimitive(value));
        return obj;
    }

    @Override
    public void updateFromJson(JsonObject obj) {
        setValue(obj.get("value").getAsInt());
    }
}
