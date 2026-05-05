# SRUU - Système de Réponse aux Urgences Urbaines
## Urban Emergency Response System with JADE

A sophisticated multi-agent system for coordinating emergency response in urban environments using JADE (Java Agent Development Framework).

### Project Overview

This project implements a simulation of urban emergency response coordination with 9 distinct agent types:

1. **Dispatcher Agent** - Central coordinator making optimal unit assignments
2. **Sensor Agents** (3x) - Detect and report incidents
3. **Ambulance Agents** (2x) - Medical emergency response
4. **Fire Truck Agents** (2x) - Fire and rescue operations
5. **Police Unit Agents** (2x) - Perimeter control and crowd management
6. **Biohazard Containment Unit** (1x) - Specialized hazmat response
7. **Medical Coordinator Agent** - Hospital resource management
8. **Traffic Controller Agent** - Emergency route optimization
9. **Logger Agent** - Incident tracking and reporting

---

## Technical Stack

- **Framework:** JADE 4.5.0
- **Language:** Java 11+
- **Build Tool:** Maven 3.6+
- **Communication:** FIPA-ACL with XML Ontology
- **Architecture:** Hierarchical dispatcher-based

---

## Prerequisites

- Java Development Kit (JDK) 11 or higher
- Maven 3.6 or higher
- ~200MB free disk space

### Verify Installation

```bash
java -version
mvn -version
```

---

## Installation & Setup

### 1. Clone or Download Project

```bash
cd /path/to/projet_sma
```

### 2. Build Project

```bash
mvn clean package
```

This will:
- Download JADE and dependencies (~100MB)
- Compile all Java sources
- Run unit tests
- Create executable JAR in `target/`

### 3. Project Structure

```
projet_sma/
├── pom.xml                                  # Maven configuration
├── src/main/java/fr/umbb/sma/
│   ├── EmergencySystemLauncher.java        # Main entry point
│   ├── agents/
│   │   ├── BaseAgent.java                  # Parent class
│   │   ├── DispatcherAgent.java            # Core coordinator
│   │   ├── SensorAgent.java                # Incident detection
│   │   ├── AmbulanceAgent.java             # Medical response
│   │   ├── FireTruckAgent.java             # Fire/rescue
│   │   ├── PoliceUnitAgent.java            # Perimeter control
│   │   ├── BiohazardContainmentUnitAgent.java  # Hazmat (NEW)
│   │   ├── MedicalCoordinatorAgent.java    # Hospital coordination
│   │   ├── TrafficControllerAgent.java     # Route optimization
│   │   └── LoggerAgent.java                # Audit logging
│   ├── ontology/
│   │   ├── EmergencyOntology.java          # Domain ontology
│   │   ├── EmergencyIncident.java          # Incident definition
│   │   ├── IncidentType.java               # Enum: FIRE, MEDICAL, etc.
│   │   ├── UnitStatus.java                 # Enum: IDLE, RESPONDING, etc.
│   │   ├── Location.java                   # 2D coordinates
│   │   ├── UnitProposal.java               # Unit offer
│   │   └── HospitalInfo.java               # Hospital capacity
│   └── utils/
│       ├── IncidentLogger.java             # Logging infrastructure
│       └── UtilityFunction.java            # Dispatcher decision logic
├── TECHNICAL_REPORT.md                     # Design documentation (10 pages)
└── PRESENTATION_OUTLINE.md                 # Presentation guide

target/
└── sruu-emergency-system-1.0.0.jar        # Executable JAR
```

---

## Running the System

### Quick Start

```bash
# Option 1: Using Maven
mvn exec:java -Dexec.mainClass="fr.umbb.sma.EmergencySystemLauncher"

# Option 2: Using compiled JAR
java -jar target/sruu-emergency-system-1.0.0.jar

# Option 3: With custom JADE settings
java -Dcom.tilab.jade.imtp.leap.LEAPIMTPManager.defaultport=8888 \
     -jar target/sruu-emergency-system-1.0.0.jar
```

### Expected Output

