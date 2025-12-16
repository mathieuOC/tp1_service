package cal.info;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class chaussettecontroleur implements HttpHandler {

    private final serviceinventaire service;
    private final Gson gson = new Gson();

    public chaussettecontroleur(serviceinventaire service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;

        try {
            switch (method) {
                case "GET":
                    response = handleGet();
                    break;
                case "POST":
                    response = handlePost(exchange);
                    break;
                case "PUT":
                    response = handlePut(exchange);
                    break;
                case "DELETE":
                    response = handleDelete(exchange);
                    break;
                default:
                    sendResponse(exchange, 405, "{\"error\":\"Méthode non supportée\"}");
                    return;
            }

            sendResponse(exchange, 200, response);

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }


    private String handleGet() throws SQLException {
        List<chaussette> result = service.lister();
        return gson.toJson(result);
    }


    private String handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            chaussette c = gson.fromJson(body, chaussette.class);
            chaussette added = service.ajouter(c);
            return gson.toJson(added);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("JSON invalide : " + e.getMessage());
        }
    }


    private String handlePut(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
        if (!params.containsKey("id")) {
            throw new IllegalArgumentException("Paramètre 'id' requis");
        }

        int id = Integer.parseInt(params.get("id"));
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        chaussette updated = gson.fromJson(body, chaussette.class);

        chaussette result = service.modifier(id, updated);
        if (result == null) {
            throw new IllegalArgumentException("Chaussette non trouvée");
        }

        return gson.toJson(result);
    }


    private String handleDelete(HttpExchange exchange) {
        Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());
        if (!params.containsKey("id")) {
            throw new IllegalArgumentException("Paramètre 'id' requis");
        }

        int id = Integer.parseInt(params.get("id"));
        boolean ok = service.supprimer(id);
        return "{\"success\":" + ok + "}";
    }

    private Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) return Map.of();
        return List.of(query.split("&")).stream()
                .map(p -> p.split("="))
                .filter(p -> p.length == 2)
                .collect(Collectors.toMap(p -> p[0], p -> p[1]));
    }

    private void sendResponse(HttpExchange ex, int code, String body) throws IOException {
        ex.sendResponseHeaders(code, body.getBytes(StandardCharsets.UTF_8).length);
        ex.getResponseBody().write(body.getBytes());
        ex.close();
    }
}