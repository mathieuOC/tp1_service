package cal.info;

import java.util.Objects;

public class chaussette {
    private int id;
    private String couleur;
    private String taille;
    private String tissu;
    private double prix;

    public chaussette() {}

    public chaussette(int id, String couleur, String taille, String tissu, double prix) {
        this.id = id;
        this.couleur = couleur;
        this.taille = taille;
        this.tissu = tissu;
        this.prix = prix;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    public String getTaille() { return taille; }
    public void setTaille(String taille) { this.taille = taille; }
    public String getTissu() { return tissu; }
    public void setTissu(String tissu) { this.tissu = tissu; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        chaussette other = (chaussette) obj;
        return Objects.equals(id, other.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
