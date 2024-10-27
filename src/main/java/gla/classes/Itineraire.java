package gla.classes;

import java.sql.SQLException;
import java.util.ArrayList;


public class Itineraire { // classe "graphe"
	
	private StationLigne station_ligne;
	
	public Itineraire(StationLigne sl) {
		station_ligne= sl;
	}
	
	public ArrayList<LigneVariante> RechercheLignesCommun(ArrayList<LigneVariante> LignesStationA, ArrayList<LigneVariante> LignesStationB) {
		ArrayList<LigneVariante> communs= new ArrayList<LigneVariante>();
		for(LigneVariante l: LignesStationA) {
			for(LigneVariante l2: LignesStationB) {
				if(l.getNom().equals(l2.getNom()) && l.getNumVariante().equals(l2.getNumVariante())) {
					communs.add(l2);
					break ;
				}
			}
		}
		return communs;
	}
	
	public void AfficheChemin(LigneVariante l, Station depart, Station arrive) {
		ArrayList<Arete> tabArr= l.getAretes();
		int i;
		int indiceDepart= 0, indiceArrive= 0;
		boolean dernierA= false, dernierB= false;
		for(i= 0; i< tabArr.size(); i++) { // verifier aussi quand on arrive à la derniere arrete on doit regarder les 2 stations !
			if(tabArr.get(i).getStationA().getNom().equals(depart.getNom())) {
				indiceDepart= i;
				break ;
			}
			if(i== tabArr.size() - 1) {
				if(tabArr.get(i).getStationB().getNom().equals(depart.getNom())) {
					indiceDepart= i;
					dernierA= true;
				}
			}
		}
		for(i= 0; i< tabArr.size(); i++) { // verifier aussi quand on arrive à la derniere arrete on doit regarder les 2 stations !
			if(tabArr.get(i).getStationA().getNom().equals(arrive.getNom())) {
				indiceArrive= i;
				break ;
			}
			if(i== tabArr.size() - 1) {
				if(tabArr.get(i).getStationB().getNom().equals(arrive.getNom())) {
					indiceArrive= i;
					dernierB= true;
				}
			}
		}
		i= 0;
		if(indiceDepart< indiceArrive) {
			System.out.print("[ ");
			for(i= indiceDepart; i<= indiceArrive; i++) {
				if(i== indiceArrive) {
					if(dernierB== true) {
						System.out.print(tabArr.get(i).getStationA().getNom() + ", ");
						System.out.println(tabArr.get(i).getStationB().getNom() + " ]");
					}
					else {
						System.out.println(tabArr.get(i).getStationA().getNom() + " ]");
					}
				}
				else {
					System.out.print(tabArr.get(i).getStationA().getNom() + ", ");
				}
			}
		}
		else {
			if(indiceDepart > indiceArrive) {
				System.out.print("[ ");
				for(i= indiceDepart; i>= indiceArrive; i--) {
					if(i== indiceArrive) {
						if(dernierA== true) {
							System.out.print(tabArr.get(i).getStationB().getNom() + ", ");
							System.out.println(tabArr.get(i).getStationA().getNom() + " ]");
						}
						else {
							System.out.println(tabArr.get(i).getStationA().getNom() + " ]");
						}
					}
					else {
						if(i== indiceDepart) {
							if(dernierA== true) {
								System.out.print(tabArr.get(i).getStationB().getNom() + ", ");
							}
							else {
								System.out.print(tabArr.get(i).getStationA().getNom() + ", ");
							}
						}
						else {
							if(dernierA== true) {
								System.out.print(tabArr.get(i).getStationB().getNom() + ", ");
							}
							else {
								System.out.print(tabArr.get(i).getStationA().getNom() + ", ");
							}
						}
					}
				}
			}
			else { // indiceDepart= indiceArrive si dernière case du tableau arrête
				System.out.print("[ ");
				if(depart.getNom().equals(arrive.getNom())) {
					if(tabArr.get(indiceDepart).getStationA().getNom().equals(depart.getNom())) {
						System.out.print(tabArr.get(indiceDepart).getStationA().getNom() + ", ");
						System.out.println(tabArr.get(indiceArrive).getStationA().getNom() + " ]");
					}
					else {
						System.out.print(tabArr.get(indiceDepart).getStationB().getNom() + ", ");
						System.out.println(tabArr.get(indiceArrive).getStationB().getNom() + " ]");
					}
				}
				else {
					System.out.print(tabArr.get(indiceDepart).getStationA().getNom() + ", ");
					System.out.println(tabArr.get(indiceArrive).getStationB().getNom() + " ]");
				}
			}
		}
	}
	
