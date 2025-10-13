package cal.info;

import java.sql.*;
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

    public vente creerVente(List<Integer> chaussetteIds) {
        try {
            conn.setAutoCommit(false);


            vente v = new vente();
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ventes (date) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setTimestamp(1, Timestamp.valueOf(v.getDate()));
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) v.setId(rs.getInt(1));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ventes_chaussettes (id_vente, id_chaussette) VALUES (?, ?)")) {
                for (Integer idC : chaussetteIds) {
                    chaussette c = inventaire.trouverParId(idC);
                    if (c == null) throw new IllegalArgumentException("Chaussette introuvable id=" + idC);
                    ps.setInt(1, v.getId());
                    ps.setInt(2, idC);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return v;
        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException(e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    public boolean annulerVente(int idVente) {
        String deleteDetails = "DELETE FROM ventes_chaussettes WHERE id_vente = ?";
        String deleteVente = "DELETE FROM ventes WHERE id = ?";

        try (PreparedStatement ps1 = conn.prepareStatement(deleteDetails);
             PreparedStatement ps2 = conn.prepareStatement(deleteVente)) {

            ps1.setInt(1, idVente);
            ps1.executeUpdate();

            ps2.setInt(1, idVente);
            int rows = ps2.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
}