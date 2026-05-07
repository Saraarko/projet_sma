package fr.umbb.sma.agents;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.*;
import fr.umbb.sma.ontology.*;
import fr.umbb.sma.utils.IncidentLogger;
import fr.umbb.sma.utils.UtilityFunction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DispatcherAgent extends BaseAgent {
    private IncidentLogger logger = IncidentLogger.getInstance();
    private Map<String, PendingIncident> pendingIncidents = new ConcurrentHashMap<>();
    private Map<String, String> assignedIncidents = new ConcurrentHashMap<>();

    public static class PendingIncident {
        public EmergencyIncident incident;
        public List<UnitProposal> proposals = new ArrayList<>();
        public int proposalCounter = 0;

        public PendingIncident(EmergencyIncident inc) {
            this.incident = inc;
        }
    }

    @Override
    protected void setup() {
        super.setup();
        logMessage("Dispatcher initialized and ready to coordinate emergency responses");

        addBehaviour(new IncidentReceptionBehaviour());
        addBehaviour(new ProposalEvaluationBehaviour());
    }

    private class IncidentReceptionBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                try {
                    EmergencyIncident incident = (EmergencyIncident) getContentManager()
                        .extractContent(msg);

                    logMessage("Received incident: " + incident);
                    logger.logIncidentReported(incident);
                    pendingIncidents.put(incident.getId(), new PendingIncident(incident));

                    findAvailableUnits(incident);
                } catch (Exception e) {
                    logMessage("Error processing incident: " + e.getMessage());
                }
            } else {
                block();
            }
        }
    }

    private void findAvailableUnits(EmergencyIncident incident) {
        String[] requiredServices = getRequiredServices(incident.getType());

        for (String service : requiredServices) {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(service);
            template.addServices(sd);

            try {
                DFAgentDescription[] results = DFService.search(this, template);

                if (results.length > 0) {
                    logMessage("Found " + results.length + " units for " + service);
                    issueCallForProposal(incident, results);
                } else {
                    logMessage("No units available for service: " + service);
                }
            } catch (FIPAException fe) {
                logMessage("DF search error: " + fe.getMessage());
            }
        }
    }

    private String[] getRequiredServices(IncidentType type) {
        switch (type) {
            case FIRE:
                return new String[]{"FIRE", "RESCUE"};
            case MEDICAL:
                return new String[]{"MEDICAL"};
            case STRUCTURAL_COLLAPSE:
                return new String[]{"FIRE", "RESCUE", "CROWD_CONTROL"};
            default:
                return new String[]{};
        }
    }

    private void issueCallForProposal(EmergencyIncident incident, DFAgentDescription[] units) {
        PendingIncident pending = pendingIncidents.get(incident.getId());
        if (pending == null) return;

        for (DFAgentDescription dfd : units) {
            AID responder = dfd.getName();
            ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
            cfp.addReceiver(responder);
            cfp.setOntology(ontology.getName());
            cfp.setLanguage(codec.getName());
            cfp.setConversationId(incident.getId());
            cfp.setReplyWith("cfp_" + System.currentTimeMillis());

            try {
                getContentManager().fillContent(cfp, incident);
                send(cfp);
                pending.proposalCounter++;
            } catch (Exception e) {
                logMessage("Error sending CFP: " + e.getMessage());
            }
        }
    }

    private class ProposalEvaluationBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                try {
                    String incidentId = msg.getConversationId();
                    PendingIncident pending = pendingIncidents.get(incidentId);

                    if (pending != null) {
                        UnitProposal proposal = (UnitProposal) getContentManager()
                            .extractContent(msg);
                        proposal.setUnitName(msg.getSender().getLocalName());

                        UtilityFunction.calculateUtility(pending.incident, proposal);
                        pending.proposals.add(proposal);

                        logMessage("Received proposal: " + proposal);

                        // If we have proposals from all units, make decision
                        if (pending.proposals.size() >= pending.proposalCounter ||
                            pending.proposalCounter == 0) {
                            selectBestUnit(pending);
                        }
                    }
                } catch (Exception e) {
                    logMessage("Error processing proposal: " + e.getMessage());
                }
            } else {
                block();
            }
        }
    }

    private void selectBestUnit(PendingIncident pending) {
        if (pending.proposals.isEmpty()) {
            logMessage("No proposals for incident: " + pending.incident.getId());
            return;
        }

        UnitProposal bestProposal = pending.proposals.stream()
            .max(Comparator.comparingDouble(UnitProposal::getUtilityScore))
            .get();

        logMessage("Selected unit: " + bestProposal.getUnitName() +
                   " with utility: " + bestProposal.getUtilityScore());
        logger.logIncidentAssigned(pending.incident.getId(), bestProposal.getUnitName(),
            System.currentTimeMillis());

        // Accept selected unit
        AID selectedUnit = new AID(bestProposal.getUnitName(), AID.ISLOCALNAME);
        ACLMessage acceptance = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        acceptance.addReceiver(selectedUnit);
        acceptance.setConversationId(pending.incident.getId());
        acceptance.setOntology(ontology.getName());
        acceptance.setLanguage(codec.getName());

        try {
            getContentManager().fillContent(acceptance, pending.incident);
            send(acceptance);
            assignedIncidents.put(pending.incident.getId(), bestProposal.getUnitName());
        } catch (Exception e) {
            logMessage("Error accepting proposal: " + e.getMessage());
        }

        // Reject other units
        for (UnitProposal proposal : pending.proposals) {
            if (!proposal.getUnitName().equals(bestProposal.getUnitName())) {
                AID rejectedUnit = new AID(proposal.getUnitName(), AID.ISLOCALNAME);
                ACLMessage rejection = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                rejection.addReceiver(rejectedUnit);
                rejection.setConversationId(pending.incident.getId());
                send(rejection);
            }
        }

        pendingIncidents.remove(pending.incident.getId());
    }

    @Override
    protected void takeDown() {
        logger.generateReport("incident_report_" + System.currentTimeMillis() + ".json");
        logMessage("Dispatcher shutting down");
    }
}
