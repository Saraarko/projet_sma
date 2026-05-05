# 🪟 **Windows: Step-by-Step Guide to Run SRUU**

## ✅ **Prerequisites Check**

Before running, you need Java and Maven installed. Check this first:

### **1. Check Java Installation**

Open Command Prompt (`Win + R` → type `cmd` → Enter):

```cmd
java -version
```

**Expected Output:**
```
java version "11.0.0" or higher
Java(TM) SE Runtime Environment
```

**If "java is not recognized":**
- Download Java: https://www.oracle.com/java/technologies/downloads/
- Install JDK 11 or higher
- Restart Command Prompt after installation

---

### **2. Check Maven Installation**

In Command Prompt:

```cmd
mvn --version
```

**Expected Output:**
```
Apache Maven 3.6.0 or higher
```

**If "mvn is not recognized":**
- Download Maven: https://maven.apache.org/download.cgi
- Extract to a folder (e.g., `C:\apache-maven-3.9.0`)
- Add to System PATH:
  1. Right-click "This PC" → Properties
  2. Click "Advanced system settings"
  3. Click "Environment Variables"
  4. Under "System variables", click "New"
  5. Variable name: `MAVEN_HOME`
  6. Variable value: `C:\apache-maven-3.9.0` (your path)
  7. Click OK
  8. Edit "Path" variable, add: `C:\apache-maven-3.9.0\bin`
  9. Restart Command Prompt

---

## 🚀 **Running the System**

### **Step 1: Open Command Prompt**

Press `Win + R`, type `cmd`, press Enter.

You should see:
```
C:\Users\YourName>
```

---

### **Step 2: Navigate to Project**

```cmd
cd C:\Users\Administrator\Desktop\projet_sma
```

Press Enter. You should see:
```
C:\Users\Administrator\Desktop\projet_sma>
```

---

### **Step 3: Build the Project (First Time Only)**

```cmd
BUILD.bat
```

Press Enter.

**Expected Output (takes 30-60 seconds):**

```
==========================================
Building SRUU Emergency System
==========================================

[1/3] Checking Java installation...
Visual Java found: 11.0.0
[2/3] Checking Maven installation...
Visual Maven found: Apache Maven 3.9.0
[3/3] Compiling project (this may take 1-2 minutes)...

[INFO] Scanning for projects...
[INFO] 
[INFO] ------------------------< fr.umbb.sma:sruu-emergency-system >------------------------
[INFO] Building SRUU - Urban Emergency Response System 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] Cleaning resources
[INFO] Downloading from central: ...
[INFO] Downloaded ...
[INFO] Compiling 21 source files ...
[INFO] Building jar: C:\Users\Administrator\Desktop\projet_sma\target\sruu-emergency-system-1.0.0.jar
[INFO] 
[INFO] BUILD SUCCESS
[INFO] Total time: 45.234 s
[INFO] Finished at: 2026-04-29T10:45:30
[INFO] BUILD SUCCESS

JAR location: target\sruu-emergency-system-1.0.0.jar
JAR size: 45 MB

To run the system:
  java -jar target\sruu-emergency-system-1.0.0.jar

Or double-click: RUN.bat
```

**If you see "BUILD SUCCESS":** ✅ Compilation complete!

**If you see errors:**
- Check Java version is 11+ (`java -version`)
- Check Maven installed (`mvn --version`)
- Try again: `mvn clean package -DskipTests`

---

### **Step 4: Run the System**

```cmd
RUN.bat
```

Or alternatively:
```cmd
java -jar target\sruu-emergency-system-1.0.0.jar
```

Press Enter.

---

## 📊 **Expected Output (Real-Time Simulation)**

### **Phase 1: Initialization (First 5 seconds)**

```
==========================================
Urban Emergency Response System (SRUU)
JADE Multi-Agent Implementation
==========================================

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

[Dispatcher] Dispatcher initialized and ready to coordinate emergency responses
[Sensor-1] Sensor deployed at (20,30), detection interval: 3000ms
[Sensor-2] Sensor deployed at (70,70), detection interval: 3500ms
[Sensor-3] Sensor deployed at (50,50), detection interval: 4000ms
[MedicalCoordinator] Medical Coordinator initialized with 4 hospitals
[TrafficController] Traffic Controller initialized
[Logger] Logger Agent initialized - passive audit system active
```

✅ **All 14 agents running**

---

### **Phase 2: Incident Detection (Seconds 10-20)**

