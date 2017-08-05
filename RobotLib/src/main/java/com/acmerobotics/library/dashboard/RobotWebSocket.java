package com.acmerobotics.library.dashboard;

import android.util.Log;

import com.acmerobotics.library.dashboard.message.Message;
import com.acmerobotics.library.dashboard.message.MessageType;
import com.google.gson.JsonParser;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoWSD.WebSocket;
import fi.iki.elonen.NanoWSD.WebSocketFrame;
import fi.iki.elonen.NanoWSD.WebSocketFrame.CloseCode;

public class RobotWebSocket extends WebSocket {
	
	private JsonParser parser;
	private RobotDashboard dashboard;

	public RobotWebSocket(IHTTPSession handshakeRequest, RobotDashboard dash) {
		super(handshakeRequest);
		parser = new JsonParser();
		dashboard = dash;
	}

	@Override
	protected void onOpen() {
		Log.i("Dashboard", "[OPEN]\t" + this.getHandshakeRequest().getRemoteIpAddress());
		dashboard.addSocket(this);
	}

	@Override
	protected void onClose(CloseCode code, String reason, boolean initiatedByRemote) {
		Log.i("Dashboard", "[CLOSE]\t" + this.getHandshakeRequest().getRemoteIpAddress());
		dashboard.removeSocket(this);
	}

	@Override
	protected void onMessage(WebSocketFrame message) {
		Message msg = RobotDashboard.GSON.fromJson(message.getTextPayload(), Message.class);
		if (msg.getType() == MessageType.PING) {
			send(new Message(MessageType.PONG));
		} else {
			Log.i("DashboardMessage", "[RECV] " + message.getTextPayload());
			dashboard.onMessage(this, msg);
		}
	}

	@Override
	protected void onPong(WebSocketFrame pong) {
		
	}

	@Override
	protected void onException(IOException exception) {
		
	}

	public void send(Message message) {
		try {
			String messageStr = RobotDashboard.GSON.toJson(message);
			if (message.getType() != MessageType.PONG) {
				Log.i("DashboardMessage", "[SENT] " + messageStr);
			}
			send(messageStr);
		} catch (IOException e) {
			Log.e("RobotWebSocket", e.getMessage() + ": " + e);
		}
	}
	
}