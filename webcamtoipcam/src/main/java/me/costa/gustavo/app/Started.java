package me.costa.gustavo.app;

import java.net.MalformedURLException;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
  
public class Started {  
    public static void main(String[] args) throws MalformedURLException {  
        int port = 8080;  
  
        Server server = new Server(port);  
  
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);  
        context.setContextPath("/");  
        context.setResourceBase(".");  
        context.setWelcomeFiles(new String[]{ "index.html" });  
        server.setHandler(context);  
          
          
       ServletHolder holderHome = new ServletHolder("/", DefaultServlet.class);  
        holderHome.setInitParameter("dirAllowed","true");  
        holderHome.setInitParameter("pathInfoOnly","true");  
        context.addServlet(holderHome,"/*");  
          
        try {  
            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);  
            wscontainer.addEndpoint(ImageWebSocketJSR.class);  
              
            server.start();  
            System.out.println("Listening port : " + port );  
              
            server.join();  
        } catch (Exception e) {  
            System.out.println("Error.");  
            e.printStackTrace();  
        }  
    }  
}  