package cal.info;

import com.zaxxer.hikari.pool.HikariPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceinventaire {
    private final Connection conn;
    private Connection connexion;

    public serviceinventaire(Connection conn) throws SQLException {
        this.conn = conn;

        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS chaussettes (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    couleur VARCHAR(50),
                    taille VARCHAR(10),
                    tissu VARCHAR(50),
                    prix DOUBLE
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la table chaussettes", e);
        }
    }

    public chaussette ajouter(chaussette c) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO chaussettes (couleur, taille, tissu, prix) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCouleur());
            ps.setString(2, c.getTaille());
            ps.setString(3, c.getTissu());
            ps.setDouble(4, c.getPrix());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
            return c;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<chaussette> lister() throws SQLException {
        List<chaussette> liste = new ArrayList<>();
        ResultSet rs = connexion.createStatement()
                .executeQuery("SELECT * FROM chaussettes");

        while (rs.next()) {
            liste.add(map(rs));
        }
        return liste;
    }

    public chaussette modifier(int id, chaussette updated) {
        String sql = "UPDATE chaussettes SET couleur=?, taille=?, tissu=?, prix=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, updated.getCouleur());
            ps.setString(2, updated.getTaille());
            ps.setString(3, updated.getTissu());
            ps.setDouble(4, updated.getPrix());
            ps.setInt(5, id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                updated.setId(id);
                return updated;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification : " + e.getMessage(), e);
        }
    }


    private chaussette map(ResultSet rs) throws SQLException {
        return new chaussette(
                rs.getInt("id"),
                rs.getString("couleur"),
                rs.getString("taille"),
                rs.getString("type_tissu"),
                rs.getDouble("prix")
        );
    }

    public chaussette trouverParId(int id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM chaussettes WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new chaussette(
                            rs.getInt("id"),
                            rs.getString("couleur"),
                            rs.getString("taille"),
                            rs.getString("tissu"),
                            rs.getDouble("prix")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean supprimer(int id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM chaussettes WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}