package gla.classes;


public class Edge {

    private Station st_dest;
    private Station st_dep;

    private String distance;
    private String duree;

    private String ligne;

    private String variante;

    // costructeur de l'objet Edge
    public Edge(Station st_dep, Station st_dest, String distance, String duree, String ligne, String variante){
        this.st_dep = st_dep;
        this.st_dest = st_dest;
        this.distance = distance;
        this.duree = duree;
        this.ligne = ligne;

        this.variante = variante;
    }

    public String getDuree() {
        return duree;
    }

    public String getLigne() {
        return ligne;
    }

    public Station getSt_dest() {
        return st_dest;
    }

    public String getDistance() {
        return distance;
    }

    public String getVariante() {
        return variante;
    }

    public Station getSt_dep() {
        return st_dep;
    }
}
