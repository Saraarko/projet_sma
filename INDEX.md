# SRUU Project - Complete File Index

## 📚 Documentation Files

### Start Here
1. **[QUICK_START.md](QUICK_START.md)** - 5-minute setup guide (START HERE!)
   - Installation steps
   - Expected output
   - Customization examples
   - Troubleshooting

2. **[README.md](README.md)** - Complete reference manual
   - Project overview
   - Installation & setup
   - Configuration options
   - Performance metrics
   - Debugging guide

3. **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - Delivery checklist
   - Completeness verification
   - Feature list
   - Implementation metrics
   - Quality highlights

### Technical Documentation
4. **[TECHNICAL_REPORT.md](TECHNICAL_REPORT.md)** - Design documentation (9 pages)
   - System architecture
   - Protocols & ontology
   - Dispatcher utility function (with math)
   - FSM state diagrams
   - Robustness analysis
   - Design justifications

5. **[PRESENTATION_OUTLINE.md](PRESENTATION_OUTLINE.md)** - 10-minute presentation
   - Architecture overview (1 min)
   - Demonstration scenarios (6 min)
   - Critical review (3 min)
   - Talking points
   - Time allocation

---

## 🔨 Build & Execution

### Build Scripts
- **[pom.xml](pom.xml)** - Maven build configuration
  - JADE 4.5.0 dependency
  - Java 11 compiler target
  - JAR packaging

- **[BUILD.sh](BUILD.sh)** - Linux/Mac build script
  - Checks Java & Maven
  - Compiles project
  - Verifies JAR creation

- **[BUILD.bat](BUILD.bat)** - Windows build script
  - Same as above for Windows
  - Visual error messages
  - Installation guidance

### Run Scripts
- **[RUN.bat](RUN.bat)** - Windows execution
  - Double-click to run
  - JAR verification
  - Java check

### Manual Build/Run
```bash
# Compile
mvn clean package -DskipTests

# Run
java -jar target/sruu-emergency-system-1.0.0.jar
```

---

## 💻 Source Code

### Main Entry Point
- **[EmergencySystemLauncher.java](src/main/java/fr/umbb/sma/EmergencySystemLauncher.java)**
  - Creates JADE container
  - Starts all 14 agents
  - Configures agent deployment

### Agents (14 total, 9 types)

#### Core Coordinator
1. **[DispatcherAgent.java](src/main/java/fr/umbb/sma/agents/DispatcherAgent.java)**
   - Central decision-maker
   - FIPA Contract Net protocol
   - Utility function evaluation
   - Incident tracking

#### Sensors (Generate Incidents)
2. **[SensorAgent.java](src/main/java/fr/umbb/sma/agents/SensorAgent.java)**
   - 3 instances deployed
   - Random incident generation
   - Fixed locations on 2D grid

#### Intervention Units (Field Responders)
3. **[AmbulanceAgent.java](src/main/java/fr/umbb/sma/agents/AmbulanceAgent.java)**
   - 2 instances deployed
   - Responds to MEDICAL incidents
   - DF registration: MEDICAL service

4. **[FireTruckAgent.java](src/main/java/fr/umbb/sma/agents/FireTruckAgent.java)**
   - 2 instances deployed
   - Responds to FIRE & STRUCTURAL_COLLAPSE
   - DF registration: FIRE & RESCUE services
   - Water reserve tracking (100 units, depletes while active)
   - Auto-abort when water empty

5. **[PoliceUnitAgent.java](src/main/java/fr/umbb/sma/agents/PoliceUnitAgent.java)**
   - 2 instances deployed
   - Perimeter control & crowd management
   - DF registration: CROWD_CONTROL & PERIMETER services
   - Patrol behavior when idle

6. **[BiohazardContainmentUnitAgent.java](src/main/java/fr/umbb/sma/agents/BiohazardContainmentUnitAgent.java)** ⭐ **NEW**
   - 1 instance deployed
   - Handles BIOHAZARD & CRYOGENIC_LEAK incidents
   - DF registration: BIOHAZARD_CONTAINMENT service
   - Containment capacity tracking
   - Specialized response FSM

