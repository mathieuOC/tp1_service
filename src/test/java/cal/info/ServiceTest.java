package cal.info;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTest {

    private static Connection conn;
    private static serviceinventaire inventaire;
    private static servicevente ventes;

    @BeforeAll
    static void init() throws Exception {
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        inventaire = new serviceinventaire(conn);
        ventes = new servicevente(conn, inventaire);
        System.out.println(" Base de test initialisée.");
    }

    @Test
    @Order(1)
    void testAjoutChaussette() {
        chaussette c = new chaussette(0, "rouge", "M", "coton", 9.99);
        chaussette ajoutée = inventaire.ajouter(c);
        assertNotNull(ajoutée);
        assertTrue(ajoutée.getId() > 0);
        System.out.println(" Chaussette ajoutée : " + ajoutée);
    }

    @Test
    @Order(2)
    void testListerChaussettes() throws SQLException {
        List<chaussette> liste = inventaire.lister();
        assertFalse(liste.isEmpty());
        assertEquals("rouge", liste.get(0).getCouleur());
        System.out.println("Chaussettes listées : " + liste.size());
    }

    @Test
    @Order(3)
    void testTrouverParId() {
        chaussette c = inventaire.trouverParId(1);
        assertNotNull(c);
        assertEquals("rouge", c.getCouleur());
        System.out.println("Trouvée : " + c);
    }

    @Test
    @Order(4)
    void testCreerVente() {
        // Crée une vente avec l'ID de la chaussette 1
        vente v = ventes.creerVente(List.of(1));
        assertNotNull(v);
        assertTrue(v.getId() > 0);
        System.out.println("Vente créée : ID=" + v.getId());
    }

    @Test
    @Order(5)
    void testListerVentes() {
        List<vente> liste = ventes.lister();
        assertFalse(liste.isEmpty());
        System.out.println(" Ventes listées : " + liste.size());
    }


}