	public ArrayList<StationAPrendre> RechercheChemin(LigneVariante ligne, ArrayList<LigneVariante> LignesArrive, ArrayList<LigneVariante> checked, ArrayList<StationAPrendre> liste) throws SQLException{ // je devrais stocker dans une nouvelle classe pour stocker une array de la station ET la ligne à prendre ?
		ArrayList<Arete> tabArr= ligne.getAretes();
		int i;
		for(i= 0; i< tabArr.size(); i++) {
			Arete arrCourant= tabArr.get(i);
			Station stationA= arrCourant.getStationA(); // on parcours toutes les stations de la ligne en param
			ArrayList<LigneVariante> LignesStationA= station_ligne.getLignes(stationA);
			if(LignesStationA!= null) {
				StationAPrendre stationCommun= StationEnCommun(stationA, LignesStationA, LignesArrive);
				if(stationCommun!= null) {
					ArrayList<StationAPrendre> courant= new ArrayList<StationAPrendre>();
					courant.addAll(liste);
					courant.add(stationCommun);
					return courant;
				}
			}
			if(i== tabArr.size() - 1) {
				Station stationB= arrCourant.getStationB(); // on parcours toutes les stations de la ligne en param
				ArrayList<LigneVariante> LignesStationB= station_ligne.getLignes(stationB);
				if(LignesStationB!= null) {
					StationAPrendre stationCommun= StationEnCommun(stationB, LignesStationB, LignesArrive);
					if(stationCommun!= null) {
						ArrayList<StationAPrendre> courant= new ArrayList<StationAPrendre>();
						courant.addAll(liste);
						courant.add(stationCommun);
						return courant;
					}
				}
			}
		}
		// pas de station en commun entre la ligne qui passe par la station de depart et les lignes de la station d'arrivée
		checked.add(ligne);
		
		Station stationAPrendre= new Station();
		ArrayList<LigneVariante> tab= ProchaineLignesAAnalyser(tabArr, checked, stationAPrendre);

		boolean uneFois= true;
		LigneVariante LigneAPrendre= null;
		
		for(LigneVariante l: tab) {
			ArrayList<StationAPrendre> courant= new ArrayList<StationAPrendre>();
			courant.addAll(liste);
			if(l.getNom().equals(ligne.getNom())== false) {
				if(uneFois== true) {
					LigneAPrendre= l;
					courant.add(new StationAPrendre(stationAPrendre, LigneAPrendre));
					uneFois= false;
				}
			}
			ArrayList<StationAPrendre> a= RechercheChemin(l, LignesArrive, checked, courant);
			if(a.size()!= liste.size()) {
				return a;
			}
		}
		
		return liste;
	}
	
	public StationAPrendre StationEnCommun(Station stationA, ArrayList<LigneVariante> LignesStationA, ArrayList<LigneVariante> LignesArrive){
		for(LigneVariante ligneStationA: LignesStationA) {
			for(LigneVariante ligneStationArrive: LignesArrive) {
				if(ligneStationArrive.getNom().equals(ligneStationA.getNom()) && ligneStationArrive.getNumVariante().equals(ligneStationA.getNumVariante())) {
					StationAPrendre s= new StationAPrendre(stationA, ligneStationA);
					return s;
				}
			}
		}
		return null;
	}
	
	public ArrayList<LigneVariante> ProchaineLignesAAnalyser(ArrayList<Arete> tabArr, ArrayList<LigneVariante> checked, Station stationAPrendre) throws SQLException{
		int i;
		ArrayList<LigneVariante> tab= new ArrayList<LigneVariante>();

		boolean appartient= false, uneFois= false;
		
		for(i= 0; i< tabArr.size(); i++) {
			Arete arrCourant= tabArr.get(i);
			Station stationA= arrCourant.getStationA(); // on parcours toutes les stations de la ligne en param
			ArrayList<LigneVariante> LignesStationA= station_ligne.getLignes(stationA);
			if(LignesStationA!= null) {
				for(LigneVariante ligneStationA: LignesStationA) {
					boolean AppartientAChecked= false;
					boolean AppartientATab= false;
					for(LigneVariante ligneChecked: checked) { // checked= les lignes déjà parcourus
						if(ligneChecked.getNom().equals(ligneStationA.getNom())== true && ligneChecked.getNumVariante().equals(ligneStationA.getNumVariante())== true) { // pour stocker par ex: ligne 7 variante 1 et ligne 7 variante 2 (il faut parcourir les 2 "lignes" car il se peut que pour une "ligne" il y a pas de correspondance vers une ligne de lignesArrive mais il y en a une pour l'autre "ligne" !)
							AppartientAChecked= true;
							break ;
						}
					}
					for(LigneVariante ltab: tab) { // tab= les lignes qu'on devra parcourir (on prend pas les doublons)
						if(ltab.getNom().equals(ligneStationA.getNom())== true && ltab.getNumVariante().equals(ligneStationA.getNumVariante())== true) {
							AppartientATab= true;
							break ;
						}
					}
					if(AppartientAChecked== false && AppartientATab== false) {
						tab.add(ligneStationA);
					}
					if(uneFois== false) { // ici je fais en sorte de prendre la 1ère station qui permet de faire la correspondance entre la ligne courante et la ligne suivante non parcourue
						for(LigneVariante ligneChecked: checked) {
							if(ligneChecked.getNom().equals(ligneStationA.getNom())== true && ligneChecked.getNumVariante().equals(ligneStationA.getNumVariante())== true) {
								appartient= true;
								break ;
							}
						}
						if(appartient== false) {
							stationAPrendre.setNom(stationA.getNom()); // stationAPrendre= la station qu'on doit prendre pour aller à une autre ligne non parcourue
							stationAPrendre.setX(stationA.getX());
							stationAPrendre.setY(stationA.getY());
							uneFois= true;
						}
						appartient= false;
					}
				}
			}
		}
		return tab; // les prochaines lignes à analyser
	}
	
