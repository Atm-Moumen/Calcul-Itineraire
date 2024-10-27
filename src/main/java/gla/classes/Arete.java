package gla.classes;


public class Arete{ 
	private Station stationA;
    private  Station stationB;
    private float distance;
    private String duree;
    

    public Arete(Station stA, Station stB, float distance, String duree){
    	stationA= stA;
    	stationB= stB;
        this.distance = distance;
        this.duree= duree;
    }

    public float getDistance(){
        return distance;
    }

    public void setDistance(float distance){
        this.distance = distance;
    }

    public String getDuree(){
        return duree;
    }

    public void setDuree(String duree){
        this.duree = duree;
    }
    
    public Station getStationA() {
    	return stationA;
    }
    
    public Station getStationB() {
    	return stationB;
    }

}

