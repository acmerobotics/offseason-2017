package com.acmerobotics.library.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.util.Log;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import com.acmerobotics.library.dashboard.option.DoubleOption;
import com.acmerobotics.library.dashboard.option.EnumOption;
import com.acmerobotics.library.dashboard.option.IntegerOption;
import com.acmerobotics.library.dashboard.option.Option;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import static com.acmerobotics.library.configuration.OpModeConfiguration.*;

public class RobotDashboard {
	
	private Map<String, String> telemetry;
	private SharedPreferences prefs;
	private List<Option<?>> config;
	private List<RobotWebSocket> sockets;
	private RobotWebSocketServer server;
	
	public RobotDashboard(Context ctx) {
		prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		sockets = new ArrayList<>();
		telemetry = new HashMap<>();

		config = new ArrayList<>();
		config.add(new EnumOption(PREF_ALLIANCE_COLOR, AllianceColor.class, prefs));
		config.add(new EnumOption(PREF_PARK_DEST, ParkDest.class, prefs));
		config.add(new IntegerOption(PREF_DELAY, prefs));
		config.add(new IntegerOption(PREF_NUM_BALLS, prefs));
		config.add(new EnumOption(PREF_MATCH_TYPE, MatchType.class, prefs));
		config.add(new IntegerOption(PREF_MATCH_NUMBER, prefs));
		config.add(new DoubleOption(PREF_LAST_HEADING, prefs));

		server = new RobotWebSocketServer(this);
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private JsonArray getTelemetryAsJson() {
		JsonArray arr = new JsonArray();
		for (Entry<String, String> line : telemetry.entrySet()) {
			JsonArray lineArray = new JsonArray();
			lineArray.add(line.getKey());
			lineArray.add(line.getValue());
			arr.add(lineArray);
		}
		return arr;
	}

	private JsonArray getConfigAsJson() {
		JsonArray arr = new JsonArray();
		for (Option o : config) {
			arr.add(o.getAsJson());
		}
		return arr;
	}
	
	public void updateTelemetry() {
		JsonArray arr = getTelemetryAsJson();
		JsonObject data = new JsonObject();
		data.add("telemetry", arr);
		sendAll(getMessage("update", data).toString());
	}
	
	public void updateConfig() {
		JsonArray arr = getConfigAsJson();
		JsonObject data = new JsonObject();
		data.add("config", arr);
		sendAll(getMessage("update", data).toString());
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
	
	private static JsonObject getMessage(String type, JsonObject data) {
		JsonObject msg = new JsonObject();
		msg.add("type", new JsonPrimitive(type));
		msg.add("data", data);
		return msg;
	}
	
	public synchronized void sendAll(String msg) {
		for (RobotWebSocket ws : sockets) {
			try {
				ws.send(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void addSocket(RobotWebSocket socket) {
		sockets.add(socket);
		updateConfig();
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
				updateConfig();
			}
		} else if (type.equals("update")) {
			JsonObject data = msg.get("data").getAsJsonObject();
			if (data.has("config")) {
				JsonArray arr = data.get("config").getAsJsonArray();
				for (int i = 0; i < arr.size(); i++) {
				    config.get(i).updateFromJson(arr.get(i).getAsJsonObject());
                }
			}
		} else {
			Log.i("Dashboard", String.format("unknown message recv'd: '%s'", type));
			Log.i("Dashboard", msg.toString());
		}
	}
	
	public void stop() {
		server.stop();
	}
	
}
