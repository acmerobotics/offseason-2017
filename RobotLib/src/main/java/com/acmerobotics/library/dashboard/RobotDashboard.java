package com.acmerobotics.library.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.json.JSONArray;

import static com.acmerobotics.library.configuration.OpModeConfiguration.*;

public class RobotDashboard {

	private static RobotDashboard dashboard;

	public static RobotDashboard open(Context ctx) {
		dashboard = new RobotDashboard(ctx);
		return dashboard;
	}

	public static RobotDashboard getInstance() {
		return dashboard;
	}

	private Map<String, String> telemetry;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	private List<RobotWebSocket> sockets;
	private RobotWebSocketServer server;
    private List<OptionGroup> optionGroups;
	private Gson gson;
	
	private RobotDashboard(Context ctx) {
		prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		sockets = new ArrayList<>();
		telemetry = new HashMap<>();
		gson = new Gson();

		optionGroups = new ArrayList<>();
		optionGroups.add(new OptionGroup(Config.class, prefs));
		optionGroups.add(new OptionGroup(Constants.class, prefs));

		server = new RobotWebSocketServer(this);
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public JsonElement getConfigJson() {
	    JsonArray arr = new JsonArray();
	    for (OptionGroup group : optionGroups) {
	        JsonObject obj = new JsonObject();
	        obj.add("name", new JsonPrimitive(group.getName()));
	        obj.add("options", group.getJson());
	        arr.add(obj);
        }
        return arr;
    }

    public void updateConfigWithJson(JsonElement configJson) {
        JsonArray arr = configJson.getAsJsonArray();
	    for (int i = 0; i < arr.size(); i++) {
	    	optionGroups.get(i).updateFromJson(arr.get(i));
        }
    }

    public static JsonObject getMessage(String type, JsonObject data) {
        JsonObject msg = new JsonObject();
        msg.add("type", new JsonPrimitive(type));
        msg.add("data", data);
        return msg;
    }

    public JsonObject getConfigUpdateMessage() {
	    JsonObject data = new JsonObject();
	    data.add("config", getConfigJson());
	    return getMessage("update", data);
    }

	private JsonElement getTelemetryJson() {
		JsonArray arr = new JsonArray();
		for (Entry<String, String> line : telemetry.entrySet()) {
			JsonArray lineArray = new JsonArray();
			lineArray.add(line.getKey());
			lineArray.add(line.getValue());
			arr.add(lineArray);
		}
		return arr;
	}

	private JsonObject getTelemetryUpdateMessage() {
		JsonObject data = new JsonObject();
		data.add("telemetry", getTelemetryJson());
		return getMessage("update", data);
	}

	public void addTelemetry(String key, String value) {
		telemetry.put(key, value);
	}

	public void addTelemetry(String key, Object value) {
		addTelemetry(key, value.toString());
	}

	public void addTelemetry(String key, String value, Object...args) {
		addTelemetry(key, String.format(value, args));
	}

	public void updateTelemetry() {
	    sendAll(getTelemetryUpdateMessage().toString());
    }

	public synchronized void sendAll(String msg) {
		for (RobotWebSocket ws : sockets) {
			send(ws, msg);
		}
	}

	public synchronized void send(RobotWebSocket ws, String msg) {
		try {
			ws.send(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void addSocket(RobotWebSocket socket) {
		sockets.add(socket);
		send(socket, getConfigUpdateMessage().toString());
	}

	public synchronized void removeSocket(RobotWebSocket socket) {
		sockets.remove(socket);
	}

	public synchronized void onMessage(RobotWebSocket socket, JsonObject msg) {
		System.out.println(msg.toString());
		String type = msg.get("type").getAsString();
		if (type.equals("get")) {
			String data = msg.get("data").getAsString();
			if (data.equals("config")) {
				send(socket, getConfigUpdateMessage().toString());
			}
		} else if (type.equals("update")) {
			JsonObject data = msg.get("data").getAsJsonObject();
			if (data.has("config")) {
			    updateConfigWithJson(data.get("config"));
			}
		} else {
			Log.i("Dashboard", String.format("unknown message recv'd: '%s'", type));
			Log.i("Dashboard", msg.toString());
		}
	}

	public void stop() {
		server.stop();
		dashboard = null;
	}
	
}
