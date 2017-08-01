package me.costa.gustavo.app;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@WebServlet(urlPatterns="/image1")
public class ToUpperWebSocketServlet extends WebSocketServlet {

	/**
	 * mvn jetty:run
	 */
	private static final long serialVersionUID = 3322334510779470630L;

	@Override
	public void configure(WebSocketServletFactory factory) {
		      factory.register(ImageWebSocketJSR.class);

	}

}
