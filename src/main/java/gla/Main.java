package gla;

import gla.classes.*;
import gla.database.SQLiteDB;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static SQLiteDB bdd;

    public static int init_bd(){
        bdd = new SQLiteDB();
        //tentative de connexion à la base de donnée
        if(bdd.connexion() == -1) {
            Affichage.erreur_connexion_bd();
            return -1;
        } else {
            Affichage.succes_connexion_bd();
            //utiliser la database GLA ou la créer si elle n'existe aps
            bdd.lier_gla();

            //on demande si on doit lire/relire le fichier csv
            String lire_csv = "";
            System.out.println("Lire le fichier csv?");
            while(!lire_csv.equalsIgnoreCase("o") && !lire_csv.equalsIgnoreCase("O")
                    && !lire_csv.equalsIgnoreCase("n") && !lire_csv.equalsIgnoreCase("N")){
                System.out.print("Veuillez répondre par oui (o/O) ou non (n/N): ");
                lire_csv = scanner.nextLine();
            }

            if(lire_csv.equalsIgnoreCase("o") || lire_csv.equalsIgnoreCase("O")){
                //lire le fichier csv
                Affichage.lecture_csv_en_cours();
                if(bdd.init_tables() == -1){
                    Affichage.erreur_init_tables_sql();
                    return -1;
                } else {
                    if((bdd.lire_csv("src/main/java/gla/database/map_data.csv") == -1)||(bdd.lire_csv_timetables("src/main/java/gla/database/timetables.csv") == -1)) {
                        Affichage.lecture_csv_erreur();
                        return -1;
                    }
                    Affichage.lecture_csv_ok();
                }
            }
        }
        return 0;
    }

    public static String input_non_vide(){
        String res = "";
        while (scanner.hasNextLine()) {
            res = scanner.nextLine();
            if(!res.equalsIgnoreCase("")) break;
        }
        return res;
    }

    public static int int_non_vide(){
        String tmp = "";
        int res = -1;
        boolean int_ok = false;
        while(!int_ok){
            tmp = input_non_vide();
            try {
                res = Integer.parseInt(tmp);
                int_ok = true;
            } catch (Exception e){
                Affichage.choix_incorrect_menu();
            }
        }
        return res;
    }

    public static double double_non_vide(String s){
        System.out.print(s);
        String tmp = "";
        double res = -1;
        boolean double_ok = false;
        while(!double_ok){
            tmp = input_non_vide();
            try {
                res = Double.parseDouble(tmp);
                double_ok = true;
            } catch (Exception e){
                Affichage.erreur_non_float();
            }
        }
        return res;
    }

    public static String[] demande_station_depart_arrivee(){
        System.out.println("Quelle est le nom de la station de départ ?");
        Station station_depart = menu_recherche_station();
        if(station_depart == null){
            Affichage.erreur_recherche_itineraire_dijkstra();
        } else {
            //pas de problème la station de départ existe
            System.out.println("Quelle est le nom de la station d'arrivée ?");
            Station station_arrivee = menu_recherche_station();
            if (station_arrivee == null) {
                Affichage.erreur_recherche_station();
            } else {
                String[] res = new String[2];
                res[0] = station_depart.getNom();
                res[1] = station_arrivee.getNom();
                return res;
            }
        }
        return null;
    }

    public static void menu() {
        while(true) {
            Affichage.menu();
            int choix = int_non_vide();
            while (choix < 1 || choix > 6) {
                Affichage.choix_inexistant_menu();
                choix = int_non_vide();
            }
            switch (choix) {
                case 1:
                    System.out.println("Quel est le nom de la station recherchée?");
                    //on récupère la station demandée
                    Station station_demandee = menu_recherche_station();
                    if(station_demandee == null){
                        //on affiche un message d'erreur si elle est nulle
                        Affichage.erreur_recherche_station();
                    } else {
                        //on affiche les détails de la station sinon
                        Affichage.afficher_une_station(station_demandee);
                    }
                    break;

                case 2:
                    menu_recherche_ligne();
                    break;

                case 3:
                    menu_horaires();
                    break;

                case 4:
                    String[] stations_p0 = demande_station_depart_arrivee();
                    if(stations_p0 != null){
                        menu_recherche_itineraire_plan_0(stations_p0[0], stations_p0[1]);
                    } else {
                        Affichage.erreur_recherche_station();
                    }
                    break;
                case 5:
                    menu_recherche_coord_ou_station();
                    break;
                case 6:
                    //retourner au main pour terminer le programme
                    return;
                default:
                    break;
            }
        }
    }

    //permet à l'utilisateur d'avoir des informations sur une station, quelles lignes passent par cette station
    public static Station menu_recherche_station(){
        try {
            String station_demandee = input_non_vide();
            //on boucle jusqu'à ce que la station demandée est une station existante
            ResultSet res = bdd.station_existe(station_demandee);
            while (res == null) {
                Affichage.station_ou_ligne_inexistante("station");
                station_demandee = input_non_vide();
                res = bdd.station_existe(station_demandee);
            }
            //on récupère toutes les stations récupérées à partir du string de la station demandée (par motif)
            ArrayList<Station> liste_stations = bdd.recherche_station(res);
            if (liste_stations.size() == 0) {
                return null;
            } else if(liste_stations.size() == 1) {
                //si il y en a que une on la retourne directement
                return liste_stations.get(0);
            } else {
                //si il y en a plusieurs on demande laquelle l'utilisateur veut
                Affichage.demander_choix_stations(liste_stations);
                int choix_station = int_non_vide();
                while (choix_station < 0 || choix_station >= liste_stations.size()) {
                    Affichage.choix_inexistant_menu();
                    choix_station = int_non_vide();
                }
                //puis on la retourne
                return liste_stations.get(choix_station);
            }
        } catch (SQLException e) {
            Affichage.erreur_recherche_station();
        }
        return null;
    }

    //permet à l'utilisateur de connaître toutes les variantes d'une ligne, et les stations par lesquelles elles passent
    public static void menu_recherche_ligne(){
        try {
            System.out.println("Quel est le numéro de la ligne recherchée?");
            String ligne_demandee = input_non_vide();
            //on boucle tant que la ligne demandée n'est pas une ligne existante
            while (!bdd.ligne_existe(ligne_demandee)) {
                Affichage.station_ou_ligne_inexistante("ligne");
                ligne_demandee = input_non_vide();
            }
            //on récupère ensuite toutes les variantes de la ligne demandée
            List<String> variantes = new ArrayList<>();
            ResultSet liste_variantes = bdd.selectVariante(ligne_demandee);
            while (liste_variantes.next()) {
                variantes.add(liste_variantes.getString("variante"));
            }
            //on affiche la liste des variantes
            Affichage.liste_variantes(ligne_demandee, variantes);

            //on affiche chaque variante
            for (String variante : variantes) {
                //on récupère la station de départ de la variante
                ResultSet res_depart = bdd.stationDepart(ligne_demandee, variante);
                String depart = null;
                while (res_depart.next()) {
                    depart = res_depart.getString("nom");
                }
                //on récupère la station d'arrivée de la variante
                ResultSet res_arrivee = bdd.stationArriver(ligne_demandee, variante);
                String arrivee = null;
                while (res_arrivee.next()) {
                    arrivee = res_arrivee.getString("nom");
                }
                //on affiche les infos de la ligne pour cette variante, station de départ et d'arrivée
                Affichage.infos_ligne(ligne_demandee, variante, depart, arrivee);

                //on crée une liste de station ordonnée
                LinkedList<String> liste_stations = new LinkedList<>();
                int i = 0;
                //la première station est celle de départ
                liste_stations.add(depart);
                boolean test = true;
                while (test) {
                    //on récupère la/les station(s) suivante(s)
                    ResultSet res_station_suivante = bdd.nextStation(liste_stations.get(i), ligne_demandee, variante);
                    if (res_station_suivante.next()) {
                        String station_suivante = res_station_suivante.getString("nom");
                        //tant que la station suivante n'est pas la station d'arrivée, on l'ajoute à la liste
                        if (station_suivante != arrivee) {
                            i = i + 1;
                            liste_stations.add(station_suivante);
                            //si c'est la station d'arrivée, on arrête la boucle
                            if (station_suivante.equals(arrivee)) {
                                test = false;
                            }
                        }
                    }
                }
                //on affiche la liste des stations dans l'ordre
                Affichage.liste_stations(liste_stations);
            }

        } catch (SQLException e){
            Affichage.station_ou_ligne_inexistante("ligne");
        }
    }

    //demande à l'utilisateur quelle ligne il veut de cette station et renvoie le nom de la ligne
    public static String demande_ligne_de_station(Station station){
        String res = "";
        Affichage.afficher_lignes(station, false);
        boolean ok = false;
        System.out.print("Pour quelle ligne souhaitez-vous les horaires? ");
        while(!ok){
            res = input_non_vide();
            if(station.getLignes().contains(res)) ok = true;
            else {
                Affichage.erreur_ligne_incorrecte();
            }
        }
        System.out.println();
        return res;
    }

    public static void menu_horaires(){
        try {
            System.out.println("Pour quelle station voulez-vous voir les horaires?");
            //on récupère la station demandée
            Station station_demandee = menu_recherche_station();
            if(station_demandee == null){
                //on affiche un message d'erreur si elle est nulle
                Affichage.erreur_recherche_station();
            } else {
                //on demande la ligne pour laquelle l'utilisateur veut les horaires
                String ligne_demandee = demande_ligne_de_station(station_demandee);

                // on récupère toutes les variantes dans lesquelles la station appartient à la ligne
                ResultSet rs = bdd.getVariantesForStationAndLigne(station_demandee.getNom(),ligne_demandee);
                if (!rs.next()) {
                    Affichage.erreur_variante_non_trouvee(station_demandee.getNom(), ligne_demandee);
                } else {
                    do {
                        String variante = rs.getString("variante");
                        // on récupère la station de depart de la variante
                        String stationDepart = bdd.stationDepart(ligne_demandee, variante).getString("nom");
                        Affichage.variante(variante);
                        // on calcule le temps nécessaire pour aller d'une station de départ jusqu'à la station demandée
                        Temps tempsAjoute = bdd.calculateTime(station_demandee.getNom(),ligne_demandee,variante,stationDepart);
                        // on affiche les horaires des stations
                        ResultSet rs2 = bdd.getHeure(ligne_demandee,stationDepart, variante);
                        TreeSet<Temps>  horairesOrdonnees = new TreeSet<>();
                        while (rs2.next()){
                            Temps horaire = new Temps(Integer.parseInt(rs2.getString("heure")),Integer.parseInt(rs2.getString("minutes")),0);
                            horairesOrdonnees.add(horaire.ajouterTemps(tempsAjoute,false));
                        }
                        for (Temps horaires:horairesOrdonnees){
                            System.out.print(horaires + "    " );
                        }
                        System.out.println();
                    }while (rs.next());
                }
            }

        } catch(Exception e){
            Affichage.erreur_sql();
        }
    }

    public static void menu_recherche_itineraire_plan_0(String station_depart, String station_arrivee){
        try {
            ResultSet res1 = bdd.lignes_station(station_depart);
            ResultSet res2 = bdd.lignes_station(station_arrivee);
            ArrayList<LigneVariante> tab_depart = new ArrayList<LigneVariante>();
            ArrayList<LigneVariante> tab_arrivee = new ArrayList<LigneVariante>();
            Map<Station, ArrayList<LigneVariante>> m = new HashMap<Station, ArrayList<LigneVariante>>();
            while (res1.next()) { // je recup toutes les lignes avec les variantes (nom ligne + nom variante) de res1
                ResultSet arretes = bdd.listArretesParLigneVariante(res1.getString("nom"), res1.getString("variante"));
                ArrayList<Arete> array = new ArrayList<Arete>(); // toutes les arêtes de la ligne avec variante
                while (arretes.next()) {
                    int idStationA = arretes.getInt("stationA");
                    ResultSet SetStation = bdd.getStation(idStationA);
                    String nomStationA = null;
                    String xStationA = null, yStationA = null;
                    if (SetStation.next()) {
                        nomStationA = SetStation.getString("nom");
                        xStationA = SetStation.getString("x");
                        yStationA = SetStation.getString("y");
                    }
                    int idStationB = arretes.getInt("stationB");
                    ResultSet SetStationB = bdd.getStation(idStationB);
                    String nomStationB = null;
                    String xStationB = null, yStationB = null;
                    if (SetStationB.next()) {
                        nomStationB = SetStationB.getString("nom");
                        xStationB = SetStationB.getString("x");
                        yStationB = SetStationB.getString("y");
                    }
                    array.add(new Arete(new Station(nomStationA, Float.parseFloat(xStationA), Float.parseFloat(yStationA)), new Station(nomStationB, Float.parseFloat(xStationB), Float.parseFloat(yStationB)), Float.valueOf(arretes.getString("distanceAB")), arretes.getString("tempsAB")));
                }
                // je construit pour chaque ligne (aves sa variante) ses arrêtes
                tab_depart.add(new LigneVariante(res1.getString("nom"), res1.getString("variante"), array)); // on stocke toutes les Lignes (avec sa variante) qui passe par la station donnée
            }
            while (res2.next()) { // je recup toutes les lignes variantes (leur noms) de res2
                ResultSet arretes = bdd.listArretesParLigneVariante(res2.getString("nom"), res2.getString("variante"));
                ArrayList<Arete> array = new ArrayList<Arete>(); // toutes les arêtes de la ligne avec variante
                while (arretes.next()) {
                    int idStationA = arretes.getInt("stationA");
                    ResultSet SetStation = bdd.getStation(idStationA);
                    String nomStationA = null;
                    String xStationA = null, yStationA = null;
                    if (SetStation.next()) {
                        nomStationA = SetStation.getString("nom");
                        xStationA = SetStation.getString("x");
                        yStationA = SetStation.getString("y");
                    }
                    int idStationB = arretes.getInt("stationB");
                    ResultSet SetStationB = bdd.getStation(idStationB);
                    String nomStationB = null;
                    String xStationB = null, yStationB = null;
                    if (SetStationB.next()) {
                        nomStationB = SetStationB.getString("nom");
                        xStationB = SetStationB.getString("x");
                        yStationB = SetStationB.getString("y");
                    }
                    array.add(new Arete(new Station(nomStationA, Float.parseFloat(xStationA), Float.parseFloat(yStationA)), new Station(nomStationB, Float.parseFloat(xStationB), Float.parseFloat(yStationB)), Float.valueOf(arretes.getString("distanceAB")), arretes.getString("tempsAB")));
                }
                tab_arrivee.add(new LigneVariante(res2.getString("nom"), res2.getString("variante"), array));
            }
            Station s = new Station(station_depart, 0, 0);
            Station s2 = new Station(station_arrivee, 0, 0);
            m.put(s, tab_depart);
            m.put(s2, tab_arrivee);
            StationLigne station_ligne = new StationLigne(m, bdd);
            Itineraire itineraire = new Itineraire(station_ligne);
            itineraire.trajetV0(s, s2);

        } catch (Exception e){
            Affichage.erreur_recherche_itineraire_plan_0();
        }
    }

    public static void menu_recherche_coord_ou_station(){
        try{
            Graph gr = new Graph();
            String res1 = "";
            double x_depart = -1;
            double y_depart = -1;
            int id_st_depart = -1;
            String nom_st_depart = "";
            double x_arrivee = -1;
            double y_arrivee = -1;
            int id_st_arrive = -1;
            String nom_st_arrivee = "";
            System.out.println("Votre départ est-elle une station ou des coordonnées?");
            while(!res1.equalsIgnoreCase("c") && !res1.equalsIgnoreCase("s")){
                System.out.println("Répondre par s pour station et c pour coordonnées.");
                res1 = input_non_vide();
            }
            if(res1.equalsIgnoreCase("c")){
                x_depart = double_non_vide("Entrez la coordonnée x de départ: ");
                y_depart = double_non_vide("Entrez la coordonnée y de départ: ");
                //rechercher la station la plus proche des coordonnées de depart
                id_st_depart = gr.trouve_station_plus_proche(bdd, x_depart, y_depart);
                nom_st_depart = bdd.getStation(id_st_depart).getString("nom");
            }else{
                System.out.println("Quelle est le nom de la station de départ ?");
                Station station_depart = menu_recherche_station();
                if(station_depart == null) {
                    Affichage.erreur_recherche_itineraire_dijkstra();
                }else{
                    nom_st_depart = station_depart.getNom();
                }
            }
            if(!nom_st_depart.equals("")){
                String res2 = "";
                System.out.println("Votre arrivée est-elle une station ou des coordonnées?");
                while(!res2.equalsIgnoreCase("c") && !res2.equalsIgnoreCase("s")){
                    System.out.println("Répondre par s pour station et c pour coordonnées.");
                    res2 = input_non_vide();
                }
                if(res2.equalsIgnoreCase("c")){
                    x_arrivee = double_non_vide("Entrez la coordonnée x d'arrivée: ");
                    y_arrivee = double_non_vide("Entrez la coordonnée y d'arrivée: ");
                    //rechercher la station la plus proche des coordonnées d'arrivée
                    id_st_arrive = gr.trouve_station_plus_proche(bdd, x_arrivee, y_arrivee);
                    nom_st_arrivee = bdd.getStation(id_st_arrive).getString("nom");
                }else{
                    System.out.println("Quelle est le nom de la station d'arrivée ?");
                    Station station_arrivee = menu_recherche_station();
                    if(station_arrivee == null) {
                        Affichage.erreur_recherche_itineraire_dijkstra();
                    } else {
                        nom_st_arrivee = station_arrivee.getNom();
                    }
                }
                if (!nom_st_arrivee.equals("")){
                    Calendar cal = Calendar.getInstance();
                    Temps actuel = new Temps(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

                    Affichage.afficher_heure_depart(actuel);
                    Temps marche = null;
                    //vérifier s'il commence par marcher à pied

                    if (bdd.is_it_station(x_depart, y_depart) == -1 && x_depart != -1 && y_depart !=-1) {
                        marche = gr.convertToHMS(gr.calcul_temps_de_marche(x_depart, y_depart,

                                bdd.getStation(id_st_depart).getDouble("x"),
                                bdd.getStation(id_st_depart).getDouble("y")));
                        Affichage.marcher_de_xy_a_station(Double.toString(x_depart), Double.toString(y_depart),
                                nom_st_depart, marche);
                    }

                    gr.algorithme_dijkstra(bdd, nom_st_depart, actuel, marche);

                    Affichage.afficher_trajet(nom_st_depart, nom_st_arrivee);
                    Temps[] tmp = Affichage.afficher_itineraire_dijkstra_plan3(gr, nom_st_arrivee, actuel);
                    if(tmp != null) {
                        actuel = tmp[0]; //durée du trajet en métro

                        //vérifier s'il marche à pied à la fin
                        if (bdd.is_it_station(x_arrivee, y_arrivee) == -1 && x_arrivee!= -1 && y_arrivee != -1) {
                            marche = gr.convertToHMS(gr.calcul_temps_de_marche(x_arrivee, y_arrivee,
                                    bdd.getStation(id_st_arrive).getDouble("x"),
                                    bdd.getStation(id_st_arrive).getDouble("y")));
                            Affichage.marcher_de_station_a_xy(Double.toString(x_arrivee), Double.toString(y_arrivee),
                                    nom_st_arrivee, marche);
                            //on ajoute le temps de marche
                            actuel = actuel.ajouterTemps(marche,true);
                            Affichage.afficher_duree_trajet(actuel);
                            Affichage.afficher_heure_arrivee(tmp[1].ajouterTemps(marche, false));
                        } else {
                            Affichage.afficher_duree_trajet(actuel);
                            Affichage.afficher_heure_arrivee(tmp[1]);
                        }
                    }
                }
            }

        } catch (Exception e){
            Affichage.erreur_recherche_itineraire_dijkstra();
        }
    }

    public static void main(String[] args) {
        Affichage.accueil(false);

        if(init_bd() == 0){
            //pas de problème, on continue
            menu();
        }

        if(bdd != null) bdd.close();
        scanner.close();

        Affichage.accueil(true);
    }

}



