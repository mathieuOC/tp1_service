package cal.info;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class vente {
    private int id;
    private LocalDateTime date;
    private List<chaussette> chaussettes = new ArrayList<>();

    public vente() {
        this.date = LocalDateTime.now();
    }

    public vente(int id) {
        this();
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public List<chaussette> getChaussettes() { return chaussettes; }
    public void addChaussette(chaussette c) { chaussettes.add(c); }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        vente other = (vente) obj;
        return Objects.equals(id, other.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
