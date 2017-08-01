package me.costa.gustavo.app;


import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;



@ServerEndpoint(value = "/image")
public class ImageWebSocket {
	@OnOpen
	public void onSessionOpened(Session session) {
		System.out.println("onSessionOpened: " + session);
	}
	@OnMessage
	public void onMessageReceived(String message, Session session) throws IOException {
		if ("img".equals(message)) {
			String image = OpenCVHelper.getInstance().getBase64Image();
			if (image != null) {
				session.getBasicRemote().sendText(image);
			} else {
				System.out.println("Error: Null image");
			}
		}
	}
	@OnClose
	public void onClose(Session session, CloseReason closeReason){
		System.out.println("onClose: " + session);
	}
	@OnError
	public void onErrorReceived(Throwable t) {
		System.out.println("onErrorReceived: " + t);
	}
}

