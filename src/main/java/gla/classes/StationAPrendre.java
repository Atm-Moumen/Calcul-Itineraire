package gla.classes;

import java.util.Objects;

public class StationAPrendre {

	private Station station;
	private LigneVariante ligne;
	
	public StationAPrendre(Station s, LigneVariante l) {
		station= s;
		ligne= l;
	}
	
	public Station getStation() {
		return station;
	}
	
	public LigneVariante getLigne() {
		return ligne;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		StationAPrendre that = (StationAPrendre) obj;
		return station.equals(that.station) && ligne.equals(that.ligne);
	}

	@Override
	public int hashCode() {
		return Objects.hash(station, ligne);
	}
}
