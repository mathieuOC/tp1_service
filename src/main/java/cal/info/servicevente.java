package cal.info;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class servicevente {
    private final Connection conn;
    private final serviceinventaire inventaire;

    public servicevente(Connection conn, serviceinventaire inventaire) {
        this.conn = conn;
        this.inventaire = inventaire;

        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS ventes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    date TIMESTAMP
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS ventes_chaussettes (
                    id_vente INT,
                    id_chaussette INT,
                    FOREIGN KEY (id_vente) REFERENCES ventes(id),
                    FOREIGN KEY (id_chaussette) REFERENCES chaussettes(id)
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création des tables de vente", e);
        }
    }

    public vente creer(List<Integer> idsChaussettes) throws SQLException {
        conn.setAutoCommit(false);

        try {
            int venteId;

            try (PreparedStatement ps =
                         conn.prepareStatement(
                                 "INSERT INTO ventes (date_vente) VALUES (?)",
                                 Statement.RETURN_GENERATED_KEYS)) {

                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                venteId = rs.getInt(1);
            }

            for (int id : idsChaussettes) {
                try (PreparedStatement ps =
                             conn.prepareStatement(
                                     "INSERT INTO vente_chaussette (vente_id, chaussette_id) VALUES (?, ?)")) {
                    ps.setInt(1, venteId);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }

                conn.prepareStatement(
                        "DELETE FROM chaussettes WHERE id=" + id).executeUpdate();
            }

            conn.commit();
            return new vente(venteId);

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public boolean annuler(int id) throws SQLException {
        conn.setAutoCommit(false);
        try {
            conn.prepareStatement(
                    "DELETE FROM vente_chaussette WHERE vente_id=" + id).executeUpdate();
            conn.prepareStatement(
                    "DELETE FROM ventes WHERE id=" + id).executeUpdate();
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
        return false;
    }

    public List<vente> lister() {
        List<vente> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM ventes");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                vente v = new vente(rs.getInt("id"));
                v.setDate(rs.getTimestamp("date").toLocalDateTime());
                list.add(v);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public vente creerVente(List<Integer> ids) {
return null;
    }
}