```
========================================
Urban Emergency Response System (SRUU)
JADE Multi-Agent Implementation
========================================

[Dispatcher] Agent initialized
[Sensor-1] Agent initialized
[Sensor-2] Agent initialized
[Sensor-3] Agent initialized
[Ambulance-1] Agent initialized
[Ambulance-2] Agent initialized
[FireTruck-1] Agent initialized
[FireTruck-2] Agent initialized
[Police-1] Agent initialized
[Police-2] Agent initialized
[BiohazardCU-1] Agent initialized
[MedicalCoordinator] Agent initialized
[TrafficController] Agent initialized
[Logger] Agent initialized

[LAUNCHER] All agents started successfully!
[LAUNCHER] System running... (Press Ctrl+C to stop)

[Sensor-1] Sensor deployed at (20,30)...
[Dispatcher] Received incident: Incident[...]
[Dispatcher] Sending CFP to available units...
...
```

---

## Simulation Scenarios

The system automatically generates 3 test incidents per sensor (9 total) distributed across:

### Scenario 1: Multi-Type Incidents
- **Trigger:** Different incident types (FIRE, MEDICAL, BIOHAZARD)
- **Expected:** Correct unit type selection via utility function
- **Observation:** Check response times and type matching

### Scenario 2: Simultaneous Incidents
- **Trigger:** Multiple incidents within short time window
- **Expected:** Parallel processing, no deadlocks
- **Observation:** Verify multiple units dispatch simultaneously

### Scenario 3: Resource Constraints
- **Trigger:** Fire truck water depletion
- **Expected:** ABORT message, automatic reassignment
- **Observation:** Robustness handling unit failure

---

## Output & Reports

### Real-Time Logs
- Console output shows all agent actions
- Each message prefixed with `[AgentName]`
- Timestamp shows system clock

### JSON Report Generation
Generated in `logs/` directory:

```json
{
  "generatedAt": "2026-04-29T10:45:00",
  "totalIncidents": 3,
  "incidents": [
    {
      "id": "Sensor-1_1234567890",
      "type": "FIRE",
      "severity": 8,
      "reportedAt": "10:23:45",
      "assignedUnit": "FireTruck-1",
      "status": "COMPLETED",
      "responseTimeSeconds": 45.2
    }
  ],
  "statistics": {
    "completed": 3,
    "pending": 0,
    "aborted": 0,
    "averageResponseTimeSeconds": 52.3
  },
  "eventLog": [
    "[10:23:45] [Sensor-1] Incident detected...",
    "[10:23:46] [Dispatcher] Evaluating proposals...",
    "[10:24:30] [FireTruck-1] Arrived at incident..."
  ]
}
```

### View Reports
```bash
# List generated reports
ls -lh logs/

# Pretty-print JSON
cat logs/incident_report_*.json | python -m json.tool

# Count incidents
grep -c "\"id\"" logs/incident_report_*.json
```

---

## Configuration

### Modifying Incident Generation
Edit `SensorAgent.java`:
```java
private int incidentCounter = 0;  // Max incidents per sensor
private double detectionProbability = 0.3;  // 0.0-1.0
private int detectionInterval = 5000;  // milliseconds
```

### Adjusting Unit Speeds
Edit individual agent files:
```java
private int speed = 2;  // units per movement tick
```

Speeds:
- Ambulance: 2 units/tick
- FireTruck: 3 units/tick
- Police: 3 units/tick
- BiohazardCU: 2 units/tick

### Tuning Utility Function Weights
Edit `UtilityFunction.java`:
```java
private static final double DISTANCE_WEIGHT = 0.35;
private static final double TYPE_MATCH_WEIGHT = 0.25;
private static final double STATUS_WEIGHT = 0.20;
private static final double SEVERITY_WEIGHT = 0.20;
```

Weights must sum to 1.0. Increase weight to prioritize that factor.

### Hospital Configuration
Edit `MedicalCoordinatorAgent.initializeHospitals()`:
```java
hospitals.add(new HospitalInfo("Hospital Name", 
    new Location(x, y), totalBeds, availableBeds));
```

---

## Debugging

### Enable Verbose Logging
```bash
JADE_VERBOSE=true java -jar target/sruu-emergency-system-1.0.0.jar
```

### Monitor Agent Communications
Check for stuck agents:
```bash
# Look for agents not progressing
watch -n 2 'grep "RESPONDING\|ON_SCENE" <(tail -50 console.log)'
```

### Common Issues

