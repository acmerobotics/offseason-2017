package com.acmerobotics.library.dashboard.message;

import android.util.Log;

import com.acmerobotics.library.dashboard.RobotDashboard;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * @author Ryan
 */

public class MessageDeserializer implements JsonDeserializer<Message> {
    @Override
    public Message deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject messageObj = jsonElement.getAsJsonObject();
        String messageTypeString = messageObj.get("type").getAsString();
        MessageType messageType = EnumUtil.fromValue(messageTypeString, MessageType.class);
        JsonElement data = messageObj.get("data");
        switch (messageType) {
            case UPDATE:
                return new Message(messageType, jsonDeserializationContext.deserialize(data, UpdateMessageData.class));
            case GET:
                return new Message(messageType, jsonDeserializationContext.deserialize(data, String.class));
            default:
                Log.w(RobotDashboard.TAG, String.format("Unknown message type: %s (%s)", messageType, messageTypeString));
            case PING:
                return new Message(messageType);
        }
    }
}
