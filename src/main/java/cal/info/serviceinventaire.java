package cal.info;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceinventaire {
    private final Connection conn;

    public serviceinventaire(Connection conn) {
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

    public List<chaussette> lister() {
        List<chaussette> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM chaussettes");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new chaussette(
                        rs.getInt("id"),
                        rs.getString("couleur"),
                        rs.getString("taille"),
                        rs.getString("tissu"),
                        rs.getDouble("prix")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
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
                return null; // pas trouvé
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification : " + e.getMessage(), e);
        }
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