#### Support Agents
7. **[MedicalCoordinatorAgent.java](src/main/java/fr/umbb/sma/agents/MedicalCoordinatorAgent.java)**
   - Hospital resource management
   - 4 hospitals with bed tracking
   - Ambulance routing

8. **[TrafficControllerAgent.java](src/main/java/fr/umbb/sma/agents/TrafficControllerAgent.java)**
   - Emergency corridor management
   - Route optimization
   - Timeout handling

9. **[LoggerAgent.java](src/main/java/fr/umbb/sma/agents/LoggerAgent.java)**
   - Passive audit agent
   - Generates JSON reports
   - Incident lifecycle tracking

#### Base Class
- **[BaseAgent.java](src/main/java/fr/umbb/sma/agents/BaseAgent.java)**
  - Parent class for all agents
  - Ontology & codec setup
  - Common logging methods

### Domain Ontology (Formal Specification)

Located in `src/main/java/fr/umbb/sma/ontology/`:

1. **[EmergencyOntology.java](src/main/java/fr/umbb/sma/ontology/EmergencyOntology.java)**
   - Central ontology registry
   - XML codec configuration
   - Schema reflection

2. **[EmergencyIncident.java](src/main/java/fr/umbb/sma/ontology/EmergencyIncident.java)**
   - Incident data structure
   - Fields: id, type, severity, location, reportedTime
   - toString for logging

3. **[IncidentType.java](src/main/java/fr/umbb/sma/ontology/IncidentType.java)**
   - Enum: FIRE, MEDICAL, STRUCTURAL_COLLAPSE, BIOHAZARD, CRYOGENIC_LEAK

4. **[UnitStatus.java](src/main/java/fr/umbb/sma/ontology/UnitStatus.java)**
   - Enum: IDLE, RESPONDING, ON_SCENE, ACTIVE, RETURNING, UNAVAILABLE

5. **[Location.java](src/main/java/fr/umbb/sma/ontology/Location.java)**
   - 2D coordinates (x, y)
   - Euclidean distance calculation
   - Grid range: 0-100

6. **[UnitProposal.java](src/main/java/fr/umbb/sma/ontology/UnitProposal.java)**
   - Unit offer to dispatcher
   - Fields: unitName, unitType, location, status, estimatedTime, utilityScore

7. **[HospitalInfo.java](src/main/java/fr/umbb/sma/ontology/HospitalInfo.java)**
   - Hospital capacity model
   - Fields: name, location, totalBeds, availableBeds
   - Bed reservation/release methods

### Utilities

Located in `src/main/java/fr/umbb/sma/utils/`:

1. **[UtilityFunction.java](src/main/java/fr/umbb/sma/utils/UtilityFunction.java)**
   - Dispatcher decision-making algorithm
   - Weighted multi-factor utility: U = Σ(w_i × score_i)
   - Weights: distance (0.35), type_match (0.25), status (0.20), severity (0.20)
   - Component scoring functions
   - Decision explanation for audit trail

2. **[IncidentLogger.java](src/main/java/fr/umbb/sma/utils/IncidentLogger.java)**
   - Incident record management
   - JSON report generation
   - Metrics calculation
   - Event timeline tracking

---

## 📊 Generated Output

### Runtime Logs
- **Console output:** Real-time agent actions
- **Format:** `[AgentName] Message`
- **Examples:** Incident reports, proposals, assignments

### Reports (Generated in `logs/` directory)
- **Files:** `incident_report_*.json`
- **Content:** 
  - Incident details (type, severity, location)
  - Assignment information (unit, time)
  - Response metrics (response time, total time)
  - Event timeline
  - Statistics (completion rate, average response time)

---

## 🎯 File Organization Summary

