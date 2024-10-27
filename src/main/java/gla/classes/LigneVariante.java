package gla.classes;
import java.util.ArrayList;
import java.util.Objects;


public class LigneVariante{
    
	private String nom;
	private String variante;
	private ArrayList<Arete> aretes;
	
	public LigneVariante(String n, String v, ArrayList<Arete> arr) {
		nom= n;
		variante= v;
		aretes= arr;
	}

    public String getNom(){
        return nom;
    }

    public ArrayList<Arete> getAretes(){
    	return aretes;
    }
    
    public String getNumVariante(){
        return variante;
    }
    
    public String afficheLigneVariante() {
    	String res= "";
    	for(Arete arr: aretes) {
    		res= res + "[ " + arr.getStationA() + " -- " + arr.getStationB() + " ] ";
    	}
    	return res;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		LigneVariante that = (LigneVariante) obj;
		return nom.equals(that.nom) && variante.equals(that.variante) && aretes.equals(that.aretes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nom, variante, aretes);
	}
    
}