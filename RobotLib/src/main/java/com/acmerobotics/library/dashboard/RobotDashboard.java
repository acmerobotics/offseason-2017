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
    private List<Class<?>> configClasses;
	private Gson gson;
	
	private RobotDashboard(Context ctx) {
		prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		sockets = new ArrayList<>();
		telemetry = new HashMap<>();
		gson = new Gson();
		editor = prefs.edit();

        configClasses = new ArrayList<>();
        configClasses.add(Config.class);
        configClasses.add(Constants.class);

		server = new RobotWebSocketServer(this);
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JsonArray getConfigJson(Class<?> klass) {
	    try {
            JsonArray arr = new JsonArray();
            for (Field f : klass.getFields()) {
                JsonObject option = new JsonObject();
                option.add("name", new JsonPrimitive(f.getName()));
                Class<?> type = f.getType();
                switch (type.getSimpleName()) {
                    case "boolean":
                        option.add("type", new JsonPrimitive("boolean"));
                        option.add("value", new JsonPrimitive(f.getBoolean(null)));
                        break;
                    case "int":
                        option.add("type", new JsonPrimitive("int"));
                        option.add("value", new JsonPrimitive(f.getInt(null)));
                        break;
                    case "double":
                        option.add("type", new JsonPrimitive("double"));
                        option.add("value", new JsonPrimitive(f.getDouble(null)));
                        break;
                    case "String":
                        option.add("type", new JsonPrimitive("string"));
                        option.add("value", new JsonPrimitive(f.get(null).toString()));
                        break;
                    default:
                        if (type.getSuperclass().equals(Enum.class)) {
                            option.add("type", new JsonPrimitive("enum"));
                            JsonArray values = new JsonArray();
                            List<?> enumConstants = Arrays.asList(type.getEnumConstants());
                            for (Object constant : enumConstants) {
                                values.add(new JsonPrimitive(constant.toString()));
                            }
                            option.add("values", values);
                            Object value = f.get(null);
                            if (value == null) {
                                option.add("value", new JsonPrimitive(0));
                            } else {
                                option.add("value", new JsonPrimitive(enumConstants.indexOf(value)));
                            }
                        }
                        break;
                }
                arr.add(option);
            }
            return arr;
        } catch (IllegalAccessException e) {
	        e.printStackTrace();
	        return null;
        }
    }

    public static JsonArray getConfigJson(List<Class<?>> classes) {
	    JsonArray arr = new JsonArray();
	    for (Class<?> klass : classes) {
	        JsonObject obj = new JsonObject();
	        obj.add("name", new JsonPrimitive(klass.getSimpleName()));
	        obj.add("options", getConfigJson(klass));
	        arr.add(obj);
        }
        return arr;
    }

    public static void updateClassWithJson(Class<?> klass, JsonArray configJson) {
	    try {
            for (int i = 0; i < configJson.size(); i++) {
                JsonObject option = configJson.get(i).getAsJsonObject();
                Field f = klass.getField(option.get("name").getAsString());
                JsonElement value = option.get("value");
                switch (option.get("type").getAsString()) {
                    case "boolean":
                        f.setBoolean(null, value.getAsBoolean());
                        break;
                    case "int":
                        f.setInt(null, value.getAsInt());
                        break;
                    case "double":
                        f.setDouble(null, value.getAsDouble());
                        break;
                    case "string":
                        f.set(null, value.getAsString());
                        break;
                    case "enum":
                        int index = value.getAsInt();
                        f.set(null, f.getType().getEnumConstants()[index]);
                        break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static void updateClassesWithJson(List<Class<?>> classes, JsonArray configJson) {
	    for (int i = 0; i < classes.size(); i++) {
	        updateClassWithJson(classes.get(i), configJson.get(i).getAsJsonObject().get("options").getAsJsonArray());
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
	    data.add("config", getConfigJson(configClasses));
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
			    updateClassesWithJson(configClasses, data.get("config").getAsJsonArray());
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
