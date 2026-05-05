package fr.umbb.sma.ontology;

import jade.content.Predicate;
import java.io.Serializable;

public class EmergencyIncident implements Predicate, Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private IncidentType type;
    private int severity; // 1-10
    private Location location;
    private long reportedTime;

    public EmergencyIncident() {}

    public EmergencyIncident(String id, IncidentType type, int severity, Location location) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.location = location;
        this.reportedTime = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public IncidentType getType() { return type; }
    public void setType(IncidentType type) { this.type = type; }

    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public long getReportedTime() { return reportedTime; }
    public void setReportedTime(long reportedTime) { this.reportedTime = reportedTime; }

    @Override
    public String toString() {
        return String.format("Incident[%s|%s|severity:%d|%s]", id, type, severity, location);
    }
}
