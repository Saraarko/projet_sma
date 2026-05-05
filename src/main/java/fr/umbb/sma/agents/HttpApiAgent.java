package fr.umbb.sma.agents;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import fr.umbb.sma.utils.IncidentLogger;
import jade.core.behaviours.OneShotBehaviour;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class HttpApiAgent extends BaseAgent {
    private HttpServer server;
    private static final int PORT = 8080;

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                try {
                    server = HttpServer.create(new InetSocketAddress(PORT), 0);
                    server.createContext("/api/data", new DataHandler());
                    server.createContext("/api/health", new HealthHandler());
                    server.setExecutor(null);
                    server.start();
                    logMessage("HTTP API server started on port " + PORT);
                } catch (IOException e) {
                    logMessage("Failed to start HTTP server: " + e.getMessage());
                }
            }
        });
    }

    private static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String json = IncidentLogger.getInstance().toJson();
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    private static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            byte[] bytes = "{\"status\":\"ok\"}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }

    @Override
    protected void takeDown() {
        if (server != null) {
            server.stop(0);
            logMessage("HTTP API server stopped");
        }
    }
}
