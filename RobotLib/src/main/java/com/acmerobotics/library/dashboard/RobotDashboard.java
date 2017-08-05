package com.acmerobotics.library.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.acmerobotics.library.dashboard.draw.Canvas;
import com.acmerobotics.library.dashboard.message.Message;
import com.acmerobotics.library.dashboard.message.MessageDeserializer;
import com.acmerobotics.library.dashboard.message.MessageType;
import com.acmerobotics.library.dashboard.message.UpdateMessageData;
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
import java.util.List;

public class RobotDashboard {
	public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Message.class, new MessageDeserializer())
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

	private Telemetry telemetry;
	private SharedPreferences prefs;
	private List<RobotWebSocket> sockets;
	private RobotWebSocketServer server;
    private List<OptionGroup> optionGroups;
	private Canvas fieldOverlay;
    private OpModeManagerImpl manager;
	
	private RobotDashboard(Context ctx, OpModeManagerImpl manager) {
		prefs = ctx.getSharedPreferences(CONFIG_PREFS, Context.MODE_PRIVATE);
		sockets = new ArrayList<>();
		telemetry = new Telemetry();
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

    public JsonArray getConfigJson() {
	    JsonArray arr = new JsonArray();
	    for (OptionGroup group : optionGroups) {
	        JsonObject obj = new JsonObject();
	        obj.add("name", new JsonPrimitive(group.getName()));
	        obj.add("options", group.getAsJson());
	        arr.add(obj);
        }
        return arr;
    }

	public Message getFieldOverlayUpdateMessage() {
	    return new Message(MessageType.UPDATE, UpdateMessageData.builder().fieldOverlay(fieldOverlay).build());
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

    public Message getConfigUpdateMessage() {
	    return new Message(MessageType.UPDATE, UpdateMessageData.builder().config(getConfigJson()).build());
    }

	private Message getTelemetryUpdateMessage() {
		return new Message(MessageType.UPDATE, UpdateMessageData.builder().telemetry(telemetry).build());
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
		telemetry.addEntry(new Telemetry.Entry(key, element));
	}

	public void updateTelemetry() {
	    sendAll(getTelemetryUpdateMessage());
	    telemetry.clear();
    }

    public Canvas getFieldOverlay() {
		return fieldOverlay;
	}

	public void drawOverlay() {
		sendAll(getFieldOverlayUpdateMessage());
		fieldOverlay.clear();
	}

	public synchronized void sendAll(Message message) {
		for (RobotWebSocket ws : sockets) {
			ws.send(message);
		}
	}

	public synchronized void addSocket(RobotWebSocket socket) {
		sockets.add(socket);
		socket.send(getConfigUpdateMessage());
	}

	public synchronized void removeSocket(RobotWebSocket socket) {
		sockets.remove(socket);
	}

	public synchronized void onMessage(RobotWebSocket socket, Message msg) {
        switch(msg.getType()) {
            case GET: {
                String data = (String) msg.getData();
                if (data.equals("config")) {
                    socket.send(getConfigUpdateMessage());
                } else if (data.equals("telemetry")) {
                    socket.send(getTelemetryUpdateMessage());
                }
                break;
            }
            case UPDATE: {
                UpdateMessageData data = (UpdateMessageData) msg.getData();
                updateConfigWithJson(data.getConfig());
                break;
            }
            default:
                Log.i("Dashboard", String.format("unknown message recv'd: '%s'", msg.getType()));
                Log.i("Dashboard", msg.toString());
                break;
        }
	}

	public void stop() {
		server.stop();
		dashboard = null;
	}
}
