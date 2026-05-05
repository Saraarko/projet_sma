package fr.umbb.sma;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import fr.umbb.sma.utils.IncidentLogger;

public class EmergencySystemLauncher {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Urban Emergency Response System (SRUU)");
        System.out.println("JADE Multi-Agent Implementation");
        System.out.println("========================================\n");

        try {
            Runtime rt = Runtime.instance();
            Profile p = new ProfileImpl();
            p.setParameter(Profile.MAIN_HOST, "localhost");
            p.setParameter(Profile.MAIN_PORT, "8888");
            p.setParameter(Profile.CONTAINER_NAME, "SRUU-Container");

            AgentContainer ac = rt.createMainContainer(p);

            // Pre-register agent positions for the dashboard
            IncidentLogger log = IncidentLogger.getInstance();
            log.registerAgent("Dispatcher", "Dispatcher", 50, 50);
            log.registerAgent("Sensor-1", "Sensor", 20, 30);
            log.registerAgent("Sensor-2", "Sensor", 70, 70);
            log.registerAgent("Sensor-3", "Sensor", 50, 50);
            log.registerAgent("Ambulance-1", "Ambulance", 50, 50);
            log.registerAgent("Ambulance-2", "Ambulance", 40, 80);
            log.registerAgent("FireTruck-1", "FireTruck", 25, 25);
            log.registerAgent("FireTruck-2", "FireTruck", 75, 25);
            log.registerAgent("Police-1", "Police", 75, 75);
            log.registerAgent("Police-2", "Police", 25, 75);
            log.registerAgent("BiohazardCU-1", "Biohazard", 50, 75);
            log.registerAgent("MedicalCoordinator", "MedicalCoordinator", 50, 10);
            log.registerAgent("TrafficController", "TrafficController", 10, 50);

            // Launch HTTP API Agent first
            ac.createNewAgent("HttpApi",
                "fr.umbb.sma.agents.HttpApiAgent",
                new Object[]{}).start();

            // Launch Dispatcher (core agent)
            ac.createNewAgent("Dispatcher",
                "fr.umbb.sma.agents.DispatcherAgent",
                new Object[]{}).start();

            // Launch Sensors (Location X, Y, Interval, Probability)
            ac.createNewAgent("Sensor-1",
                "fr.umbb.sma.agents.SensorAgent",
                new Object[]{20, 30, 2000, 0.8}).start();

            ac.createNewAgent("Sensor-2",
                "fr.umbb.sma.agents.SensorAgent",
                new Object[]{70, 70, 2500, 0.8}).start();

            ac.createNewAgent("Sensor-3",
                "fr.umbb.sma.agents.SensorAgent",
                new Object[]{50, 50, 3000, 0.8}).start();

            // Launch Intervention Units
            ac.createNewAgent("Ambulance-1",
                "fr.umbb.sma.agents.AmbulanceAgent",
                new Object[]{50, 50}).start();

            ac.createNewAgent("Ambulance-2",
                "fr.umbb.sma.agents.AmbulanceAgent",
                new Object[]{40, 80}).start();

            ac.createNewAgent("FireTruck-1",
                "fr.umbb.sma.agents.FireTruckAgent",
                new Object[]{25, 25}).start();

            ac.createNewAgent("FireTruck-2",
                "fr.umbb.sma.agents.FireTruckAgent",
                new Object[]{75, 25}).start();

            ac.createNewAgent("Police-1",
                "fr.umbb.sma.agents.PoliceUnitAgent",
                new Object[]{75, 75}).start();

            ac.createNewAgent("Police-2",
                "fr.umbb.sma.agents.PoliceUnitAgent",
                new Object[]{25, 75}).start();

            // Launch Biohazard Containment Unit
            ac.createNewAgent("BiohazardCU-1",
                "fr.umbb.sma.agents.BiohazardContainmentUnitAgent",
                new Object[]{50, 75}).start();

            // Launch Support Agents
            ac.createNewAgent("MedicalCoordinator",
                "fr.umbb.sma.agents.MedicalCoordinatorAgent",
                new Object[]{}).start();

            ac.createNewAgent("TrafficController",
                "fr.umbb.sma.agents.TrafficControllerAgent",
                new Object[]{}).start();

            ac.createNewAgent("Logger",
                "fr.umbb.sma.agents.LoggerAgent",
                new Object[]{}).start();

            System.out.println("\n[LAUNCHER] All agents started successfully!");
            System.out.println("[LAUNCHER] System running... (Press Ctrl+C to stop)\n");

        } catch (StaleProxyException e) {
            System.err.println("Error starting agents: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