**Issue:** "Address already in use: 8888"
```bash
# Change JADE port in EmergencySystemLauncher.java
p.setParameter(Profile.MAIN_PORT, "8889");
```

**Issue:** OutOfMemory
```bash
# Increase heap size
java -Xmx512m -jar target/sruu-emergency-system-1.0.0.jar
```

**Issue:** No incidents generated
```bash
# Reduce detectionProbability threshold in SensorAgent
detectionProbability = 0.8;  // 80% chance per cycle
```

---

## Testing

### Unit Tests
```bash
mvn test
```

### Manual Testing Checklist
- [ ] All 9 agents start successfully
- [ ] Sensors generate incidents
- [ ] Dispatcher receives and processes incidents
- [ ] CFP/PROPOSE/ACCEPT flow completes
- [ ] Units move toward incident locations
- [ ] JSON report generated with valid metrics
- [ ] No deadlocks observed (> 2 simultaneous incidents)
- [ ] Fire truck water depletion triggers abort
- [ ] Alternative unit reassigned after abort

---

## Performance Metrics

### Expected System Performance
- **Agent Startup Time:** < 5 seconds
- **Decision Time (Dispatcher):** 100-500ms per incident
- **Movement Simulation:** 1 second tick interval
- **Average Response Time:** 45-60 seconds per incident
- **Throughput:** 3-5 incidents/minute
- **Deadlock Risk:** None (by design)

### Memory Usage
- Base system: ~100MB
- Per additional agent: ~2-5MB
- JSON reports: ~50-100KB per incident

---

## Troubleshooting

### Agents Not Starting
1. Check Java version: `java -version` (need 11+)
2. Verify JADE in classpath: `mvn dependency:tree`
3. Check for port conflicts: `netstat -an | grep 8888`

### No Incidents Detected
1. Increase `detectionProbability` in SensorAgent
2. Reduce `detectionInterval` in SensorAgent
3. Check sensor location coverage (0-100 x 0-100 grid)

### Reports Not Generated
1. Verify `logs/` directory exists: `mkdir -p logs`
2. Check write permissions: `touch logs/test.txt`
3. Inspect Logger agent startup messages

### Slow Response Times
1. Reduce movement tick interval (faster iteration)
2. Increase unit speeds
3. Place units closer to incident locations

---

## Documentation

- **TECHNICAL_REPORT.md** - Complete design documentation (10 pages)
  - Architecture overview
  - Utility function mathematics
  - FSM state diagrams
  - Robustness analysis
  - Design decisions & justifications

- **PRESENTATION_OUTLINE.md** - Presentation script (10 minutes)
  - Overview section (1 min)
  - Demonstration scenarios (6 min)
  - Critical review (3 min)
  - Talking points & time allocation

---

## Project Deliverables

✅ **Working Implementation**
- 9 fully functional agent types
- FIPA-compliant communication
- Formal ontology-based messaging
- Contract Net protocol

✅ **Technical Report** (10 pages max)
- System architecture
- Protocols and ontology
- Utility function mathematics
- FSM behavior models
- Robustness analysis
- Design justifications

✅ **Demonstration Ready**
- 3 test scenarios (simultaneous incidents, water depletion, report analysis)
- JSON report with response time metrics
- Incident lifecycle tracking
- No deadlocks or crashes

---

## Team Information

**Project:** Système de Réponse aux Urgences Urbaines (SRUU)  
**Team Size:** 1 person (Sara Arkoub)  
**Institution:** UMBB - Département d'Informatique (M1 Génie Logiciel)  
**Duration:** ~8 hours  
**Last Updated:** April 29, 2026

---

## License

This is an educational project for UMBB Computer Science Department.

---

## Support

For issues or questions:
1. Check TECHNICAL_REPORT.md for design details
2. Review agent source code comments
3. Inspect JSON reports for incident tracking
4. Run with increased verbosity for debugging

---

## Future Enhancements

1. Web-based UI dashboard
2. Predictive incident modeling
3. Multi-dispatcher hierarchical coordination
4. Real-time performance metrics
5. Machine learning weight optimization
6. Cross-city incident sharing

---

**Ready to dispatch!** 🚨
#   p r o j e t _ s m a  
 #   p r o j e t _ s m a  
 