package fr.umbb.sma.utils;

import fr.umbb.sma.ontology.EmergencyIncident;
import fr.umbb.sma.ontology.IncidentType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IncidentLogger {
    private static IncidentLogger instance;
    private static final String LOG_DIR = "logs";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Map<String, IncidentRecord> incidentRecords = new ConcurrentHashMap<>();
    private List<String> eventLog = Collections.synchronizedList(new ArrayList<>());
    private Map<String, AgentStatus> agentStatuses = new ConcurrentHashMap<>();

    public static synchronized IncidentLogger getInstance() {
        if (instance == null) instance = new IncidentLogger();
        return instance;
    }

    public static class IncidentRecord {
        public String incidentId;
        public IncidentType type;
        public int severity;
        public int locationX;
        public int locationY;
        public long reportedTime;
        public String reportedTimeStr;
        public String assignedUnit;
        public long assignmentTime;
        public long arrivalTime;
        public long completionTime;
        public String status;
        public String notes;

        public IncidentRecord(String id, IncidentType type, int severity, int x, int y) {
            this.incidentId = id;
            this.type = type;
            this.severity = severity;
            this.locationX = x;
            this.locationY = y;
            this.reportedTime = System.currentTimeMillis();
            this.reportedTimeStr = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
            this.status = "PENDING";
        }
    }

    public static class AgentStatus {
        public String name;
        public String type;
        public String status;
        public int x;
        public int y;
        public long lastUpdate;

        public AgentStatus(String name, String type, int x, int y) {
            this.name = name;
            this.type = type;
            this.status = "IDLE";
            this.x = x;
            this.y = y;
            this.lastUpdate = System.currentTimeMillis();
        }
    }

    public void registerAgent(String name, String type, int x, int y) {
        agentStatuses.put(name, new AgentStatus(name, type, x, y));
    }

    public void updateAgentStatus(String name, String status) {
        AgentStatus s = agentStatuses.get(name);
        if (s != null) {
            s.status = status;
            s.lastUpdate = System.currentTimeMillis();
        }
    }

    public void logIncidentReported(EmergencyIncident incident) {
        int x = incident.getLocation() != null ? incident.getLocation().getX() : 0;
        int y = incident.getLocation() != null ? incident.getLocation().getY() : 0;
        IncidentRecord record = new IncidentRecord(incident.getId(), incident.getType(), incident.getSeverity(), x, y);
        incidentRecords.put(incident.getId(), record);
        logEvent("INCIDENT_REPORTED: " + incident);
    }

    public void logIncidentAssigned(String incidentId, String unitName, long assignmentTime) {
        IncidentRecord record = incidentRecords.get(incidentId);
        if (record != null) {
            record.assignedUnit = unitName;
            record.assignmentTime = assignmentTime;
            record.status = "ASSIGNED";
            updateAgentStatus(unitName, "RESPONDING");
            logEvent(String.format("INCIDENT_ASSIGNED: %s -> %s (response time: %.2f sec)",
                incidentId, unitName, (assignmentTime - record.reportedTime) / 1000.0));
        }
    }

    public void logUnitArrival(String incidentId, String unitName) {
        IncidentRecord record = incidentRecords.get(incidentId);
        if (record != null) {
            record.arrivalTime = System.currentTimeMillis();
            updateAgentStatus(unitName, "ON_SCENE");
            logEvent(String.format("UNIT_ARRIVED: %s at incident %s (arrival time: %.2f sec)",
                unitName, incidentId, (record.arrivalTime - record.reportedTime) / 1000.0));
        }
    }

    public void logIncidentCompleted(String incidentId, String unitName) {
        IncidentRecord record = incidentRecords.get(incidentId);
        if (record != null) {
            record.completionTime = System.currentTimeMillis();
            record.status = "COMPLETED";
            updateAgentStatus(unitName, "IDLE");
            long responseTime = record.completionTime - record.reportedTime;
            logEvent(String.format("INCIDENT_COMPLETED: %s by %s (total time: %.2f sec)",
                incidentId, unitName, responseTime / 1000.0));
        }
    }

    public void logIncidentAborted(String incidentId, String unitName, String reason) {
        IncidentRecord record = incidentRecords.get(incidentId);
        if (record != null) {
            record.status = "ABORTED";
            record.notes = reason;
            updateAgentStatus(unitName, "IDLE");
            logEvent(String.format("INCIDENT_ABORTED: %s by %s - %s", incidentId, unitName, reason));
        }
    }

    public void logEvent(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        String logEntry = "[" + timestamp + "] " + message;
        eventLog.add(logEntry);
        System.out.println(logEntry);
    }

    public String toJson() {
        JsonObject root = new JsonObject();

        JsonArray incidents = new JsonArray();
        int completed = 0, pending = 0, assigned = 0, aborted = 0;
        double totalResponseTime = 0;

        for (IncidentRecord record : incidentRecords.values()) {
            JsonObject inc = new JsonObject();
            inc.addProperty("id", record.incidentId);
            inc.addProperty("type", record.type != null ? record.type.toString() : "UNKNOWN");
            inc.addProperty("severity", record.severity);
            inc.addProperty("locationX", record.locationX);
            inc.addProperty("locationY", record.locationY);
            inc.addProperty("reportedAt", record.reportedTimeStr);
            inc.addProperty("assignedUnit", record.assignedUnit != null ? record.assignedUnit : "");
            inc.addProperty("status", record.status);

            if ("COMPLETED".equals(record.status)) {
                completed++;
                double rt = (record.completionTime - record.reportedTime) / 1000.0;
                inc.addProperty("responseTimeSeconds", rt);
                totalResponseTime += rt;
            } else if ("ASSIGNED".equals(record.status)) {
                assigned++;
            } else if ("PENDING".equals(record.status)) {
                pending++;
            } else if ("ABORTED".equals(record.status)) {
                aborted++;
                if (record.notes != null) inc.addProperty("abortReason", record.notes);
            }
            incidents.add(inc);
        }

        root.add("incidents", incidents);

        JsonObject stats = new JsonObject();
        stats.addProperty("total", incidentRecords.size());
        stats.addProperty("completed", completed);
        stats.addProperty("assigned", assigned);
        stats.addProperty("pending", pending);
        stats.addProperty("aborted", aborted);
        if (completed > 0)
            stats.addProperty("avgResponseTimeSeconds", totalResponseTime / completed);
        else
            stats.addProperty("avgResponseTimeSeconds", 0);
        root.add("statistics", stats);

        JsonArray agents = new JsonArray();
        for (AgentStatus s : agentStatuses.values()) {
            JsonObject a = new JsonObject();
            a.addProperty("name", s.name);
            a.addProperty("type", s.type);
            a.addProperty("status", s.status);
            a.addProperty("x", s.x);
            a.addProperty("y", s.y);
            a.addProperty("lastUpdate", s.lastUpdate);
            agents.add(a);
        }
        root.add("agents", agents);

        JsonArray events = new JsonArray();
        List<String> recentEvents = eventLog.size() > 100
            ? eventLog.subList(eventLog.size() - 100, eventLog.size())
            : eventLog;
        recentEvents.forEach(events::add);
        root.add("recentEvents", events);

        return gson.toJson(root);
    }

    public void generateReport(String filename) {
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get(LOG_DIR));
            String filepath = LOG_DIR + "/" + filename;
            try (FileWriter writer = new FileWriter(filepath)) {
                writer.write(toJson());
            }
            System.out.println("\n[REPORT] Generated at: " + filepath);
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }
}
