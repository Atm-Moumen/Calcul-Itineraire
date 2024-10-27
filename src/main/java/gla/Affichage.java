package gla;

import gla.classes.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Affichage {

    //couleurs affichage terminal
    private static final String reinit = "\u001B[0m";
    private static final String rouge = "\u001B[31m";
    private static final String vert = "\u001B[32m";
    private static final String jaune = "\u001B[33m";
    private static final String bleu = "\u001B[34m";

    public static void accueil(boolean fin){
        if(!fin) System.out.println(bleu+"=== PROJET GENIE LOGICIEL AVANCE ==="+reinit+"\n");
        else System.out.println("\n"+bleu+"=== AU REVOIR ET A BIENTOT ==="+reinit);
    }

    public static void erreur_connexion_bd(){
        System.out.println(rouge+"Erreur de connexion à la base de données."+reinit);
    }

    public static void succes_connexion_bd(){
        System.out.println(vert+"Connexion à la base de données réussie."+reinit);
    }

    public static void erreur_init_tables_sql(){
        System.out.println(rouge+"Erreur initialisation des tables sql."+reinit);
    }

    public static void lecture_csv_en_cours(){
        System.out.println(jaune+"Lecture du csv en cours .."+reinit);
    }

    public static void lecture_csv_ok(){
        System.out.println(vert+"Lecture du csv terminé."+reinit);
    }

    public static void lecture_csv_erreur(){
        System.out.println(rouge+"Erreur lecture du csv."+reinit);
    }

    public static void menu(){
        System.out.println(bleu+"""
                                
                    === MENU ===
                    """+reinit+"""
                    1- Rechercher les lignes passant par une station
                    2- Rechercher la liste des stations d'une ligne
                    3- Rechercher les horaires de passage d'une station
                    4- Rechercher un itinéraire non optimisé
                    5- Rechercher un itinéraire optimisé
                    6- Quitter
                    """); // on enleve temporairement 5- Recherche un itinéraire (dijkstra en cours)
    }

    public static void choix_inexistant_menu(){
        System.out.println(rouge+"Choix inexistant."+reinit);
    }

    public static void choix_incorrect_menu(){
        System.out.println(rouge+"Choix incorrect."+reinit);
    }

    public static void station_ou_ligne_inexistante(String station_ou_ligne){
        System.out.println(rouge+"La "+station_ou_ligne+" demandée n'existe pas."+reinit);
        System.out.println("Veuillez réessayer: ");
    }

    public static void erreur_recherche_station(){
        System.out.println(rouge+"Erreur recherche station."+reinit);
    }

    public static void liste_variantes(String ligne, List<String> var){
        System.out.print(jaune + "Ligne " + reinit + ligne + jaune + " | Variantes: " + reinit);
        for(int i=0; i<var.size(); i++){
            System.out.print(var.get(i));
            if(i != var.size()-1) System.out.print(", ");
        }
        System.out.println();
    }

    public static void infos_ligne(String ligne, String variante, String depart, String arrivee){
        System.out.println("\n" + jaune + "=== Ligne " + reinit + ligne + jaune + " | Variante " + reinit + variante + jaune + " ===" + reinit);
        System.out.println(vert + "Trajet: " + bleu + depart + reinit + " => " + bleu + arrivee + reinit);
    }

    public static void liste_stations(LinkedList<String> ls){
        for(int i=0; i<ls.size(); i++){
            System.out.print(ls.get(i));
            if(i != ls.size()-1) System.out.print(" > ");
        }
        System.out.println();
    }

    public static void afficher_lignes(Station s, boolean infos_stations){
        if(!infos_stations) System.out.print("Les lignes ");
        for(int i=0; i<s.getLignes().size(); i++){
            System.out.print(vert + s.getLignes().get(i) + reinit);
            if(i != s.getLignes().size()-1) System.out.print(", ");
        }
        if(!infos_stations) System.out.println(" passent par la station " + jaune + s.getNom() + reinit + ".");
    }

    public static void erreur_ligne_incorrecte(){
        System.out.print(rouge + "Incorrect" + reinit + ", entrez une ligne présente dans la liste: ");
    }

    public static void variante(String v){
        System.out.print(jaune + "Variante " + v + reinit + ": ");
    }

    public static void erreur_variante_non_trouvee(String s, String l){
        System.out.println(rouge + "Aucune variante trouvée pour la station " + s + " et la ligne " + l + "." + reinit);
    }

    public static void afficher_une_station(Station s){
        System.out.println(jaune + "=== Station " + reinit + s.getNom() + jaune + " ===" + reinit);
        System.out.print("Les lignes qui passent par cette station: ");
        afficher_lignes(s, true);
        System.out.println();
    }

    public static void demander_choix_stations(ArrayList<Station> stations){
        System.out.println("Entrez le numéro de la station voulue: ");
        for(int i=0; i<stations.size(); i++){
            System.out.println(jaune + i + ". " + reinit + stations.get(i).getNom());
        }
    }

    public static void erreur_sql(){
        System.out.println(rouge + "Erreur sql." + reinit);
    }

    public static void erreur_recherche_itineraire_plan_0(){
        System.out.println(rouge + "Erreur recherche itinéraire." + reinit);
    }

    public static void erreur_recherche_itineraire_dijkstra(){
        System.out.println(rouge + "Erreur recherche itinéraire par Dijkstra" + reinit);
    }

    public static void erreur_non_float(){
        System.out.println(rouge + "Ceci n'est pas un float." + reinit);
    }

    public static void afficher_trajet(String depart, String arrivee){
        System.out.println("\n" + vert + "Trajet: " + bleu + depart + reinit + " => " + bleu + arrivee + reinit);
    }

    public static void afficher_heure_depart(Temps t){
        System.out.println(jaune + "Heure de départ: " + reinit + t);
    }

    public static void afficher_heure_arrivee(Temps t){
        System.out.println(jaune + "Heure d'arrivée: " + reinit + t);
    }

    public static void afficher_duree_trajet(Temps t){
        System.out.println(vert + "Durée du trajet: " + reinit + t);
    }

    //afficher l'itinéraire à partir du graphe sur lequel on a appliqué dijkstra
    public static Temps[] afficher_itineraire_dijkstra(Graph g, String arrivee, Temps actuel){
        Sommet sommet = g.chercheSommet(arrivee); //sommet d'arrivée
        if(sommet != null){
            StringBuilder iti = new StringBuilder();
            Temps t = sommet.getTemps(); //temps total

            while(sommet.getPrecedent() != null){ //ajouter au string les informations tant que le sommet courant a un precedent
                Edge prec = sommet.getPrecedent();
                String infoStation = "";
                Sommet sommetPrec = g.chercheSommet(prec.getSt_dep().getNom());
                if(sommet.getMarche() != null){
                    if(sommetPrec.getPrecedent() != null ){
                        infoStation += sommetPrec.getPrecedent().getSt_dest().getNom();
                    }
                    infoStation +=  vert + "\nChangement de ligne: "
                            + bleu + sommetPrec.getPrecedent().getLigne() + reinit + " => " + bleu + prec.getLigne() + reinit;
                    infoStation += jaune + "\nTemps de marche estimé: " + reinit + sommet.getMarche();
                }
                if(sommet.getAttente() != null){
                    infoStation += jaune + "\nTemps d'attente estimé: " + reinit + sommet.getAttente() + "\n";
                }
                Temps duree =  new Temps(prec.getDuree());

                infoStation += prec.getSt_dep().getNom() + "\n | Ligne " + bleu + prec.getLigne() + reinit + " Durée " + vert;
                if(duree.getHeure() == 0){
                    infoStation +=  String.format("%02dm %02ds", duree.getMinute(), duree.getSeconde()) + reinit;
                }else{
                    infoStation +=  duree + reinit;
                }
                if(sommet.getSt().equals(arrivee)){
                    infoStation += "\n"+ sommet.getSt() ;
                }
                iti.insert(0, infoStation + "\n");
                sommet = sommetPrec; //change le sommet courant en son precedent
            }

            System.out.println(iti); //affiche le trajet
            Temps[] res = new Temps[2];
            res[0] = t; //durée totale du trajet
            res[1] = actuel.ajouterTemps(t, false); //heure d'arrivée
            return res;
        } else {
            System.out.println("La station n'existe pas/il n'existe pas de plus court chemin");
            return null;
        }
    }

    public static Temps[] afficher_itineraire_dijkstra_plan3(Graph g, String arrivee, Temps actuel){
        double tempsDeMarcheStations = 0;
        Sommet sommet = g.chercheSommet(arrivee); //sommet d'arrivée
        if(sommet != null){
            StringBuilder iti = new StringBuilder();
            Temps t = sommet.getTemps(); //temps total

            while(sommet.getPrecedent() != null){ //ajouter au string les informations tant que le sommet courant a un precedent
                Edge prec = sommet.getPrecedent();
                String infoStation = "";
                Sommet sommetPrec = g.chercheSommet(prec.getSt_dep().getNom());
                if(sommet.getMarche() != null){
                    if(sommetPrec.getPrecedent() != null ){
                        infoStation += sommetPrec.getPrecedent().getSt_dest().getNom();
                    }
                    tempsDeMarcheStations = g.calcul_temps_de_marche(prec.getSt_dep().getX(),
                          prec.getSt_dep().getY(), prec.getSt_dest().getX(), prec.getSt_dest().getY());

                    if (tempsDeMarcheStations < (g.convertirTempsEnHours(sommet.getMarche()) + g.convertirTempsEnHours(sommet.getAttente()))){
                        infoStation += "\n Marcher à pied de "+ bleu +prec.getSt_dep()+ reinit +" à "+ bleu +prec.getSt_dest()+ reinit +" \n";
                        // modifier le temps total de trajet
                        t = g.convertToHMS(g.convertirTempsEnHours(t) - (g.convertirTempsEnHours(sommet.getMarche()) + g.convertirTempsEnHours(sommet.getAttente())) + tempsDeMarcheStations);
                    }
                    else {
                        infoStation += vert + "\nChangement de ligne: "
                                + bleu + sommetPrec.getPrecedent().getLigne() + reinit + " => " + bleu + prec.getLigne() + reinit;
                        infoStation += jaune + "\nTemps de marche estimé: " + reinit + sommet.getMarche();

                        if(sommet.getAttente() != null){
                            infoStation += jaune + "\nTemps d'attente estimé: " + reinit + sommet.getAttente() + "\n";
                        }
                        tempsDeMarcheStations = 0; // pour ne pas afficher la duree de la marche
                    }
                }
                Temps duree =  new Temps(prec.getDuree());
                if (tempsDeMarcheStations == 0){ // dans ce cas il affiche la station
                    infoStation += prec.getSt_dep().getNom() + "\n | Ligne " + bleu + prec.getLigne() + reinit + " Durée " + vert;
                    if(duree.getHeure() == 0){
                        infoStation +=  String.format("%02dm %02ds", duree.getMinute(), duree.getSeconde()) + reinit;
                    }else{
                        infoStation +=  duree + reinit;
                    }
                }
                else{ // affiche que la durée de la marche
                    infoStation += " Durée " + vert + g.convertToHMS(tempsDeMarcheStations) + reinit;
                    tempsDeMarcheStations = 0;
                }

                if(sommet.getSt().equals(arrivee)){
                    infoStation += "\n"+ sommet.getSt() ;
                }
                iti.insert(0, infoStation + "\n");
                sommet = sommetPrec; //change le sommet courant en son precedent
            }
            System.out.println(iti); //affiche le trajet
            Temps[] res = new Temps[2];
            res[0] = t; //durée totale du trajet
            res[1] = actuel.ajouterTemps(t, false); //heure d'arrivée
            return res;
        } else {
            System.out.println("La station n'existe pas/il n'existe pas de plus court chemin");
            return null;
        }
    }

    public static void marcher_de_xy_a_station(String x, String y, String station, Temps duree){
        System.out.println(bleu + "Marcher à pied de " + reinit + "(" + x + "; " + y + ")"
                + bleu + " à " + reinit + station
                + bleu + " | Temps estimé: " + reinit + duree);
    }

    public static void marcher_de_station_a_xy(String x, String y, String station, Temps duree){
        System.out.println(bleu + "Marcher à pied de " + reinit + station
                + bleu + " à " + reinit + "(" + x + "; " + y + ")"
                + bleu + " | Temps estimé: " + reinit + duree);
    }
}