	public LigneVariante RechercheLigneVarianteEnCommun(LigneVariante ligne, Station stationDepart, Station stationArrive) throws SQLException {
		
		ArrayList<LigneVariante> lignesStationDepart= station_ligne.getLignes(stationDepart);
		ArrayList<LigneVariante> lignesStationArrive= station_ligne.getLignes(stationArrive);
		ArrayList<LigneVariante> ligneVariantesStationDepart= new ArrayList<LigneVariante>();
		ArrayList<LigneVariante> ligneVariantesStationArrive= new ArrayList<LigneVariante>();
		if(lignesStationArrive== null || lignesStationDepart== null) {
			System.out.println("Erreur chargement lignes");
			return null;
		}
		for(LigneVariante ligneDepart: lignesStationDepart) {
			if(ligneDepart.getNom().equals(ligne.getNom())){
				ligneVariantesStationDepart.add(ligneDepart);
			}
		}
		for(LigneVariante ligneArrive: lignesStationArrive) {
			if(ligneArrive.getNom().equals(ligne.getNom())){
				ligneVariantesStationArrive.add(ligneArrive);
			}
		}
		for(LigneVariante l: ligneVariantesStationDepart) {
			for(LigneVariante l2: ligneVariantesStationArrive) {
				if(l2.getNumVariante().equals(l.getNumVariante())){
					return l2; // retourne une ligne variante en commun entre les 2 stations données en paramètres
				}
			}
		}
		
		return null; // pas de lignes variantes en commun
	}
	
