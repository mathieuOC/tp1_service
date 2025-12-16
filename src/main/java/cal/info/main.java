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




/*package cal.info;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class Hickaripool {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();


        config.setJdbcUrl("jdbc:h2:file:./data/hautlesbas-db");
        config.setDriverClassName("org.h2.Driver");
        config.setUsername("sa");
        config.setPassword("");


        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30_000);
        config.setIdleTimeout(600_000);
        config.setMaxLifetime(1_800_000);


        config.setAutoCommit(false);
        config.setPoolName("HautLesBasPool");

        dataSource = new HikariDataSource(config);
    }

    private Hickaripool() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdown() {
        dataSource.close();
    }
}
ceci est mon hickari mais le commit push ne le laisse pas etre commiter allors je trouve un alternative
cchat changement pour commit hey commit
 */