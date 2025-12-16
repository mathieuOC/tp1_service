package cal.info;

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