```
[Sensor-1] Incident reported: Incident[Sensor-1_1714397130000|FIRE|severity:8|(35,45)]
[Logger] [10:45:30] [Sensor-1] INCIDENT_REPORTED: Incident[Sensor-1_1714397130000|FIRE|...
[Dispatcher] Received incident: Incident[Sensor-1_1714397130000|FIRE|severity:8|(35,45)]
[Dispatcher] Found 2 units for FIRE
[Dispatcher] Sending CFP to available units...

[Sensor-2] Incident reported: Incident[Sensor-2_1714397135000|MEDICAL|severity:5|(68,72)]
[Logger] [10:45:35] [Sensor-2] INCIDENT_REPORTED: Incident[Sensor-2_1714397135000|MEDICAL|...
[Dispatcher] Received incident: Incident[Sensor-2_1714397135000|MEDICAL|severity:5|(68,72)]
[Dispatcher] Found 2 units for MEDICAL
[Dispatcher] Sending CFP to available units...
```

✅ **Multiple incidents detected (FIRE + MEDICAL)**

---

### **Phase 3: Unit Proposals (Seconds 20-25)**

```
[FireTruck-1] Sent proposal for Sensor-1_1714397130000 at distance 15.0
[Logger] [10:45:32] [FireTruck-1] Sent proposal for incident...
[Ambulance-1] Sent proposal for Sensor-1_1714397130000 at distance 25.3

[Ambulance-1] Sent proposal for Sensor-2_1714397135000 at distance 5.2
[Ambulance-2] Sent proposal for Sensor-2_1714397135000 at distance 28.5
```

✅ **Units respond with proposals**

---

### **Phase 4: Dispatcher Decision (Seconds 25-28)**

```
[Dispatcher] Received proposal: Proposal[FireTruck-1|FireTruck|dist:15.0|utility:64.49]
[Dispatcher] Received proposal: Proposal[Ambulance-1|Ambulance|dist:25.3|utility:32.15]
[Dispatcher] Received proposal: Proposal[Ambulance-1|Ambulance|dist:5.2|utility:78.34]
[Dispatcher] Received proposal: Proposal[Ambulance-2|Ambulance|dist:28.5|utility:28.92]

[Dispatcher] Selected unit: FireTruck-1 with utility: 64.49
[Logger] [10:45:33] [Dispatcher] INCIDENT_ASSIGNED: Sensor-1_... -> FireTruck-1 (response time: 3.00 sec)
[Dispatcher] Selected unit: Ambulance-1 with utility: 78.34
[Logger] [10:45:36] [Dispatcher] INCIDENT_ASSIGNED: Sensor-2_... -> Ambulance-1 (response time: 1.00 sec)
```

✅ **Dispatcher selects optimal units based on utility function**

---

### **Phase 5: Unit Movement (Seconds 30-50)**

```
[FireTruck-1] Accepting incident: Sensor-1_1714397130000
[FireTruck-1] Moving toward incident... distance: 15.0
[FireTruck-1] Moving toward incident... distance: 12.0
[FireTruck-1] Moving toward incident... distance: 9.0
[FireTruck-1] Moving toward incident... distance: 6.0
[FireTruck-1] Moving toward incident... distance: 3.0
[FireTruck-1] Moving toward incident... distance: 1.0
[FireTruck-1] Arrived at incident Sensor-1_1714397130000
[Logger] [10:45:50] [FireTruck-1] UNIT_ARRIVED: FireTruck-1 at incident... (arrival time: 20.00 sec)

[Ambulance-1] Accepting incident: Sensor-2_1714397135000
[Ambulance-1] Moving toward incident... distance: 5.2
[Ambulance-1] Moving toward incident... distance: 2.6
[Ambulance-1] Arrived at incident Sensor-2_1714397135000
[Logger] [10:45:45] [Ambulance-1] UNIT_ARRIVED: Ambulance-1 at incident... (arrival time: 10.00 sec)
```

✅ **Units moving toward incidents (watch distance decrease)**

---

### **Phase 6: Active Intervention (Seconds 50-65)**

```
[FireTruck-1] On scene at incident
[FireTruck-1] Fighting fire... water: 100
[FireTruck-1] Fighting fire... water: 85
[FireTruck-1] Fighting fire... water: 70
[FireTruck-1] Fighting fire... water: 55
[FireTruck-1] Fighting fire... water: 40

[Ambulance-1] On scene at incident
[Ambulance-1] Providing medical care...
[Ambulance-1] Transporting patient to hospital...
[Ambulance-1] Incident Sensor-2_1714397135000 completed by Ambulance-1
[Logger] [10:46:05] [Ambulance-1] INCIDENT_COMPLETED: Sensor-2_... (total time: 30.00 sec)
```

✅ **Units handling incidents (water depletion visible)**

---

### **Phase 7: Robustness Test - Water Depletion (If present)**

