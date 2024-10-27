package gla;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


import gla.database.SQLiteDB;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import gla.classes.LigneVariante;
import gla.classes.Station;
import gla.classes.StationLigne;
import gla.classes.Itineraire;
import gla.classes.Arete;
import gla.classes.StationAPrendre;

public class ItineraireTest {

    private StationLigne stationLigne;

    private LigneVariante ligne1;
    private LigneVariante ligne2;
    private LigneVariante ligne3;
    private LigneVariante ligne4;
    private ArrayList<LigneVariante> lignesStationA;
    private ArrayList<LigneVariante> lignesStationB;
    private ArrayList<LigneVariante> lignesStationC, lignesStationD, lignesStationE, lignesStationF;


    private ArrayList<Arete> aretes, aretes2, aretes3;
    private ArrayList<LigneVariante> lignesArrivee;
    private Itineraire itineraire;
    private Station a,b,c,d,e,f;
    private Arete areteAB, areteBC, areteCD, areteDE, areteBF;
    private Map<Station, ArrayList<LigneVariante>> map;
    private SQLiteDB bdd = new SQLiteDB();

    public void initTestDb(){ // création d'une bdd en mémoire pour effectuer la CI
        bdd.connexionTest();
        bdd.init_tables();
        bdd.lire_csv("src/main/java/gla/database/map_data.csv");
        bdd.lire_csv_timetables("src/main/java/gla/database/timetables.csv");
    }

    @Before
    public void setUp() {
        stationLigne = new StationLigne(new HashMap<Station, ArrayList<LigneVariante>>(), bdd);
        aretes = new ArrayList<Arete>();
        aretes2 = new ArrayList<>();
        aretes3 = new ArrayList<>();
        ligne1 = new LigneVariante("ligne1", "variante1", aretes);
        ligne2 = new LigneVariante("ligne2", "variante2", aretes2);
        ligne3 = new LigneVariante("ligne3", "variante3", aretes3);
        lignesStationA = new ArrayList<>();
        lignesStationB = new ArrayList<>();
        lignesStationC = new ArrayList<>();
        lignesStationD = new ArrayList<>();
        lignesStationE = new ArrayList<>();
        lignesStationF = new ArrayList<>();
        itineraire = new Itineraire(stationLigne);
        a = new Station("A", 0.0f, 0.0f);
        b = new Station("B", 1.0f, 2.0f);
        c = new Station("C", 2.0f, 3.0f);
        d = new Station("D", 3.0f, 4.0f);
        e = new Station("E", 4.0f, 5.0f);
        f = new Station("F", 5.0f, 6.0f);
        areteAB = new Arete(a, b, 1.0f, "5:00");
        areteBC = new Arete(b, c, 2.0f, "6.00");
        areteCD = new Arete(c, d, 3.0f, "7:00");
        areteDE = new Arete(d, e, 4.0f, "8:00");
        areteBF = new Arete(b, f, 5.0f, "9:00");
        aretes.add(areteAB);
        aretes.add(areteBC);
        aretes.add(areteCD);
        aretes.add(areteDE);
        aretes2.add(areteAB);
        aretes2.add(areteBF);
        aretes3.add(areteCD);
        lignesStationA.add(ligne1);
        lignesStationA.add(ligne2);
        lignesStationB.add(ligne1);
        lignesStationB.add(ligne2);
        lignesStationC.add(ligne1);
        lignesStationC.add(ligne3);
        lignesStationD.add(ligne1);
        lignesStationD.add(ligne3);
        lignesStationE.add(ligne1);
        lignesStationF.add(ligne2);
        
        map = new HashMap<>();
        ligne4 = new LigneVariante("ligne4", "variante4", aretes);
        lignesArrivee = new ArrayList<>();

    }
    @After
    public void tearDown() {
        bdd = null;
        stationLigne = null;
        aretes = null;
        aretes2 = null;
        aretes3 = null;
        ligne1 = null;
        ligne2 = null;
        ligne3 = null;
        lignesStationA = null;
        lignesStationB = null;
        lignesStationC = null;
        lignesStationD = null;
        lignesStationE = null;
        lignesStationF = null;
        itineraire = null;
        a = null;
        b = null;
        c = null;
        d = null;
        e = null;
        f = null;
        areteAB = null;
        areteBC = null;
        areteCD = null;
        areteDE = null;
        areteBF = null;
        map = null;
        ligne4 = null;
        lignesArrivee = null;
    }

    @Test
    public void testRechercheLignesCommun() throws SQLException {
        ArrayList<LigneVariante> communs = itineraire.RechercheLignesCommun(lignesStationA, lignesStationB);
        assertEquals("il exise une ligne en commun",2, communs.size());
    }

    @Test
    public void testRechercheLignesCommunNoCommon() throws SQLException {
        ArrayList<LigneVariante> communs = itineraire.RechercheLignesCommun(lignesStationF, lignesStationC);
        assertEquals("pas de lignes en commun",0, communs.size());
    }

