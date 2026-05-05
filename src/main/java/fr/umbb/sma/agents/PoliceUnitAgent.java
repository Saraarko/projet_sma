package fr.umbb.sma.agents;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import fr.umbb.sma.ontology.*;
import java.util.Random;

public class PoliceUnitAgent extends BaseAgent {
    private Location currentLocation;
    private UnitStatus status = UnitStatus.IDLE;
    private String currentIncidentId;
    private EmergencyIncident activeIncident;
    private int speed = 3;
    private Random random = new Random();

    @Override
    protected void setup() {
        super.setup();

        Object[] args = getArguments();
        if (args != null && args.length >= 2) {
            int x = (int) args[0];
            int y = (int) args[1];
            currentLocation = new Location(x, y);
        } else {
            currentLocation = new Location(75, 75);
        }

        registerWithDF();
        logMessage("Police Unit ready at " + currentLocation);

        addBehaviour(new PatrolBehaviour(this, 2000));
        addBehaviour(new CFPListenerBehaviour());
        addBehaviour(new MovementBehaviour(this, 1000));
        addBehaviour(new IncidentCompletionBehaviour());
    }

    private void registerWithDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd1 = new ServiceDescription();
        sd1.setName("Police-" + getLocalName());
        sd1.setType("CROWD_CONTROL");
        dfd.addServices(sd1);

        ServiceDescription sd2 = new ServiceDescription();
        sd2.setName("Police-Perimeter-" + getLocalName());
        sd2.setType("PERIMETER");
        dfd.addServices(sd2);

        try {
            DFService.register(this, dfd);
            logMessage("Registered with DF as CROWD_CONTROL and PERIMETER");
        } catch (FIPAException fe) {
            logMessage("DF registration error: " + fe.getMessage());
        }
    }

    private class PatrolBehaviour extends TickerBehaviour {
        private PoliceUnitAgent agent;

        public PatrolBehaviour(PoliceUnitAgent a, long period) {
            super(a, period);
            agent = a;
        }

        @Override
        protected void onTick() {
            if (agent.status == UnitStatus.IDLE) {
                int newX = agent.random.nextInt(100);
                int newY = agent.random.nextInt(100);
                agent.logMessage("Patrolling to (" + newX + "," + newY + ")");
            }
        }
    }

    private class CFPListenerBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                try {
                    EmergencyIncident incident = (EmergencyIncident) getContentManager()
                        .extractContent(msg);

                    if ((incident.getType() == IncidentType.FIRE ||
                         incident.getType() == IncidentType.STRUCTURAL_COLLAPSE) &&
                        status == UnitStatus.IDLE) {

                        double distance = currentLocation.distanceTo(incident.getLocation());
                        double estimatedTime = distance / speed;

                        UnitProposal proposal = new UnitProposal(
                            getLocalName(),
                            "PoliceUnit",
                            new Location(currentLocation.getX(), currentLocation.getY()),
                            status
                        );
                        proposal.setEstimatedTimeToIncident(estimatedTime);

                        ACLMessage propose = new ACLMessage(ACLMessage.PROPOSE);
                        propose.addReceiver(msg.getSender());
                        propose.setConversationId(msg.getConversationId());
                        propose.setOntology(ontology.getName());
                        propose.setLanguage(codec.getName());

                        getContentManager().fillContent(propose, proposal);
                        send(propose);

                        logMessage("Sent proposal for " + incident.getId());
                    }
                } catch (Exception e) {
                    logMessage("Error processing CFP: " + e.getMessage());
                }
            } else {
                block();
            }
        }
    }

    private class MovementBehaviour extends TickerBehaviour {
        private PoliceUnitAgent agent;

        public MovementBehaviour(PoliceUnitAgent a, long period) {
            super(a, period);
            agent = a;
        }

        @Override
        protected void onTick() {
            if (agent.status == UnitStatus.RESPONDING && agent.activeIncident != null) {
                Location target = agent.activeIncident.getLocation();
                moveTowards(target);

                double distance = agent.currentLocation.distanceTo(target);
                if (distance < 2) {
                    agent.status = UnitStatus.ON_SCENE;
                    agent.logMessage("Secured perimeter at incident " + agent.currentIncidentId);
                    sendArrivalNotification();
                }
            } else if (agent.status == UnitStatus.RETURNING && agent.activeIncident != null) {
                moveTowards(new Location(75, 75));
                double distanceToBase = agent.currentLocation.distanceTo(new Location(75, 75));
                if (distanceToBase < 2) {
                    agent.status = UnitStatus.IDLE;
                    agent.activeIncident = null;
                    agent.currentIncidentId = null;
                    agent.logMessage("Returned to station");
                }
            }
        }

        private void moveTowards(Location target) {
            double dx = target.getX() - agent.currentLocation.getX();
            double dy = target.getY() - agent.currentLocation.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > agent.speed) {
                agent.currentLocation.setX((int)(agent.currentLocation.getX() + (dx / distance) * agent.speed));
                agent.currentLocation.setY((int)(agent.currentLocation.getY() + (dy / distance) * agent.speed));
            }
        }
    }

    private void sendArrivalNotification() {
        AID dispatcher = new AID("Dispatcher", AID.ISLOCALNAME);
        ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
        inform.addReceiver(dispatcher);
        inform.setContent("Police " + getLocalName() + " secured perimeter at incident " + currentIncidentId);
        send(inform);
    }

    private class IncidentCompletionBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                try {
                    activeIncident = (EmergencyIncident) getContentManager()
                        .extractContent(msg);
                    currentIncidentId = activeIncident.getId();
                    status = UnitStatus.RESPONDING;

                    logMessage("Accepting incident: " + currentIncidentId);
                } catch (Exception e) {
                    logMessage("Error accepting incident: " + e.getMessage());
                }
            } else {
                MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
                ACLMessage reject = myAgent.receive(mt2);
                if (reject != null) {
                    logMessage("Proposal rejected for: " + reject.getConversationId());
                } else {
                    block();
                }
            }
        }
    }

    public void completeIncident() {
        if (activeIncident != null) {
            status = UnitStatus.RETURNING;
            AID dispatcher = new AID("Dispatcher", AID.ISLOCALNAME);
            ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
            inform.addReceiver(dispatcher);
            inform.setContent("Incident " + currentIncidentId + " completed by " + getLocalName());
            send(inform);
            logMessage("Incident " + currentIncidentId + " marked as completed");
        }
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            logMessage("Deregistration error: " + fe.getMessage());
        }
        logMessage("Police Unit shutting down");
    }
}
