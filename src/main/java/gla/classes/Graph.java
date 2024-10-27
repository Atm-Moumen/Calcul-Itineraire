package gla.classes;


import gla.database.SQLiteDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.ArrayList;


public class Graph {
    public ArrayList<Sommet> graph = new ArrayList<>(); // contient les sommets de graph
    public ArrayList<Station> list_station_existe_graph = new ArrayList<>(); // contient les station de graph

    public Graph(){

    }

    // une methode qui verifie si une liste de type Sommet contient au moins un sommet dont son nom_som
    boolean contientSommet(ArrayList<Sommet> list, String nom_som) {
        return list.stream().anyMatch(p -> p.getSt().equals(nom_som));
    }

    //renvoie la station qu'on cherche
    public Station chercheStation(String nom_st, double x, double y){
        for(Station s : list_station_existe_graph){
            if(s.getNom().equals(nom_st) && s.getX()==x && s.getY()==y){
                return s;
            }
        }
        return null;
    }
    // ajouter une sommet dans le graph
    public void ajouter_Sommet(SQLiteDB bdd, String st) throws SQLException {
        if (bdd.station_exist(st)) { // verifie si la station existe dans la bdd
            if (!contientSommet(graph, st)){ //Ajoute le sommet si il n'est pas dnas le graphe
                graph.add(new Sommet(st));
                ResultSet res_st_suivant = bdd.list_arete_start_at_stA(st);
                while (res_st_suivant.next()) {
                    double x = Double.parseDouble(res_st_suivant.getString("A_x"));
                    double y = Double.parseDouble(res_st_suivant.getString("A_y"));
                    Station A = chercheStation(st, x, y);
                    if(A == null){ // ajoute les stations A et B à la liste des stations si ils ne sont pas dedans
                        A = new Station(st, x, y);
                        list_station_existe_graph.add(A);
                    }
                    x = Double.parseDouble(res_st_suivant.getString("B_x"));
                    y = Double.parseDouble(res_st_suivant.getString("B_y"));
                    Station B = chercheStation(res_st_suivant.getString("stationB"), x, y);
                    if(B == null){
                        B = new Station(res_st_suivant.getString("stationB"), x, y);
                        list_station_existe_graph.add(B);
                    }
                    graph.get(graph.size() - 1).getSuivant().add(new Edge(A,B,
                            res_st_suivant.getString("distanceAB"),
                            res_st_suivant.getString("tempsAB"),
                            res_st_suivant.getString("nom"),
                            res_st_suivant.getString("variante"))); //Ajoute les suivants du sommet
                }
            }
        } else {
            System.out.println("la station "+st+"n'existe pas, le graphe ne peut pas etre construit !!! ");
        }
    }

    //renvoie le sommet qu'on cherche
    public Sommet chercheSommet(String station){
        for(int i = 0; i < graph.size(); i++){
            if(graph.get(i).getSt().equals(station)){
                return graph.get(i);
            }
        }
        return null;
    }

    //recupere le sommet le plus petit en temps
    public Sommet lePlusPetit (ArrayList<Sommet> liste){
        Sommet s = liste.get(0);
        for(int i = 1; i < liste.size(); i++){
            if(!s.getTemps().plusRapide(liste.get(i).getTemps())){
                s = liste.get(i);
            }
        }
        return s;
    }

    //calcule la distance entre 2 points en Km
    public double distanceAB(double xA, double yA, double xB, double yB){
        double d = Math.acos(Math.sin(yA)*Math.sin(yB)+Math.cos(yA)*Math.cos(yB)*Math.cos(xB-xA));
        return d * 6378.137;
    }


    public Temps minHoraire(SQLiteDB bdd, String station, String ligne, String variante, Temps tempsCourant) throws SQLException {
        // on récupère la station de depart de la variante
        String stationDepart = bdd.stationDepart(ligne, variante).getString("nom");
        // on calcule le temps nécessaire pour aller d'une station de départ jusqu'à la station demandée
        Temps tempsAjoute = bdd.calculateTime(station,ligne,variante,stationDepart);
        ResultSet rs2 = bdd.getHeure(ligne,stationDepart, variante);

        if(rs2.next()) {
            //initialise le minimum avec le premier horaire
            Temps horaire = new Temps(Integer.parseInt(rs2.getString("heure")), Integer.parseInt(rs2.getString("minutes")), 0);
            Temps temp = horaire.ajouterTemps(tempsAjoute, false);
            Temps min = temp.reduireTemps(tempsCourant);

            while (rs2.next()) {//cherche le minimum
                horaire = new Temps(Integer.parseInt(rs2.getString("heure")), Integer.parseInt(rs2.getString("minutes")), 0);
                temp = horaire.ajouterTemps(tempsAjoute, false);
                if(temp.reduireTemps(tempsCourant).plusRapide(min)){
                    min = temp.reduireTemps(tempsCourant);
                }
            }
            return min;
        }
        return new Temps("00:00");
    }

