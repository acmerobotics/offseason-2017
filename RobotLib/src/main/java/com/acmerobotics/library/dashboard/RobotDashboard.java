package com.acmerobotics.library.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.acmerobotics.library.dashboard.draw.Canvas;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManagerImpl;
import com.qualcomm.robotcore.util.ClassFilter;
import com.qualcomm.robotcore.util.ClassManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RobotDashboard {
	public static Gson gson = new GsonBuilder()
			.create();

	private static RobotDashboard dashboard;

	public static RobotDashboard open(Context ctx, EventLoop eventLoop) {
		dashboard = new RobotDashboard(ctx, eventLoop.getOpModeManager());
		return dashboard;
	}

	public static RobotDashboard getInstance() {
		return dashboard;
	}

	public static final String CONFIG_PREFS = "config";

	private Map<String, JsonElement> telemetry;
	private SharedPreferences prefs;
	private List<RobotWebSocket> sockets;
	private RobotWebSocketServer server;
    private List<OptionGroup> optionGroups;
	private Canvas fieldOverlay;
    private OpModeManagerImpl manager;
	
	private RobotDashboard(Context ctx, OpModeManagerImpl manager) {
		prefs = ctx.getSharedPreferences(CONFIG_PREFS, Context.MODE_PRIVATE);
		sockets = new ArrayList<>();
		telemetry = new HashMap<>();
		fieldOverlay = new Canvas();

		this.manager = manager;
//		manager.registerListener(this);

		optionGroups = new ArrayList<>();
		try {
            final ClassManager classManager = new ClassManager();
            classManager.registerFilter(new ClassFilter() {
                @Override
                public void filter(Class clazz) {
                    if (clazz.isAnnotationPresent(Config.class)) {
                        Config annotation = (Config) clazz.getAnnotation(Config.class);
                        String name = annotation.value().equals("") ? clazz.getSimpleName() : annotation.value();
                        optionGroups.add(new OptionGroup(clazz, name, prefs));
                    }
                }
            });
            classManager.processAllClasses();
        } catch (IOException e) {
            Log.e("DashboardConfig", e.getMessage());
        }

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

    public JsonElement getFieldOverlayJson() {
		return gson.toJsonTree(fieldOverlay.getInstructions());
	}

	public JsonObject getFieldOverlayUpdateMessage() {
		JsonObject data = new JsonObject();
		data.add("field", getFieldOverlayJson());
		return getMessage("update", data);
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
		for (Entry<String, JsonElement> line : telemetry.entrySet()) {
			JsonObject obj = new JsonObject();
			obj.add("name", new JsonPrimitive(line.getKey()));
			obj.add("value", line.getValue());
			arr.add(obj);
		}
		return arr;
	}

	private JsonObject getTelemetryUpdateMessage() {
		JsonObject data = new JsonObject();
		data.add("telemetry", getTelemetryJson());
		return getMessage("update", data);
	}

	public void addTelemetry(String key, String value) {
		addTelemetry(key, new JsonPrimitive(value));
	}

	public void addTelemetry(String key, boolean value) {
	    addTelemetry(key, new JsonPrimitive(value));
    }

	public void addTelemetry(String key, int value) {
	    addTelemetry(key, new JsonPrimitive(value));
    }

    public void addTelemetry(String key, double value) {
	    addTelemetry(key, new JsonPrimitive(value));
    }

	public void addTelemetry(String key, JsonElement element) {
		telemetry.put(key, element);
	}

	public void updateTelemetry() {
	    sendAll(getTelemetryUpdateMessage().toString());
    }

    public Canvas getFieldOverlay() {
		return fieldOverlay;
	}

	public void drawOverlay() {
		sendAll(getFieldOverlayUpdateMessage().toString());
		fieldOverlay.clear();
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