```
projet_sma/                                 # Root directory
├── Documentation/                          # 5 markdown files
│   ├── QUICK_START.md                     # Start here! (5 min)
│   ├── README.md                          # Full reference
│   ├── TECHNICAL_REPORT.md                # Design (9 pages)
│   ├── PRESENTATION_OUTLINE.md            # Demo (10 min)
│   └── PROJECT_SUMMARY.md                 # Delivery checklist
│
├── Build System/                           # Maven + Scripts
│   ├── pom.xml                            # Maven config
│   ├── BUILD.sh                           # Linux/Mac build
│   ├── BUILD.bat                          # Windows build
│   └── RUN.bat                            # Windows run
│
├── Source Code/                            # 21 Java files
│   └── src/main/java/fr/umbb/sma/
│       ├── EmergencySystemLauncher.java   # Main entry (14 agents)
│       ├── agents/ (10 files)             # Agent implementations
│       ├── ontology/ (7 files)            # Domain model
│       └── utils/ (2 files)               # Utilities
│
└── Runtime Output/                         # Generated
    ├── target/                            # Compiled JAR
    └── logs/                              # JSON reports
```

---

## 🚀 Getting Started

### Recommended Reading Order
1. **QUICK_START.md** (5 minutes) - Get system running
2. **README.md** (15 minutes) - Understand how to use
3. **TECHNICAL_REPORT.md** (20 minutes) - Learn design
4. **PRESENTATION_OUTLINE.md** (5 minutes) - Prepare presentation

### For Instructors
- **PROJECT_SUMMARY.md** - Verify all deliverables
- **TECHNICAL_REPORT.md** - Review design quality
- **Source code** - Verify implementation

### For Presentation
- Follow **PRESENTATION_OUTLINE.md**
- Run system via **RUN.bat** or command line
- Show JSON reports from **logs/** directory
- Discuss design from **TECHNICAL_REPORT.md**

---

## 📋 Quick Reference

### Agent Count by Type
| Type | Count | Key Feature |
|------|-------|---|
| Dispatcher | 1 | Central coordinator |
| Sensor | 3 | Incident detection |
| Ambulance | 2 | Medical response |
| Fire Truck | 2 | Fire/rescue + resource mgmt |
| Police | 2 | Perimeter control |
| Biohazard Unit | 1 | Hazmat (NEW) |
| Support | 3 | Coordination agents |
| **Total** | **14** | **9 types** |

### Core Protocols
- **FIPA-ACL:** Message format
- **Contract Net:** CFP → PROPOSE → ACCEPT/REJECT
- **XML Ontology:** Domain model

### Key Locations
- **Project Root:** `/c/Users/Administrator/Desktop/projet_sma/`
- **Source Code:** `src/main/java/fr/umbb/sma/`
- **Build Output:** `target/sruu-emergency-system-1.0.0.jar`
- **Reports:** `logs/incident_report_*.json`

### Important Files
- **Dispatcher:** `DispatcherAgent.java` (core logic)
- **Utility Function:** `UtilityFunction.java` (decision math)
- **Logging:** `IncidentLogger.java` (reporting)
- **Entry Point:** `EmergencySystemLauncher.java` (agent startup)

---

## ✅ Verification Checklist

Use this to verify completeness:

- [ ] All source files present (21 Java files)
- [ ] Documentation complete (5 markdown files)
- [ ] Build scripts present (pom.xml + BUILD/RUN scripts)
- [ ] Can compile: `mvn clean package`
- [ ] Can run: `java -jar target/sruu-emergency-system-1.0.0.jar`
- [ ] 14 agents start (see console output)
- [ ] Reports generated (check logs/ directory)
- [ ] No compilation errors
- [ ] No deadlocks in execution
- [ ] Technical report present (10 pages)

---

## 📞 Questions About Files?

- **"How do I run it?"** → See QUICK_START.md
- **"How does it work?"** → See TECHNICAL_REPORT.md
- **"How do I present it?"** → See PRESENTATION_OUTLINE.md
- **"Where's the code?"** → See src/main/java/fr/umbb/sma/
- **"Is it complete?"** → See PROJECT_SUMMARY.md

---

**Status:** ✅ All files ready for submission  
**Total Files:** 30 (5 docs + 21 source + 4 build files)  
**Total Size:** ~500KB  
**Ready for:** Compilation, Execution, Presentation

