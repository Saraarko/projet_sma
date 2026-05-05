package fr.umbb.sma.ontology;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;
import fr.umbb.sma.ontology.*;

public class EmergencyOntology extends BeanOntology {
    private static final long serialVersionUID = 1L;
    public static final String ONTOLOGY_NAME = "EmergencyOntology";
    private static Ontology instance = new EmergencyOntology();

    public static Ontology getInstance() {
        return instance;
    }

    private EmergencyOntology() {
        super(ONTOLOGY_NAME);
        try {
            add(EmergencyIncident.class);
            add(IncidentType.class);
            add(UnitStatus.class);
            add(Location.class);
            add(UnitProposal.class);
            add(HospitalInfo.class);
        } catch (BeanOntologyException e) {
            e.printStackTrace();
        }
    }

    public static Codec getCodec() {
        return new SLCodec();
    }
}
