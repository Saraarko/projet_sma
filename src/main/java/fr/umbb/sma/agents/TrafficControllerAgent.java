package fr.umbb.sma.agents;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.*;

public class TrafficControllerAgent extends BaseAgent {
    private Map<String, Long> emergencyCorridors = new HashMap<>();
    private static final long CORRIDOR_TIMEOUT = 60000; // 60 seconds

    @Override
    protected void setup() {
        super.setup();
        logMessage("Traffic Controller initialized");

        addBehaviour(new RoutingRequestBehaviour());
        addBehaviour(new CorridorManagementBehaviour(this, 5000));
    }

    private class RoutingRequestBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                String content = msg.getContent();
                logMessage("Route request: " + content);

                String corridorId = "corridor_" + System.currentTimeMillis();
                emergencyCorridors.put(corridorId, System.currentTimeMillis());

                // Broadcast corridor availability to all units
                ACLMessage broadcast = new ACLMessage(ACLMessage.INFORM);
                broadcast.setContent("Emergency corridor available: " + corridorId +
                                   " for " + (CORRIDOR_TIMEOUT / 1000) + " seconds");

                logMessage("Opened emergency corridor: " + corridorId);
            } else {
                block();
            }
        }
    }

    private class CorridorManagementBehaviour extends TickerBehaviour {
        private TrafficControllerAgent agent;

        public CorridorManagementBehaviour(TrafficControllerAgent a, long period) {
            super(a, period);
            agent = a;
        }

        @Override
        protected void onTick() {
            long now = System.currentTimeMillis();
            List<String> expiredCorridors = new ArrayList<>();

            for (Map.Entry<String, Long> entry : agent.emergencyCorridors.entrySet()) {
                if (now - entry.getValue() > CORRIDOR_TIMEOUT) {
                    expiredCorridors.add(entry.getKey());
                }
            }

            for (String corridorId : expiredCorridors) {
                agent.emergencyCorridors.remove(corridorId);
                agent.logMessage("Emergency corridor expired: " + corridorId);
            }
        }
    }

    @Override
    protected void takeDown() {
        logMessage("Traffic Controller shutting down");
    }
}
