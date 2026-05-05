package fr.umbb.sma.agents;

import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import fr.umbb.sma.utils.IncidentLogger;

public class LoggerAgent extends BaseAgent {
    private IncidentLogger logger = IncidentLogger.getInstance();
    private long startTime = System.currentTimeMillis();

    @Override
    protected void setup() {
        super.setup();
        logMessage("Logger Agent initialized - passive audit system active");

        addBehaviour(new EventListenerBehaviour());
        addBehaviour(new ReportGenerationBehaviour(this, 30000));
    }

    private class EventListenerBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                String content = msg.getContent();
                logger.logEvent("[" + msg.getSender().getLocalName() + "] " + content);
            } else {
                block();
            }
        }
    }

    private class ReportGenerationBehaviour extends TickerBehaviour {
        private LoggerAgent agent;
        private int reportCount = 0;

        public ReportGenerationBehaviour(LoggerAgent a, long period) {
            super(a, period);
            agent = a;
        }

        @Override
        protected void onTick() {
            reportCount++;
            String filename = "incident_report_" + reportCount + "_" + System.currentTimeMillis() + ".json";
            agent.logger.generateReport(filename);
        }
    }

    @Override
    protected void takeDown() {
        logger.generateReport("final_incident_report_" + System.currentTimeMillis() + ".json");
        long duration = System.currentTimeMillis() - startTime;
        logMessage("Logger shutting down. Total simulation time: " + (duration / 1000.0) + " seconds");
    }
}
