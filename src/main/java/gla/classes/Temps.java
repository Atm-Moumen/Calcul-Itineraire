package gla.classes;

import java.util.Objects;

public class Temps implements Comparable<Temps> {
    private int heure;
    private int minute;
    private int seconde;

    public Temps(String temps) {
        String[] tab = temps.split(":");
        this.minute = Integer.parseInt(tab[0]);
        seconde = Integer.parseInt(tab[1]);
    }

    public Temps(int heure, int minute, int seconde){
        this.heure = heure;
        this.minute = minute;
        this.seconde = seconde;
    }

    public boolean plusRapide(Temps h) {
        if ((heure < h.heure) || ((heure == h.heure) && (minute < h.minute)) || ((heure == h.heure) && (minute == h.minute) && (seconde <= h.seconde))) {
            return true;
        } else {
            return false;
        }
    }

    public Temps ajouterTemps(Temps h, boolean trajet) { //permet de calculer l'horaire avec l'ajout de h
        int supS = (seconde + h.seconde) / 60;
        int secondeA = (seconde + h.seconde) % 60;
        int supM = (minute + h.minute + supS) / 60;
        int minuteA = (minute + h.minute + supS) % 60;
        int heureA = (heure + h.heure + supM);
        if(!trajet){
            heureA = heureA % 24;
        }
        return new Temps(heureA, minuteA, secondeA);
    }

    public Temps reduireTemps(Temps h) { //permet de calculer la difference entre les horaires (par ex: 23:00:00 et 23:30:00 donne 00:30:00)
        int secondeA = ((seconde - h.seconde) + 60)%60  ;
        int supS = 0;
        if((seconde - h.seconde) < 0){
            supS += 1;
        }
        int supM = 0;
        int minuteA = ((minute - h.minute - supS) + 60)%60;
        if((minute - h.minute - supS) < 0){
            supM = 1;
        }
        int heureA =  ((heure - h.heure - supM) + 24)%24;
        return new Temps(heureA, minuteA, secondeA);
    }

    @Override
    public String toString() {
        String h = String.format("%02dh", heure);
        String m = String.format("%02dm", minute);
        String s = String.format("%02ds", seconde);
        return h + " " + m + " " + s;
    }

    @Override
    public int compareTo(Temps h) {
        if (heure != h.heure) {
            return Integer.compare(heure, h.heure);
        } else if (minute != h.minute) {
            return Integer.compare(minute, h.minute);
        } else {
            return Integer.compare(seconde, h.seconde);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(heure, minute, seconde);
    }

    public int getHeure() {
        return heure;
    }

    public int getSeconde() {
        return seconde;
    }

    public int getMinute() {
        return minute;
    }
}