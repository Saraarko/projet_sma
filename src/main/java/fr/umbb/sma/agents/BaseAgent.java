package fr.umbb.sma.agents;

import jade.core.Agent;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import fr.umbb.sma.ontology.EmergencyOntology;

public abstract class BaseAgent extends Agent {
    protected Ontology ontology;
    protected Codec codec;

    @Override
    protected void setup() {
        this.ontology = EmergencyOntology.getInstance();
        this.codec = new SLCodec();
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);

        System.out.println("[" + getLocalName() + "] Agent initialized");
    }

    protected void logMessage(String message) {
        System.out.println("[" + getLocalName() + "] " + message);
    }
}