	public boolean estEnCommun(LigneVariante ligne, Station stationDepart, Station stationArrive) throws SQLException {
		ArrayList<Arete> tabArr= ligne.getAretes();
		int i;
		boolean possedeStationDepart= false, possedeStationArrive= false;
		for(i= 0; i< tabArr.size(); i++) {
			Arete arrCourant= tabArr.get(i);
			Station stationA= arrCourant.getStationA();
			if(stationA.getNom().equals(stationDepart.getNom())) {
				possedeStationDepart= true;
			}
			if(stationA.getNom().equals(stationArrive.getNom())) {
				possedeStationArrive= true;
			}
			if(possedeStationDepart== true && possedeStationArrive== true) {
				return true;
			}
			if(i== tabArr.size() - 1) {
				Station stationB= arrCourant.getStationB();
				if(stationB.getNom().equals(stationDepart.getNom())) {
					possedeStationDepart= true;
				}
				if(stationB.getNom().equals(stationArrive.getNom())) {
					possedeStationArrive= true;
				}
				if(possedeStationDepart== true && possedeStationArrive== true) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void AffichePassage(LigneVariante ligne, Station stationDepart, Station stationArrive) throws SQLException {
		ArrayList<LigneVariante> lignesStationArrive= station_ligne.getLignes(stationArrive);
		if(lignesStationArrive== null) {
			System.out.println("Erreur chargement lignes");
			return ;
		}
		ArrayList<LigneVariante> ligneVariantesStationArrive= new ArrayList<LigneVariante>();
		for(LigneVariante ligneArrive: lignesStationArrive) {
			if(ligneArrive.getNom().equals(ligne.getNom())){
				ligneVariantesStationArrive.add(ligneArrive); // je prends que les lignes de stationArrive qui sont les meme que celui de stationDepart (le meme nom de ligne mais pas celui des variantes !)
			}
		}
		
		ArrayList<Arete> tabArr= ligne.getAretes();
		int i;
		for(i= 0; i< tabArr.size(); i++) {
			Arete arrCourant= tabArr.get(i);
			Station stationA= arrCourant.getStationA();
			ArrayList<LigneVariante> lignesStationA= station_ligne.getLignes(stationA);
			ArrayList<LigneVariante> ligneVariantesStationA= new ArrayList<LigneVariante>();
			for(LigneVariante ligneStationA: lignesStationA) {
				if(ligneStationA.getNom().equals(ligne.getNom())){
					ligneVariantesStationA.add(ligneStationA);
				}
			}
			if(ligneVariantesStationA.size()!= 0) {
				for(LigneVariante ligneVariantesA: ligneVariantesStationA) { // on parcourt toutes les variantes d'une ligne en param qui passe par StationA
					for(LigneVariante ligneVariantesArrive: ligneVariantesStationArrive) {
						if(ligneVariantesArrive.getNumVariante().equals(ligneVariantesA.getNumVariante())) { // si ligne de StationA= nom ligne en param avec la variante en commun entre stationArrive et stationA
							AfficheChemin(ligne, stationDepart, stationA);
							AfficheChemin(ligneVariantesArrive, stationA, stationArrive);
						}
					}
				}
			}
		}
	}
	
	public void trajetV0(Station depart, Station arrive) throws SQLException{
		if(depart== null || arrive== null) {
			return ;
		}
		ArrayList<LigneVariante> LignesDepart= station_ligne.getLignes(depart);
		ArrayList<LigneVariante> LignesArrive= station_ligne.getLignes(arrive);
		if(LignesDepart== null || LignesArrive== null) {
			return ;
		}
		ArrayList<LigneVariante> communs= RechercheLignesCommun(LignesDepart, LignesArrive);
		if(communs.size()> 0) {
			LigneVariante commun= communs.get(0);
			// Dire quels station faut prendre à partir de la ligne en commun (le premier qu'on trouve)
			System.out.println("\nVeuillez prendre la ligne " + commun.getNom() + " ");
			AfficheChemin(commun, depart, arrive);
		}
		else { // aucune ligne en commun entre depart et arrive
			for(LigneVariante ligneVar: LignesDepart) {
				ArrayList<StationAPrendre> commun= RechercheChemin(ligneVar, LignesArrive, new ArrayList<LigneVariante>(), new ArrayList<StationAPrendre>()); // probleme ici
				if(commun!= null) {
					System.out.println("\nPrenez la ligne " + ligneVar.getNom());
					LigneVariante tmp= ligneVar;
					int i;
					if(estEnCommun(ligneVar, depart, commun.get(0).getStation())== true) { // retourne true si les 2 stations se trouvent sur la même ligne variante
						AfficheChemin(ligneVar, depart, commun.get(0).getStation());
					}
					else {
						LigneVariante ligneVarianteCommun= RechercheLigneVarianteEnCommun(ligneVar, depart, commun.get(0).getStation());
						if(ligneVarianteCommun!= null) {
							AfficheChemin(ligneVarianteCommun, depart, commun.get(0).getStation());
						}
						else {
							AffichePassage(ligneVar, depart, commun.get(0).getStation());
						}
					}
					
					for(i= 0; i< commun.size(); i++) {
						System.out.println("\nPrenez la ligne " + commun.get(i).getLigne().getNom());
						if(i== commun.size() - 1) {
							if(estEnCommun(commun.get(i).getLigne(), commun.get(i).getStation(), arrive)== true) { // retourne true si les 2 stations se trouvent sur la même ligne variante
								AfficheChemin(commun.get(i).getLigne(), commun.get(i).getStation(), arrive);
							}
							else {
								LigneVariante ligneVarianteCommun= RechercheLigneVarianteEnCommun(commun.get(i).getLigne(), commun.get(i).getStation(), arrive);
								if(ligneVarianteCommun!= null) {
									AfficheChemin(ligneVarianteCommun, commun.get(i).getStation(), arrive);
								}
								else {
									AffichePassage(commun.get(i).getLigne(), commun.get(i).getStation(), arrive);
								}
							}
						}
						else {
							if(estEnCommun(commun.get(i).getLigne(), commun.get(i).getStation(), commun.get(i+1).getStation())== true) { // retourne true si les 2 stations se trouvent sur la même ligne variante
								AfficheChemin(commun.get(i).getLigne(), commun.get(i).getStation(), commun.get(i+1).getStation());
							}
							else {
								LigneVariante ligneVarianteCommun= RechercheLigneVarianteEnCommun(commun.get(i).getLigne(), commun.get(i).getStation(), commun.get(i+1).getStation());
								if(ligneVarianteCommun!= null) {
									AfficheChemin(ligneVarianteCommun, commun.get(i).getStation(), commun.get(i+1).getStation());
								}
								else {
									AffichePassage(commun.get(i).getLigne(), commun.get(i).getStation(), commun.get(i+1).getStation());
								}
							}
						}
					}
					break ;
				}
			}
		}
	}

}
