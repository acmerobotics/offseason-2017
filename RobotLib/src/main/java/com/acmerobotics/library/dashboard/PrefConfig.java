package com.acmerobotics.library.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Path;

import com.acmerobotics.library.configuration.OpModeConfiguration;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ryan
 */

public class PrefConfig {

    public enum Type {
        INT("int", 0),
        DOUBLE("double", 0D),
        BOOLEAN("boolean", false),
        STRING("string", ""),
        ENUM("enum", 0);

        private String typeString;
        private Object defaultValue;
        Type(String type, Object val) {
            typeString = type;
            defaultValue = val;
        }
    }

    private class Option {
        private String name;
        private Type type;
        private Class<? extends Enum<?>> enumClass;

        public Option(String name, Type type) {
            this(name, type, null);
        }

        public Option(String name, Type type, Class<? extends Enum<?>> enumClass) {
            this.name = name;
            this.type = type;
            this.enumClass = enumClass;
        }

        public void updateFromJson(JsonObject obj) {
            JsonElement value = obj.get("value");
            switch (type) {
            case INT:
                editor.putInt(name, value.getAsInt());
                break;
            case DOUBLE:
                editor.putFloat(name, value.getAsFloat());
                break;
            case BOOLEAN:
                editor.putBoolean(name, value.getAsBoolean());
                break;
            case STRING:
                editor.putString(name, value.getAsString());
                break;
            case ENUM:
                editor.putInt(name, value.getAsInt());
                break;
            }
            editor.commit();
        }

        public JsonObject getAsJson() {
            JsonObject obj = new JsonObject();
            obj.add("name", new JsonPrimitive(name));
            obj.add("type", new JsonPrimitive(type.typeString));
            switch (type) {
            case INT:
                obj.add("value", new JsonPrimitive(prefs.getInt(name, (int) type.defaultValue)));
                break;
            case DOUBLE:
                obj.add("value", new JsonPrimitive(prefs.getFloat(name, (float) type.defaultValue)));
                break;
            case BOOLEAN:
                obj.add("value", new JsonPrimitive(prefs.getBoolean(name, (boolean) type.defaultValue)));
                break;
            case STRING:
                obj.add("value", new JsonPrimitive(prefs.getString(name, (String) type.defaultValue)));
                break;
            case ENUM:
                obj.add("value", new JsonPrimitive(prefs.getInt(name, (int) type.defaultValue)));
                JsonArray vals = new JsonArray();
                for (Enum<?> val : enumClass.getEnumConstants()) {
                    vals.add(val.toString());
                }
                obj.add("values", vals);
                break;
            }
            return obj;
        }

        public String getName() {
            return name;
        }

    }

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private List<Option> options;

    public PrefConfig(Context ctx, String name) {
        prefs = ctx.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = prefs.edit();
        options = new ArrayList<>();
    }

    public void addOption(String name, Type type) {
        options.add(new Option(name, type));
    }

    public void addOption(String name, Type type, Class<? extends Enum<?>> enumClass) {
        options.add(new Option(name, type, enumClass));
    }

    public JsonArray getAsJson() {
        JsonArray arr = new JsonArray();
        for (PrefConfig.Option o : options) {
            arr.add(o.getAsJson());
        }
        return arr;
    }

    public void updateFromJson(JsonArray arr) {
        for (int i = 0; i < arr.size(); i++) {
            JsonObject el = arr.get(i).getAsJsonObject();
            options.get(i).updateFromJson(el);
        }
    }

}
