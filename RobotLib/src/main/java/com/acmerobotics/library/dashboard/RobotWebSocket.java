package com.acmerobotics.library.dashboard;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
		System.out.println("[OPEN]\t" + this.getHandshakeRequest().getRemoteIpAddress());
		dashboard.addSocket(this);
	}

	@Override
	protected void onClose(CloseCode code, String reason, boolean initiatedByRemote) {
		System.out.println("[CLOSE]\t" + this.getHandshakeRequest().getRemoteIpAddress());
		dashboard.removeSocket(this);
	}

	@Override
	protected void onMessage(WebSocketFrame message) {
		JsonObject msg = parser.parse(message.getTextPayload()).getAsJsonObject();
		if (msg.get("type").getAsString().equals("ping")) {
			try {
				send("{\"type\":\"pong\"}");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			dashboard.onMessage(this, msg);
		}
	}

	@Override
	protected void onPong(WebSocketFrame pong) {
		
	}

	@Override
	protected void onException(IOException exception) {
		
	}
	
}