package com.acmerobotics.library.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.acmerobotics.library.dashboard.draw.Canvas;
import com.acmerobotics.library.dashboard.message.Message;
import com.acmerobotics.library.dashboard.message.MessageDeserializer;
import com.acmerobotics.library.dashboard.message.MessageType;
import com.acmerobotics.library.dashboard.message.UpdateMessageData;
import com.acmerobotics.library.dashboard.util.ClassFilter;
import com.acmerobotics.library.dashboard.util.ClasspathScanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.qualcomm.robotcore.eventloop.EventLoop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RobotDashboard {
    public static final String TAG = "RobotDashboard";
	public static final String CONFIG_PREFS = "config";

	public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Message.class, new MessageDeserializer())
			.create();

	private static RobotDashboard dashboard;

	// TODO I'm sure there's a better way to make a static singleton
	public static RobotDashboard open(Context ctx, EventLoop eventLoop) {
		// the eventLoop can be used to get the op mode manager and monitor op mode activity
		dashboard = new RobotDashboard(ctx);
		return dashboard;
	}

	public static RobotDashboard getInstance() {
		return dashboard;
	};

	private Telemetry telemetry;
	private SharedPreferences prefs;
	private List<RobotWebSocket> sockets;
	private RobotWebSocketServer server;
    private List<OptionGroup> optionGroups;
	private Canvas fieldOverlay;
	
	private RobotDashboard(Context ctx) {
		prefs = ctx.getSharedPreferences(CONFIG_PREFS, Context.MODE_PRIVATE);
		sockets = new ArrayList<>();
		telemetry = new Telemetry();
		fieldOverlay = new Canvas();
		optionGroups = new ArrayList<>();

        ClasspathScanner scanner = new ClasspathScanner(new ClassFilter() {
            @Override
            public boolean shouldProcessClass(String className) {
                return className.startsWith("com.acmerobotics");
            }

            @Override
            public void processClass(Class clazz) {
                if (clazz.isAnnotationPresent(Config.class)) {
                    Config annotation = (Config) clazz.getAnnotation(Config.class);
                    String name = annotation.value().equals("") ? clazz.getSimpleName() : annotation.value();
                    optionGroups.add(new OptionGroup(clazz, name, prefs));
                }
            }
        });
        scanner.scanClasspath();

		server = new RobotWebSocketServer(this);
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void registerConfigClass(Class<?> configClass, String name) {
	    optionGroups.add(new OptionGroup(configClass, name, prefs));
	    sendAll(getConfigUpdateMessage());
    }

    public void registerConfigClass(Class<?> configClass) {
	    registerConfigClass(configClass, configClass.getSimpleName());
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

    public void updateConfigWithJson(JsonElement configJson) {
        JsonArray arr = configJson.getAsJsonArray();
        for (int i = 0; i < arr.size(); i++) {
            optionGroups.get(i).updateFromJson(arr.get(i));
        }
    }

	public Message getFieldOverlayUpdateMessage() {
	    return new Message(MessageType.UPDATE, UpdateMessageData.builder().fieldOverlay(fieldOverlay).build());
	}

    public Message getConfigUpdateMessage() {
	    return new Message(MessageType.UPDATE, UpdateMessageData.builder().config(getConfigJson()).build());
    }

	private Message getTelemetryUpdateMessage() {
		return new Message(MessageType.UPDATE, UpdateMessageData.builder().telemetry(telemetry).build());
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
                Log.w(TAG, String.format("unknown message recv'd: '%s'", msg.getType()));
                Log.w(TAG, msg.toString());
                break;
        }
	}

	public void stop() {
		server.stop();
		dashboard = null;
	}
}
