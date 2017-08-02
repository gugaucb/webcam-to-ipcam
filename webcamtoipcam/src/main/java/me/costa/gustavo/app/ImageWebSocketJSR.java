package me.costa.gustavo.app;


import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;



@WebSocket
public class ImageWebSocketJSR {
	@OnWebSocketConnect
	public void onSessionOpened(Session session) {
		System.out.println("onSessionOpened: " + session);
	}

	@OnWebSocketMessage
	public void onMessageReceived(Session session, String message) throws IOException {
		if ("img".equals(message)) {
			String image = OpenCVHelper.getInstance().getBase64Image();
			if (image != null) {
				session.getRemote().sendString(image);
			} else {
				System.out.println("Error: Null image");
			}
		}
	}
	@OnWebSocketClose
	public void onClose(Session session, int status, String reason){
		System.out.println("onClose: " + session);
	}
	@OnWebSocketError
	public void onErrorReceived(Throwable t) {
		System.out.println("onErrorReceived: " + t);
	}
}

