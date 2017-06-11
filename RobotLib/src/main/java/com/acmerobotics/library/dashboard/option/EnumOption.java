package com.acmerobotics.library.dashboard.option;

import android.content.SharedPreferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Arrays;
import java.util.List;

/**
 * @author Ryan
 */

public class EnumOption extends Option<Enum<?>> {
    private List<? extends Enum<?>> enumValues;

    public EnumOption(String key, Class<? extends Enum<?>> enumClass, SharedPreferences prefs) {
        super(key, prefs);
        this.enumValues = Arrays.asList(enumClass.getEnumConstants());
        this.value = enumValues.get(prefs.getInt(key, 0));
    }

    @Override
    public void setValue(Enum<?> value) {
        if (!this.value.equals(value)) {
            editor.putInt(key, enumValues.indexOf(value));
            editor.commit();
        }
        super.setValue(value);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject obj = new JsonObject();
        obj.add("key", new JsonPrimitive(key));
        obj.add("type", new JsonPrimitive("enum"));
        obj.add("value", new JsonPrimitive(enumValues.indexOf(value)));
        JsonArray vals = new JsonArray();
        for (Enum<?> val : enumValues) {
            vals.add(val.toString());
        }
        obj.add("values", vals);
        return obj;
    }

    @Override
    public void updateFromJson(JsonObject obj) {
        setValue(enumValues.get(obj.get("value").getAsInt()));
    }
}