    @Test
    public void testAfficheChemin() {
        Station depart = a;
        Station arrive = e;
        ArrayList<String> expectedOutput1 = new ArrayList<String>();
        expectedOutput1.add("[ A");
        expectedOutput1.add("B");
        expectedOutput1.add("C");
        expectedOutput1.add("D");
        expectedOutput1.add("E ]");
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos1));

        itineraire.AfficheChemin(ligne4, depart, arrive);
        String actualOutput1 = baos1.toString();
        String[] actualStations1 = actualOutput1.split("\\r?\\n");
        ArrayList<String> actualOutputList1 = new ArrayList<String>(Arrays.asList(actualStations1[0].split(", ")));
        assertEquals("le chemin de a à b, on est sur la ligne4",expectedOutput1, actualOutputList1);
    }

    @Test
    public void testRechercheChemin() throws SQLException {
        initTestDb();
        // Création d'une liste de lignes que l'on souhaite prendre pour arriver à notre destination finale.
        lignesArrivee.add(ligne3);
        // Création d'une instance de la classe StationLigne pour gérer les lignes passant par les stations
        stationLigne.addElement(a, lignesStationA);
        stationLigne.addElement(b, lignesStationB);
        stationLigne.addElement(c, lignesStationC);
        // Appel de la méthode RechercheChemin avec une ligne de départ
        ArrayList<StationAPrendre> resultat = itineraire.RechercheChemin(ligne2, lignesArrivee, new ArrayList<>(), new ArrayList<>());
        // Vérification du résultat
        ArrayList<StationAPrendre> attendu = new ArrayList<>();
        attendu.add(new StationAPrendre(a, ligne1));
        attendu.add(new StationAPrendre(c, ligne3));
        assertEquals("les stations à prendre",attendu, resultat);
    }

    @Test
    public void testStationEnCommun() throws SQLException {
    	/* permet de savoir si la station en paramètre se trouve parmis une des lignes d'arrivées (en paramètres aussi) 
    	 * autrement dit: la station en paramètre se trouve sur une ligne quelconque et sur une des lignes d'arrivées */
    	lignesArrivee.add(ligne1);
    	StationAPrendre resultat= itineraire.StationEnCommun(c, lignesStationC, lignesArrivee); // je suis à la ligne 3 et je regarde si je peux aller à la ligne 1 avec la station c
    	StationAPrendre attendu = new StationAPrendre(c, ligne1);
        assertEquals(attendu, resultat);
    }
    @Test
    public void testProchaineLignesAAnalyser() throws SQLException {
        initTestDb();
        stationLigne.addElement(a, lignesStationA);
        stationLigne.addElement(b, lignesStationB);
        stationLigne.addElement(c, lignesStationC);
        ArrayList<LigneVariante> checked= new ArrayList<LigneVariante>();
        checked.add(ligne1);
        checked.add(ligne2);
        Station stationAPrendre= new Station();
        // aretes contient 3 lignes, la ligne 1, 2 et 3
        // on a déjà parcourue les lignes 1 et 2, on devrait donc s'attendre que la méthode retourne la ligne 3 car la ligne 3 fait parti de la liste aretes
        ArrayList<LigneVariante> resultat= itineraire.ProchaineLignesAAnalyser(aretes, checked, stationAPrendre);
        ArrayList<LigneVariante> attendu = new ArrayList<LigneVariante>();
        attendu.add(ligne3);
        assertEquals(attendu, resultat);
    }
    @Test
    public void testStationEnCommunNoCommun() throws SQLException {
    	lignesArrivee.add(ligne2);
    	StationAPrendre resultat= itineraire.StationEnCommun(c, lignesStationC, lignesArrivee); // je suis à la ligne 3 et je regarde si je peux aller à la ligne 2 avec la station c
    	StationAPrendre attendu = null; // veut dire que la station c (qui fait parti de la ligne 1 et 3) ne fait pas parti de la ligne d'arrivée (ici ligne 2)
        assertEquals(attendu, resultat);
    }
    @Test
    public void testRechercheLigneVarianteEnCommun() throws SQLException { // permet de savoir s'il existe une ligne avec variante en commun entre 2 stations
    	stationLigne.addElement(a, lignesStationA);
        stationLigne.addElement(d, lignesStationD);
    	LigneVariante resultat= itineraire.RechercheLigneVarianteEnCommun(ligne1, a, d); // je regarde si pour la ligne 1, il existe une variante qui possède la station a et d
    	LigneVariante attendu= ligne1;
    	assertEquals(attendu, resultat);
    }
    
    @Test
    public void testRechercheLigneVarianteEnCommunNoCommun() throws SQLException {
    	stationLigne.addElement(a, lignesStationA);
        stationLigne.addElement(d, lignesStationD);
    	LigneVariante resultat= itineraire.RechercheLigneVarianteEnCommun(ligne3, d, a); // je regarde si pour la ligne 3, il existe une variante qui possède la station a et d
    	LigneVariante attendu= null;
    	assertEquals(attendu, resultat);
    }
    
    @Test
    public void testestEnCommun() throws SQLException { // permet de savoir si la ligne (avec variante) en paramètre possède les deux stations en paramètres
    	boolean resultat= itineraire.estEnCommun(ligne2, b, a);
    	boolean attendu= true;
    	assertEquals(attendu, resultat);
    }
    
    @Test
    public void testestEnCommunNoCommun() throws SQLException {
    	boolean resultat= itineraire.estEnCommun(ligne2, a, c);
    	boolean attendu= false;
    	assertEquals(attendu, resultat);
    }

}
