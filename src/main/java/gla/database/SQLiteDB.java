package gla.database;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import gla.classes.Station;
import gla.classes.Temps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class SQLiteDB {
    private Connection conn = null;

    public SQLiteDB(){
    }

    public int connexion(){
        //créer la connexion à la base de données
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:data.db");
            return 0;
        } catch (SQLException |ClassNotFoundException e) {
            //e.printStackTrace();
            return -1;
        }
    }

    public int connexionTest(){
        //créer la connexion à la base de données en mémoire(pour les tests)
        try {
            conn = DriverManager.getConnection("jdbc:sqlite::memory:");
            return 0;
        } catch (SQLException e) {
            //e.printStackTrace();
            return -1;
        }
    }

    public void close(){
        try {
            if(conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //utiliser la database GLA ou la créer si elle n'existe pas
    public void lier_gla(){
        try {
            // ...
            Statement stmt = conn.createStatement();
            //créer la base de données au nom de gla
            stmt.executeUpdate("CREATE DATABASE " + "gla");

            conn.setCatalog("gla"); // USE GLA
        } catch(SQLException e) {
            try {
                conn.setCatalog("gla"); // USE GLA
            } catch (SQLException ex) {
                //ex.printStackTrace();
            }
        }
    }

    public int init_tables(){
        try {
            Statement stmt = conn.createStatement();
            //créer les tables
            BufferedReader br = new BufferedReader(new FileReader("src/main/java/gla/database/tables.sql", StandardCharsets.UTF_8));
            String line;
            StringBuilder sql = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sql.append(line);
            }
            br.close();
            String [] exc = sql.toString().split(";");
            for (String s :exc){
                stmt.executeUpdate(s);
            }
        } catch(SQLException | IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public int insert_station(String nom, String x, String y){
        String nom_sans_accents = Normalizer.normalize(nom, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String nom_sans_majuscule = nom_sans_accents.toLowerCase();
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM stations WHERE nom=\""+nom +"\" AND x=\""+x+"\" AND y=\""+y+"\";";
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()){
                //la station n'existe pas déjà
                String sql2 = "INSERT INTO stations (nom, x, y, nom_bis) VALUES (\""+ nom +"\",\"" + x + "\",\"" + y + "\",\"" + nom_sans_majuscule + "\");" ;
                stmt.executeUpdate(sql2);
                res = stmt.executeQuery(sql);
                if(res.next()){
                    return res.getInt("id");
                }else{
                    System.out.println("-1");
                    return -1;
                }
            }
            return res.getInt("id");
        } catch (SQLException e) {
            System.out.println("station " + e.getMessage());
            return -1;
        }
    }

    public void insert_ligne(String [] variante){
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM lignes WHERE nom='"+ variante[0] +"' AND variante='" + variante[1] + "';";
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()){
                sql = "INSERT INTO lignes (nom, variante) VALUES ('"+variante[0]+ "','" + variante[1]+"');" ;
                stmt.executeUpdate(sql);
            }
        }catch (SQLException e) {
            System.out.println("ligne " +e.getMessage());
        }
    }

    public void insert_arete(int stationA, int stationB, float distance, String temps, String [] variante){
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM aretes WHERE nom=\""+ variante[0] +"\" AND variante=\"" + variante[1] +"\" AND stationA="+stationA +" AND stationB="+ stationB +  ";";
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()){
                sql = "INSERT INTO aretes (stationA, stationB, distanceAB, tempsAB, nom, variante) VALUES ("+stationA+","+stationB+","+distance+",\""+temps+"\",\""+variante[0]+"\",\""+variante[1]+"\");" ;
                stmt.executeUpdate(sql);
            }
        }catch (SQLException e) {
            System.out.println("arete " + e.getMessage());
        }
    }

    public void insert_heure(String ligne, String station, String heure, String minutes, String variante){
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM horaires WHERE ligne=\"" + ligne + "\" AND station=\"" + station + "\" AND heure=\"" + heure + "\" AND minutes=\"" + minutes + "\" AND variante=\"" + variante + "\";";
            ResultSet res = stmt.executeQuery(sql);
            if(!res.next()){
                sql = "INSERT INTO horaires (ligne, station, heure, minutes, variante) VALUES (\""+ligne+"\",\""+station+"\",\""+heure+"\",\""+minutes+"\",\""+variante+"\");" ;
                stmt.executeUpdate(sql);
            }
        }catch (SQLException e) {
            System.out.println("heure " + e.getMessage());
        }
    }

    public int lire_csv (String chemin)  {
        try {
            FileReader freader = new FileReader(chemin);
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(freader).withCSVParser(parser).build();
            List<String[]> lignes = csvReader.readAll();
            for (String[] info : lignes) {
                String [] variante = info[4].split("variant");
                for(int i = 0; i<variante.length; i++){
                    variante[i] = variante[i].trim();
                }
                String [] coordonneeA = info[1].split(",");
                for(int i = 0; i<coordonneeA.length; i++){
                    coordonneeA[i] = coordonneeA[i].trim();
                }
                String [] coordonneeB = info[3].split(",");
                for(int i = 0; i<coordonneeB.length; i++){
                    coordonneeB[i] = coordonneeB[i].trim();
                }
                int idA = insert_station(info[0], coordonneeA[0],coordonneeA[1]);
                int idB = insert_station(info[2], coordonneeB[0],coordonneeB[1]);
                if(idB != -1 && idA != -1){
                    insert_ligne(variante);
                    insert_arete(idA,idB,Float.parseFloat(info[6]), info[5],variante);
                }
            }
        } catch (IOException | CsvException e) {
            return -1;
        }
        return 0;
    }

    public int lire_csv_timetables (String chemin)  {
        try {
            FileReader freader = new FileReader(chemin);
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(freader).withCSVParser(parser).build();
            List<String[]> horaires = csvReader.readAll();
            for (String[] infos : horaires) {
                String[] heure = infos[2].split(":");
                for (int i = 0; i < heure.length; i++) {
                    heure[i] = heure[i].trim();
                }
                insert_heure(infos[0], infos[1], heure[0], heure[1], infos[3]);

            }
        } catch (IOException | CsvException e) {
            System.out.println(e.getMessage());
            return -1;
        }
        return 0;
    }
    public boolean ligne_existe(String ligne){
        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM lignes WHERE nom="+ligne +";";
            ResultSet res = stmt.executeQuery(sql);
            return res.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public ResultSet station_existe(String station){
        try {
            String station_sans_accent = Normalizer.normalize(station, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            String station_sans_majuscule = station_sans_accent.toLowerCase();

            Statement st = conn.createStatement();
            String sql = "SELECT * FROM stations WHERE nom_bis LIKE \"" + station_sans_majuscule + "%\";";
            ResultSet res = st.executeQuery(sql);

            if(res.next()) return res;
            else return null;
        } catch (SQLException e){
            return null;
        }
    }

    public ResultSet getStation(int id) {
        try{
            Statement stm =conn.createStatement();
            String sql = "SELECT * FROM stations WHERE id =\""+id+ "\";";
            return stm.executeQuery(sql);
        } catch (SQLException e) {
            return null;
        }

    }

    public ResultSet lignes_station(String station){
        try{
            Statement stm =conn.createStatement();
            String sql = "SELECT DISTINCT aretes.nom,aretes.variante FROM aretes, stations WHERE stations.nom =\""+station+ "\" and (aretes.stationA = stations.id or aretes.stationB = stations.id);";
            return stm.executeQuery(sql);
        } catch (SQLException e) {
            //System.out.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<Station> recherche_station(ResultSet res_station) throws SQLException {
        ArrayList<Station> stations = new ArrayList<>();
        while (res_station.next()) {
            Station a_ajouter = new Station(res_station.getString("nom"));

            //les lignes qui passent par cette station
            ResultSet res_ligne_station = lignes_station(res_station.getString("nom"));
            while(res_ligne_station.next()){
                a_ajouter.ajouter_lignes(res_ligne_station.getString("nom"));
            }
            //on ajoute la station si elle n'y est pas déjà
            boolean dedans = false;
            for(Station s : stations){
                if(s.getNom().equals(a_ajouter.getNom())){
                    dedans = true;
                    break;
                }
            }
            if(!dedans) stations.add(a_ajouter);
        }
        return stations;
    }
    public ResultSet selectVariante(String ligne){
        try {
            Statement st = conn.createStatement();
            String sql="select distinct(variante) from aretes where nom=\""+ligne+"\";";
            return st.executeQuery(sql);
        }catch( SQLException e){
            //System.out.println(e.getMessage());
            return null;
        }
    }

    public ResultSet stationDepart(String ligne,String variante){
        try{
            Statement stm = conn.createStatement();
            String sql="select nom from stations, (select stationA from aretes where stationA not in ( select stationB from aretes where nom=\""+ligne+"\" and variante =\""+variante+"\") and nom=\""+ligne+"\" and variante=\""+variante+"\") as A where id=stationA;";
            return stm.executeQuery(sql);
        }catch (SQLException e){
            //System.out.println(e.getMessage());
            return null;
        }
    }

    public ResultSet getHeure(String ligne, String station, String variante) {
        try {
            String sql = "SELECT heure, minutes FROM horaires WHERE ligne = ? AND station = ? AND variante =  ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, ligne);
            stm.setString(2, station);
            stm.setString(3, variante);
            return stm.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public ResultSet getVariantesForStationAndLigne(String station, String ligne) {

        try {
            String query = "SELECT distinct lignes.variante FROM lignes " +
                    "INNER JOIN aretes ON lignes.nom = aretes.nom AND lignes.variante = aretes.variante " +
                    "INNER JOIN stations ON stations.id = aretes.stationA OR stations.id = aretes.stationB " +
                    "WHERE stations.nom = ? AND lignes.nom = ?";

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, station);
            statement.setString(2, ligne);
            return statement.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    public Temps calculateTime(String stationX, String ligne, String variante, String departStation) throws SQLException {
        Temps totalTime = new Temps(0,0,0);

        // prepare the SQL query to retrieve the time between stations
        String sql;
        PreparedStatement stmt;

        // initialize the current station to the departure station
        String currentStation = departStation;

        // loop until we reach the destination station
        while (!currentStation.equals(stationX)) {
            // get the next station in the line
            sql = "SELECT sB.nom, a.tempsAB FROM aretes a " +
                    "JOIN stations sA ON a.stationA = sA.id " +
                    "JOIN stations sB ON a.stationB = sB.id " +
                    "WHERE sA.nom = ? AND a.nom = ? AND a.variante = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, currentStation);
            stmt.setString(2, ligne);
            stmt.setString(3, variante);
            ResultSet rs = stmt.executeQuery();
            // iterate through the results to find the next station
            while (rs.next()) {
                String nextStation = rs.getString("nom");
                Temps time = new Temps(rs.getString("tempsAB"));
                totalTime=totalTime.ajouterTemps(time,false);
                currentStation = nextStation;

            }
        }
        return totalTime;
    }

    public ResultSet stationArriver(String ligne,String variante){
        try{
            Statement stm = conn.createStatement();
            String sql="select nom from stations, (SELECT stationB FROM aretes WHERE stationB NOT IN (SELECT stationA FROM aretes WHERE nom=\""+ligne+"\" AND variante = \""+variante+"\") AND nom=\""+ligne+"\" AND variante =\""+variante+"\") as B where id=stationB;";
            return stm.executeQuery(sql);
        }catch (SQLException e){
            //System.out.println(e.getMessage());
            return null;
        }
    }

    public ResultSet nextStation(String stationA,String ligne,String variante){
        try{
            Statement stm = conn.createStatement();
            String sql="select nom from stations, (SELECT stationB FROM aretes as A, stations as S WHERE S.nom=\""+stationA+"\" and stationA=id and A.nom=\""+ligne+"\" AND variante=\""+variante+"\") as B where id=stationB ;";
            return stm.executeQuery(sql);
        }catch (SQLException e){
            //System.out.println(e.getMessage());
            return null;
        }
    }

    // UNE METHODE QUI RETOURNE TOUS LES ARETES DONT STATION DE DEPART EST stA
    public ResultSet list_arete_start_at_stA(String stA){
        try{
            Statement stm = conn.createStatement();
            String sql="select distinct S2.nom as stationB, distanceAB, tempsAB, B.nom, variante, x as B_x, y as B_y, A_x, A_y  from stations as S2, (SELECT Distinct stationB, distanceAB, tempsAB,A.nom, variante, S.x as A_x, S.y as A_y FROM aretes as A, stations as S WHERE S.id=stationA and S.nom=\""+stA+"\") as B where S2.id=B.stationB;";
            return stm.executeQuery(sql);
        }catch (SQLException e){
            //System.out.println(e.getMessage());
            return null;
        }
    }

    public ResultSet listArretesParLigneVariante(String ligne, String variante) {
        try{
            Statement stm = conn.createStatement();
            String sql="SELECT * from aretes where nom=\""+ligne+ "\" AND variante =\""+variante+"\";";
            return stm.executeQuery(sql);
        }catch (SQLException e){
            return null;
        }
    }

    // verifier si la station existe
    public boolean station_exist(String station){
        try {
            String station_sans_accent = Normalizer.normalize(station, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            String station_sans_majuscule = station_sans_accent.toLowerCase();

            Statement st = conn.createStatement();
            String sql = "SELECT * FROM stations WHERE nom_bis LIKE \"%" + station_sans_majuscule + "%\";";
            ResultSet res = st.executeQuery(sql);

            if(res.next()) return true;
            else return false;
        } catch (SQLException e){
            return false;
        }
    }

    public ResultSet Variantes(String ligne,String stationAr){
        try{
            Statement stmn =conn.createStatement();
            String sql="SELECT variante from aretes as A, stations as S where id=stationB AND S.nom=\""+stationAr+"\" AND A.nom=\""+ligne+"\";";
            return stmn.executeQuery(sql);
        }catch (SQLException e){
            return null;
        }
    }

    // retourne toutes les stations
    public ResultSet getAllStation(){
        try{
            Statement stm = conn.createStatement();
            String sql="select * from stations ;";
            return stm.executeQuery(sql);
        }catch (SQLException e){
            //System.out.println(e.getMessage());
            return null;
        }
    }

    // vérifier si une coordonnée corespend à une station
    public int is_it_station(double x, double y) throws SQLException {
        ResultSet res = getAllStation();
        while (res.next()){
            if (x == res.getDouble("x") & y == res.getDouble("y"))
                return res.getInt("id");
        }
        return -1;
    }

}
