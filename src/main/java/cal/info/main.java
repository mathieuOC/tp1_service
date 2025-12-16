package cal.info;

import com.sun.net.httpserver.HttpServer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:./bd_inventaire")) {

            serviceinventaire inv = new serviceinventaire(conn);
            servicevente ventes = new servicevente(conn, inv);

            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/chaussettes", new chaussettecontroleur(inv));
            server.createContext("/ventes", new ventecontrolleur(ventes));

            server.start();
            System.out.println("Serveur lancé sur http://localhost:8080");
            System.out.println("chaussettes  (GET, POST, DELETE,PUT)");
            System.out.println("ventes  (GET, POST, DELETE,PUT)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





*/