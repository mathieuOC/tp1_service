package cal.info;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ventecontrolleur implements HttpHandler {

    private final servicevente service;
    private final Gson gson = new Gson();

    public ventecontrolleur(servicevente service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;

        try {
            switch (method) {
                case "GET" -> response = controlleurGet();
                case "POST" -> response = controlleurPost(exchange);
                case "DELETE" -> response = controlleurDelete(exchange);
                default -> {
                    sendResponse(exchange, 405, "{\"error\":\"Méthode non supportée\"}");
                    return;
                }
            }
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private String controlleurGet() {
        List<vente> ventes = service.lister();
        return gson.toJson(ventes);
    }

    private String controlleurPost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JsonObject json = gson.fromJson(body, JsonObject.class);
        List<Integer> ids = gson.fromJson(json.get("chaussetteIds"), List.class);
        vente v = service.creerVente(ids);
        return gson.toJson(v);
    }

    private String controlleurDelete(HttpExchange exchange) {
        Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
        if (!params.containsKey("id"))
            throw new IllegalArgumentException("Paramètre 'id' requis");

        int id = Integer.parseInt(params.get("id"));
        boolean ok = service.annulerVente(id);
        return "{\"success\":" + ok + "}";
    }

    private Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) return Map.of();
        return List.of(query.split("&")).stream()
                .map(p -> p.split("="))
                .filter(p -> p.length == 2)
                .collect(Collectors.toMap(p -> p[0], p -> p[1]));
    }

    private void sendResponse(HttpExchange exchange, int code, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}