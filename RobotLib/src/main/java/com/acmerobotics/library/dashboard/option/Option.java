package com.acmerobotics.library.dashboard.option;

import android.content.SharedPreferences;

import com.google.gson.JsonObject;

/**
 * @author Ryan
 */

public abstract class Option<T> {

    protected final String key;
    protected T value;
    protected SharedPreferences prefs;
    protected SharedPreferences.Editor editor;

    public Option(String key, SharedPreferences prefs) {
        this.key = key;
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract JsonObject getAsJson();

    public abstract void updateFromJson(JsonObject obj);

}