```
[FireTruck-1] Fighting fire... water: 25
[FireTruck-1] Fighting fire... water: 10
[FireTruck-1] Water depleted! Aborting and returning to base.
[Logger] [10:46:10] [FireTruck-1] INCIDENT_ABORTED: Sensor-1_... - water reserve depleted
[Dispatcher] Received FAILURE from FireTruck-1
[Dispatcher] Searching for alternative unit for Sensor-1_...

[FireTruck-2] Sent proposal for Sensor-1_1714397130000 at distance 32.0
[Dispatcher] Selected unit: FireTruck-2 with utility: 58.32
[FireTruck-2] Accepting incident: Sensor-1_1714397130000
[FireTruck-2] Moving toward incident...
[FireTruck-2] Arrived at incident Sensor-1_1714397130000
[FireTruck-2] Incident Sensor-1_1714397130000 completed by FireTruck-2
[Logger] [10:46:35] [FireTruck-2] INCIDENT_COMPLETED: Sensor-1_... (total time: 65.00 sec)
```

✅ **Robustness: Auto-abort and reallocation working!**

---

### **Phase 8: Completion & Return**

```
[FireTruck-1] Returned to base and refueled water
[FireTruck-2] Returned to base and refueled water
[Ambulance-1] Returned to base
[Police-1] Returned to station
[Police-2] Returned to station
```

✅ **All units back to base (FSM RETURNING → IDLE)**

---

## 🛑 **Stop the System**

Press `Ctrl + C`:

```
^C
[Dispatcher] Dispatcher shutting down
[Logger] Generated report at: logs/incident_report_1714397160000.json
[Logger] Logger shutting down. Total simulation time: 120.5 seconds
[System] All agents terminated
```

---

## 📊 **Check the Report**

After stopping, check the generated JSON report:

```cmd
cd logs
dir
```

You'll see:
```
incident_report_1714397160000.json    45 KB
```

To view the report (formatted):

```cmd
python -m json.tool < incident_report_1714397160000.json
```

Or open it in any text editor:
```cmd
notepad incident_report_1714397160000.json
```

**Example Report Content:**
```json
{
  "generatedAt": "2026-04-29T10:46:35",
  "totalIncidents": 3,
  "incidents": [
    {
      "id": "Sensor-1_1714397130000",
      "type": "FIRE",
      "severity": 8,
      "reportedAt": "10:45:30",
      "assignedUnit": "FireTruck-1",
      "status": "COMPLETED",
      "responseTimeSeconds": 45.2
    },
    {
      "id": "Sensor-2_1714397135000",
      "type": "MEDICAL",
      "severity": 5,
      "reportedAt": "10:45:35",
      "assignedUnit": "Ambulance-1",
      "status": "COMPLETED",
      "responseTimeSeconds": 30.1
    }
  ],
  "statistics": {
    "completed": 3,
    "pending": 0,
    "aborted": 1,
    "averageResponseTimeSeconds": 52.3
  }
}
```

✅ **Report shows all incidents and metrics!**

---

## ✅ **Success Checklist**

After running, you should have:

- [x] All 14 agents initialized
- [x] 3-9 incidents generated
- [x] Multiple units responding
- [x] Dispatcher selecting best units
- [x] Units moving toward incidents
- [x] Incidents completed
- [x] Fire truck water depletion visible
- [x] Unit reallocation on failure
- [x] JSON reports generated
- [x] Response time metrics calculated
- [x] Zero deadlocks
- [x] Clean shutdown

---

## 🐛 **Troubleshooting**

### **Problem: "javac is not recognized"**
→ Java not installed. Download: https://www.oracle.com/java/technologies/downloads/

### **Problem: "mvn is not recognized"**
→ Maven not installed. Download: https://maven.apache.org/download.cgi

### **Problem: "BUILD FAILED"**
→ Run: `mvn clean package` (without -DskipTests)
→ Look for error message about missing dependencies

### **Problem: "Port 8888 already in use"**
→ Close other JADE applications or change port in code

### **Problem: "No incidents generated"**
→ Wait 30+ seconds (incident generation is probabilistic)

### **Problem: "No reports found"**
→ Check `logs` directory exists: `mkdir logs`
→ Run system for at least 30 seconds

---

## 📝 **Next Steps**

After running successfully:

1. **Read the Technical Report:** `TECHNICAL_REPORT.md`
2. **Prepare Presentation:** Follow `PRESENTATION_OUTLINE.md`
3. **Analyze Reports:** Check JSON files in `logs/`
4. **Customize:** Edit agent parameters and rerun

---

**Ready? Just follow these steps and watch the system coordinate emergencies!** 🚨
