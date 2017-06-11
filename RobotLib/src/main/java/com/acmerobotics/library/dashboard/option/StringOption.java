package com.acmerobotics.library.dashboard.option;

import android.content.SharedPreferences;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author Ryan
 */

public class StringOption extends Option<String> {
    public StringOption(String key, SharedPreferences prefs) {
        super(key, prefs);
        this.value = prefs.getString(key, "");
    }

    @Override
    public void setValue(String value) {
        if (!this.value.equals(value)) {
            editor.putString(key, value);
            editor.commit();
        }
        super.setValue(value);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject obj = new JsonObject();
        obj.add("key", new JsonPrimitive(key));
        obj.add("type", new JsonPrimitive("string"));
        obj.add("value", new JsonPrimitive(value));
        return obj;
    }

    @Override
    public void updateFromJson(JsonObject obj) {
        setValue(obj.get("value").getAsString());
    }
}