    // construction de tous les chemin a partir de depart
    public void algorithme_dijkstra(SQLiteDB bdd, String depart, Temps actuel, Temps marcheD) throws SQLException{
        ArrayList<Sommet> queue = new ArrayList<>(); // la queue de sommet à parcourir
        ArrayList<Sommet> visite = new ArrayList<>(); // la liste de sommet déjà visité
        ajouter_Sommet(bdd, depart);
        if(marcheD != null){
            graph.get(0).setTemps(marcheD); //Construction du sommet de départ avec temps égale au temps de marche lorsque on part d'une coordonnée
        }else{
            graph.get(0).setTemps(new Temps("00:00")); //construction du sommet de départ avec temps à 0 lorsqu'on part de la station
        }
        queue.add(graph.get(0));
        while(!queue.isEmpty()){
            Sommet s = lePlusPetit(queue); // Choisir le sommet le plus petit de la queue
            queue.remove(s);
            if(!visite.contains(s)){ // fait dijkstra si il n'a pas encore été visité
                visite.add(s);
                for(Edge e : s.getSuivant()) {
                    ajouter_Sommet(bdd, e.getSt_dest().getNom()); //construction des sommets suivants
                    Sommet suivant = chercheSommet(e.getSt_dest().getNom());
                    if(suivant != null){ //modifier le temps des sommets suivants
                        //ajouter dans la queue et modifier le temps si le temps initial est moins rapide et change son précédent
                        Temps t = s.getTemps();
                        Temps attente = null;
                        Temps marche = null;
                        if(s.getPrecedent()!= null && s.getPrecedent().getSt_dest() != e.getSt_dep()){ //regarde si on a changé de metro/voie et rajoute le temps de changement
                            Edge prec = s.getPrecedent();
                            double tempsDeMarche = calcul_temps_de_marche(prec.getSt_dest().getX(), prec.getSt_dest().getY(), e.getSt_dep().getX(), e.getSt_dep().getY());
                            marche = convertToHMS(tempsDeMarche);//calcule le temps de marche en sec avec comme vitesse 5Km/h
                            t= t.ajouterTemps(marche,true); //ajouter le temps de marche à la durée
                            Temps courant = actuel.ajouterTemps(t,false); //calculer l'heure courant
                            attente = minHoraire(bdd,s.getSt(), e.getLigne(), e.getVariante(), courant);//calculer l'heure d'attente
                            t = t.ajouterTemps(attente, true); //ajouter le temps d'attente
                        }else if(s.getSt().equals(depart)){//temps d'attente pour le depart
                            Temps courant = actuel.ajouterTemps(t,false); //calculer l'heure courant
                            attente = minHoraire(bdd,s.getSt(), e.getLigne(), e.getVariante(),courant); //calculer l'heure d'attente
                            t = t.ajouterTemps(attente,true); //ajouter le temps d'attente
                        }
                        t = t.ajouterTemps(new Temps(e.getDuree()), true); //ajouter la duree de trajet
                        if(suivant.getTemps() == null || (suivant.getTemps() != null && !suivant.getTemps().plusRapide(t))){ //regarde si il y a un meilleurs temps pour le sommet suivant et le met a jour
                            suivant.setTemps(t);
                            suivant.setMarche(marche);
                            suivant.setAttente(attente);
                            queue.add(suivant);
                            suivant.setPrecedent(e);
                        }
                    }
                }
            }
        }
    }

    // calcule la distance entre deux stations
    public double distanceHaversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6378.137; // rayon de la terre en kilomètres
        double dlat = Math.toRadians(lat2 - lat1);
        double dlon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dlat/2) * Math.sin(dlat/2) + Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) * Math.sin(dlon/2) * Math.sin(dlon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d =  (R * c);
        return d;
    }

    // retourne id de la station la plus proche d'une point geographique
    public int trouve_station_plus_proche(SQLiteDB bdd, double x, double y) throws SQLException {
       int id_st = bdd.is_it_station(x, y);
       Map<Integer, Double> distance = new HashMap<>();

        if ( id_st== -1){
            ResultSet res = bdd.getAllStation();
            while (res.next()){
                distance.put(res.getInt("id"),distanceHaversine(x,y,
                        res.getDouble("x"), res.getDouble("y")));
            }
            // retourner la clé de la distance minimale
            Integer minKey = Collections.min(distance.entrySet(), Comparator.comparing(Map.Entry::getValue)).getKey();

            return minKey;
        }else{
            return id_st;
        }
    }

    // calcul le temps de marche entre deux points geographiques
    public double calcul_temps_de_marche (double x1, double y1, double x2, double y2){
        final double  vitesse_marche = 5;
        return distanceHaversine(x1,y1,x2,y2) / vitesse_marche;
    }

    // convertir le temps en (heure:min:sec)
    public  Temps convertToHMS(double hours) {
        int h = (int) hours;
        int m = (int) ((hours - h) * 60);
        int s = (int) ((((hours - h) * 60) - m) * 60);
        return new Temps(h,m,s);
    }

    public double convertirTempsEnHours(Temps tempsHMS) {
        if (tempsHMS != null) {
            double decimalHours = (tempsHMS.getHeure()) + (tempsHMS.getMinute() / 60.0) + tempsHMS.getSeconde() / 3600.0; // calcul de la durée en hours
           //return TimeUnit.HOURS.convert((long) (decimalHours * 60), TimeUnit.MINUTES) / 60.0;
            return decimalHours;
        }
        else
            return 0;
    }
}

