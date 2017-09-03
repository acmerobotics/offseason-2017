package com.acmerobotics.library.dashboard;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanbrott on 8/4/17.
 */

public class Telemetry {
    public static class Entry {
        private String name;
        private JsonElement value;

        public Entry(String name, JsonElement value) {
            this.name = name;
            this.value = value;
        }
    }

    private long timestamp;
    private List<Entry> entries;

    public Telemetry() {
        entries = new ArrayList<>();
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    public void updateTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }

    public void clear() {
        entries.clear();
    }
}
