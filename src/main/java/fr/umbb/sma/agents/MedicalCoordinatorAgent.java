package fr.umbb.sma.agents;

import jade.core.AID;
import jade.core.behaviours.*;
import fr.umbb.sma.ontology.*;
import java.util.*;

public class MedicalCoordinatorAgent extends BaseAgent {
    private List<HospitalInfo> hospitals = new ArrayList<>();

    @Override
    protected void setup() {
        super.setup();
        initializeHospitals();
        logMessage("Medical Coordinator initialized with " + hospitals.size() + " hospitals");

        addBehaviour(new HospitalManagementBehaviour());
    }

    private void initializeHospitals() {
        hospitals.add(new HospitalInfo("Central Hospital", new Location(30, 30), 50, 15));
        hospitals.add(new HospitalInfo("West Medical Center", new Location(20, 60), 30, 8));
        hospitals.add(new HospitalInfo("East Clinic", new Location(80, 40), 20, 5));
        hospitals.add(new HospitalInfo("Emergency Ward", new Location(60, 20), 40, 10));
    }

    private class HospitalManagementBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Monitor hospital status
            int totalBeds = 0;
            int availableBeds = 0;

            for (HospitalInfo h : hospitals) {
                totalBeds += h.getTotalBeds();
                availableBeds += h.getAvailableBeds();
            }

            if (availableBeds < 5) {
                logMessage("ALERT: Critical hospital bed shortage! Available: " + availableBeds + "/" + totalBeds);
            }

            block(1000);
        }
    }

    public HospitalInfo findNearestHospital(Location patientLocation) {
        return hospitals.stream()
            .filter(h -> h.getAvailableBeds() > 0)
            .min(Comparator.comparingDouble(h -> h.getLocation().distanceTo(patientLocation)))
            .orElse(null);
    }

    public void reserveHospitalBed(String hospitalName) {
        for (HospitalInfo h : hospitals) {
            if (h.getName().equals(hospitalName)) {
                h.reserveBed();
                logMessage("Reserved bed at " + hospitalName + ". Available: " + h.getAvailableBeds());
                break;
            }
        }
    }

    public void releaseBed(String hospitalName) {
        for (HospitalInfo h : hospitals) {
            if (h.getName().equals(hospitalName)) {
                h.releaseBed();
                logMessage("Released bed at " + hospitalName + ". Available: " + h.getAvailableBeds());
                break;
            }
        }
    }

    @Override
    protected void takeDown() {
        logMessage("Medical Coordinator shutting down");
    }
}
