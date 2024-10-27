package gla.classes;

import java.util.ArrayList;
import java.util.Objects;

public class Station {
	
	private String nom;
	private double x;
	private double y;
	private ArrayList<String> lignes = new ArrayList<>();

	public Station(String n, double X, double Y) {
		nom = n;
		x = X;
		y = Y;
	}

	public Station(String n){
		nom = n;
		x = 0;
		y = 0;
	}
	
	public Station() {
		nom = null;
		x = 0;
		y = 0;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public String getNom() {
		return nom;
	}
	
	public String toString() {
		return nom;
	}
	
	public void setNom(String n) {
		nom= n;
	}
	
	public void setX(double x) {
		this.x= x;
	}
	
	public void setY(double y) {
		this.y= y;
	}

	public ArrayList<String> getLignes(){ return lignes; }

	public void ajouter_lignes(String l){
		boolean dedans = false;
		for(String s : lignes){
			if(s.equals(l)){
				dedans = true;
				break;
			}
		}
		if(!dedans){
			lignes.add(l);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Station station = (Station) obj;
		return Double.compare(station.x, x) == 0 && Double.compare(station.y, y) == 0 && nom.equals(station.nom);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nom, x, y);
	}
}
