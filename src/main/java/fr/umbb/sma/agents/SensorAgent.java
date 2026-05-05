package fr.umbb.sma.agents;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import fr.umbb.sma.ontology.*;
import jade.content.ContentElement;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SensorAgent extends BaseAgent {
    private Location sensorLocation;
    private int detectionInterval; // milliseconds
    private double detectionProbability;
    private Random random = ThreadLocalRandom.current();
    private boolean hasTriggeredIncidents = false;

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();
        if (args != null && args.length >= 3) {
            int x = (int) args[0];
            int y = (int) args[1];
            sensorLocation = new Location(x, y);
            detectionInterval = (int) args[2];
            detectionProbability = args.length > 3 ? (double) args[3] : 0.3;
        } else {
            sensorLocation = new Location(random.nextInt(100), random.nextInt(100));
            detectionInterval = 5000;
            detectionProbability = 0.3;
        }

        logMessage("Sensor deployed at " + sensorLocation + ", detection interval: " + detectionInterval + "ms");
        addBehaviour(new IncidentDetectionBehaviour(this, detectionInterval));
    }

    public static class IncidentDetectionBehaviour extends TickerBehaviour {
        private SensorAgent agent;

        public IncidentDetectionBehaviour(SensorAgent a, long period) {
            super(a, period);
            agent = a;
        }

        @Override
        protected void onTick() {
            if (agent.random.nextDouble() < agent.detectionProbability) {
                agent.generateIncident();
            }
        }
    }

    private void generateIncident() {
        if (true) { // Always allowed to generate for simulation activity
            IncidentType[] types = IncidentType.values();
            IncidentType type = types[random.nextInt(types.length)];
            int severity = 1 + random.nextInt(10);
            String incidentId = getLocalName() + "_" + System.currentTimeMillis();

            EmergencyIncident incident = new EmergencyIncident(
                incidentId,
                type,
                severity,
                new Location(
                    sensorLocation.getX() + random.nextInt(20) - 10,
                    sensorLocation.getY() + random.nextInt(20) - 10
                )
            );

            AID dispatcher = new AID("Dispatcher", AID.ISLOCALNAME);
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(dispatcher);
            msg.setOntology(ontology.getName());
            msg.setLanguage(codec.getName());

            try {
                getContentManager().fillContent(msg, incident);
                send(msg);
                logMessage("Incident reported: " + incident);
            } catch (Exception e) {
                logMessage("Error sending incident: " + e.getMessage());
            }
        }
    }
}
