package com.acmerobotics.library.dashboard.message;

import com.acmerobotics.library.dashboard.Telemetry;
import com.acmerobotics.library.dashboard.draw.Canvas;
import com.google.gson.JsonArray;

/**
 * @author Ryan
 */

public class UpdateMessageData {
    private Telemetry telemetry;
    private JsonArray config;
    private Canvas fieldOverlay;

    public Telemetry getTelemetry() {
        return telemetry;
    }

    public JsonArray getConfig() {
        return config;
    }

    public Canvas getFieldOverlay() {
        return fieldOverlay;
    }

    public static UpdateMessageDataBuilder builder() {
        return new UpdateMessageDataBuilder();
    }

    public static class UpdateMessageDataBuilder {
        private UpdateMessageData data;

        public UpdateMessageDataBuilder() {
            this.data = new UpdateMessageData();
        }

        public UpdateMessageDataBuilder telemetry(Telemetry telemetry) {
            this.data.telemetry = telemetry;
            return this;
        }

        public UpdateMessageDataBuilder config(JsonArray config) {
            this.data.config = config;
            return this;
        }

        public UpdateMessageDataBuilder fieldOverlay(Canvas fieldOverlay) {
            this.data.fieldOverlay = fieldOverlay;
            return this;
        }

        public UpdateMessageData build() {
            return this.data;
        }
    }
}