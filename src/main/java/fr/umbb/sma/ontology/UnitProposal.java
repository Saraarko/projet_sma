package fr.umbb.sma.ontology;

import jade.content.Predicate;
import java.io.Serializable;

public class UnitProposal implements Predicate, Serializable {
    private static final long serialVersionUID = 1L;
    private String unitName;
    private String unitType;
    private Location currentLocation;
    private UnitStatus status;
    private double estimatedTimeToIncident;
    private double utilityScore;

    public UnitProposal() {}

    public UnitProposal(String unitName, String unitType, Location location, UnitStatus status) {
        this.unitName = unitName;
        this.unitType = unitType;
        this.currentLocation = location;
        this.status = status;
    }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }

    public Location getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(Location location) { this.currentLocation = location; }

    public UnitStatus getStatus() { return status; }
    public void setStatus(UnitStatus status) { this.status = status; }

    public double getEstimatedTimeToIncident() { return estimatedTimeToIncident; }
    public void setEstimatedTimeToIncident(double time) { this.estimatedTimeToIncident = time; }

    public double getUtilityScore() { return utilityScore; }
    public void setUtilityScore(double score) { this.utilityScore = score; }

    @Override
    public String toString() {
        return String.format("Proposal[%s|%s|dist:%.1f|utility:%.2f]",
            unitName, unitType, estimatedTimeToIncident, utilityScore);
    }
}
