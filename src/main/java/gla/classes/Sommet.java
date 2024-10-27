package gla.classes;

import java.util.ArrayList;

public class Sommet {

    private String st ;
    private ArrayList<Edge> suivant = new ArrayList<>();

    private Edge precedent = null;

    private Temps temps = null;

    private Temps marche = null;

    private Temps attente = null;

    public Sommet (String st) {
        this.st = st;
    }

    public void add_next_edge(Edge arete){
        suivant.add(arete);
    }
    public Temps getTemps(){
        return temps;
    }

    public void setTemps(Temps t){
        temps = t;
    }

    public String getSt() {
        return st;
    }

    public Edge getPrecedent() {
        return precedent;
    }

    public void setPrecedent(Edge precedent) {
        this.precedent = precedent;
    }

    public ArrayList<Edge> getSuivant() {
        return suivant;
    }

    public Temps getAttente() {
        return attente;
    }

    public Temps getMarche() {
        return marche;
    }

    public void setAttente(Temps attente) {
        this.attente = attente;
    }

    public void setMarche(Temps marche) {
        this.marche = marche;
    }
}
