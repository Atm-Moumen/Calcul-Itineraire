package gla.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; 
import java.util.Map;

import gla.database.SQLiteDB;

public class StationLigne {
	
	private Map<Station, ArrayList<LigneVariante>> map;
	private SQLiteDB bdd;
	
	public StationLigne(Map<Station, ArrayList<LigneVariante>> m, SQLiteDB b) {
		map= m;
		bdd= b;
	}
	
	public Map<Station, ArrayList<LigneVariante>> getMap(){
		return map;
	}
	
	public void addElement(Station s, ArrayList<LigneVariante> m) {
		map.put(s, m);
	}
	
	public void setMap(Map<Station, ArrayList<LigneVariante>> map2) {
		map.clear();
		map.putAll(map2);
	}
	
	public ArrayList<LigneVariante> getLignes(Station depart)  throws SQLException {
		boolean appartient= false;
		Station copie= null;
		for(Station stat: map.keySet()) {
        	if(stat.getNom().equals(depart.getNom())) {
        		appartient= true;
        		copie= stat;
        		break ;
        	}
        }
		if(appartient== true) {
			return map.get(copie);
		}
		else {
			ResultSet res1= bdd.lignes_station(depart.getNom());
			ArrayList<LigneVariante> tab_depart= new ArrayList<LigneVariante>();
			while(res1.next()) { // je recup toutes les lignes avec les variantes (nom ligne + nom variante) de res1
            	ResultSet arretes= bdd.listArretesParLigneVariante(res1.getString("nom"), res1.getString("variante"));
            	ArrayList<Arete> array= new ArrayList<Arete>(); // toutes les arêtes de la ligne avec variante
            	while(arretes.next()) {
            		int idStationA= arretes.getInt("stationA");
            		ResultSet SetStation= bdd.getStation(idStationA);
            		String nomStationA= null;
            		String xStationA= null, yStationA= null;
            		if(SetStation.next()) {
            			nomStationA= SetStation.getString("nom");
            			xStationA= SetStation.getString("x");
            			yStationA= SetStation.getString("y");
            		}
            		int idStationB= arretes.getInt("stationB");
            		ResultSet SetStationB= bdd.getStation(idStationB);
            		String nomStationB= null;
            		String xStationB= null, yStationB= null;
            		if(SetStationB.next()) {
            			nomStationB= SetStationB.getString("nom");
            			xStationB= SetStationB.getString("x");
            			yStationB= SetStationB.getString("y");
            		}
            		array.add(new Arete(new Station(nomStationA, Float.parseFloat(xStationA), Float.parseFloat(yStationA)), new Station(nomStationB, Float.parseFloat(xStationB), Float.parseFloat(yStationB)), Float.valueOf(arretes.getString("distanceAB")), arretes.getString("tempsAB")));
            	}
            	// je construit pour chaque ligne (aves sa variante) ses arrêtes
            	tab_depart.add(new LigneVariante(res1.getString("nom"), res1.getString("variante"), array)); // on stocke toutes les Lignes (avec sa variante) qui passe par la station donnée
            }
			map.put(depart, tab_depart);
			return map.get(depart);
		}
	}
	
	public String toString() {
		String res= "";
		for(Map.Entry<Station, ArrayList<LigneVariante>> entre: map.entrySet()) {
			res+= entre.getKey() + ": [";
			for(LigneVariante ligne: entre.getValue()) {
				res+= "(" + ligne.getNom() + " , " + ligne.getNumVariante() + ") ";
			}
			res+= "]\n";
		}
		return res;
	}
}
