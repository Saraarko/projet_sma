package fr.umbb.sma.ontology;

import jade.content.Concept;
import java.io.Serializable;

public class HospitalInfo implements Concept, Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Location location;
    private int totalBeds;
    private int availableBeds;

    public HospitalInfo() {}

    public HospitalInfo(String name, Location location, int totalBeds, int availableBeds) {
        this.name = name;
        this.location = location;
        this.totalBeds = totalBeds;
        this.availableBeds = availableBeds;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public int getTotalBeds() { return totalBeds; }
    public void setTotalBeds(int beds) { this.totalBeds = beds; }

    public int getAvailableBeds() { return availableBeds; }
    public void setAvailableBeds(int beds) { this.availableBeds = beds; }

    public void reserveBed() {
        if (availableBeds > 0) availableBeds--;
    }

    public void releaseBed() {
        if (availableBeds < totalBeds) availableBeds++;
    }

    @Override
    public String toString() {
        return String.format("Hospital[%s|beds:%d/%d|loc:%s]", name, availableBeds, totalBeds, location);
    }
}
