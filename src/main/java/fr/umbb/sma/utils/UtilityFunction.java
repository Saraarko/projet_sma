package fr.umbb.sma.utils;

import fr.umbb.sma.ontology.EmergencyIncident;
import fr.umbb.sma.ontology.UnitProposal;

public class UtilityFunction {
    // Weights for different factors (must sum to 1.0)
    private static final double DISTANCE_WEIGHT = 0.35;
    private static final double TYPE_MATCH_WEIGHT = 0.25;
    private static final double STATUS_WEIGHT = 0.20;
    private static final double SEVERITY_WEIGHT = 0.20;

    public static double calculateUtility(EmergencyIncident incident, UnitProposal proposal) {
        double distanceScore = calculateDistanceScore(proposal.getEstimatedTimeToIncident());
        double typeMatchScore = calculateTypeMatchScore(incident, proposal);
        double statusScore = calculateStatusScore(proposal.getStatus());
        double severityScore = calculateSeverityScore(incident.getSeverity());

        double utility = (distanceScore * DISTANCE_WEIGHT) +
                        (typeMatchScore * TYPE_MATCH_WEIGHT) +
                        (statusScore * STATUS_WEIGHT) +
                        (severityScore * SEVERITY_WEIGHT);

        proposal.setUtilityScore(utility);
        return utility;
    }

    private static double calculateDistanceScore(double estimatedTime) {
        // Lower time is better: exponential penalty
        // Score: 1.0 if time=0, decreases as time increases
        return Math.exp(-0.1 * estimatedTime) * 100;
    }

    private static double calculateTypeMatchScore(EmergencyIncident incident, UnitProposal proposal) {
        // Perfect match: 100, Partial match: 50, No match: 0
        switch (incident.getType()) {
            case FIRE:
                return proposal.getUnitType().contains("FireTruck") ? 100 :
                       proposal.getUnitType().contains("Police") ? 50 : 0;

            case MEDICAL:
                return proposal.getUnitType().contains("Ambulance") ? 100 : 0;

            case STRUCTURAL_COLLAPSE:
                return proposal.getUnitType().contains("FireTruck") ? 100 :
                       proposal.getUnitType().contains("Police") ? 50 : 0;

            case BIOHAZARD:
                return proposal.getUnitType().contains("BiohazardUnit") ? 100 : 0;

            case CRYOGENIC_LEAK:
                return proposal.getUnitType().contains("BiohazardUnit") ? 100 :
                       proposal.getUnitType().contains("FireTruck") ? 50 : 0;

            default:
                return 0;
        }
    }

    private static double calculateStatusScore(fr.umbb.sma.ontology.UnitStatus status) {
        // Prefer idle units
        switch (status) {
            case IDLE:
                return 100;
            case RESPONDING:
                return 60;
            case ON_SCENE:
                return 40;
            case ACTIVE:
                return 30;
            case RETURNING:
                return 20;
            case UNAVAILABLE:
                return 0;
            default:
                return 0;
        }
    }

    private static double calculateSeverityScore(int severity) {
        // Higher severity gets proportionally higher score
        // Severity: 1-10, Score: 10-100
        return (severity * 10);
    }

    public static String explainDecision(EmergencyIncident incident, UnitProposal proposal) {
        return String.format(
            "Decision for %s: Unit=%s, Distance=%.1f, Type Match=%.1f, Status=%.1f, Severity=%.1f, Total Utility=%.2f",
            incident.getId(), proposal.getUnitName(),
            proposal.getEstimatedTimeToIncident(),
            calculateTypeMatchScore(incident, proposal),
            calculateStatusScore(proposal.getStatus()),
            calculateSeverityScore(incident.getSeverity()),
            proposal.getUtilityScore()
        );
    }
}
