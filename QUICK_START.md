# Quick Start Guide - SRUU Emergency System

## 5-Minute Setup

### Step 1: Compile (2 minutes)
```bash
cd /c/Users/Administrator/Desktop/projet_sma
mvn clean package -DskipTests
```

**Expected Output:**
```
BUILD SUCCESS
Total time: 45 s
```

### Step 2: Run (1 minute)
```bash
java -jar target/sruu-emergency-system-1.0.0.jar
```

**Wait for:**
```
[LAUNCHER] All agents started successfully!
[LAUNCHER] System running... (Press Ctrl+C to stop)
```

### Step 3: Watch Simulation (2 minutes)
System automatically generates incidents. Watch console for:
- `[Sensor-X] Incident reported`
- `[Dispatcher] Received incident`
- `[Dispatcher] Selected unit`
- `[FireTruck-X] Arrived at incident`
- `[FireTruck-X] Water depleted! Aborting`

### Step 4: Stop & Check Results
Press `Ctrl+C` to stop. Then:

```bash
ls -lh logs/
cat logs/incident_report_*.json | python -m json.tool
```

---

## What You'll See

### Console Output Example
```
[Dispatcher] Received incident: Incident[Sensor-1_1234567890|FIRE|severity:8|(40,50)]
[Dispatcher] Found 2 units for FIRE
[Dispatcher] Received proposal: Proposal[FireTruck-1|FireTruck|dist:15.0|utility:64.49]
[Dispatcher] Received proposal: Proposal[Ambulance-1|Ambulance|dist:10.0|utility:32.15]
[Dispatcher] Selected unit: FireTruck-1 with utility: 64.49
[FireTruck-1] Accepting incident: Sensor-1_1234567890
[FireTruck-1] Moving toward incident... distance: 15.0
[FireTruck-1] Arrived at incident
[FireTruck-1] Fighting fire... water: 85
[FireTruck-1] Fighting fire... water: 70
[FireTruck-1] Water depleted! Aborting and returning to base.
[Dispatcher] Searching for alternative unit for Sensor-1_1234567890
```

### JSON Report Example
```json
{
  "generatedAt": "2026-04-29T10:45:30.123",
  "totalIncidents": 3,
  "statistics": {
    "completed": 2,
    "pending": 1,
    "aborted": 0,
    "averageResponseTimeSeconds": 52.15
  },
  "incidents": [
    {
      "id": "Sensor-1_1714397130000",
      "type": "FIRE",
      "severity": 8,
      "reportedAt": "10:45:30",
      "assignedUnit": "FireTruck-1",
      "status": "COMPLETED",
      "responseTimeSeconds": 45.2
    }
  ]
}
```

---

## 9 Agents Running

| # | Agent | Role | Location |
|---|-------|------|----------|
| 1 | Dispatcher | Coordinator | N/A |
| 2 | Sensor-1 | Detect FIRE | (20, 30) |
| 3 | Sensor-2 | Detect MEDICAL | (70, 70) |
| 4 | Sensor-3 | Detect BIOHAZARD | (50, 50) |
| 5 | Ambulance-1 | Medical Response | (50, 50) |
| 6 | Ambulance-2 | Medical Response | (40, 80) |
| 7 | FireTruck-1 | Fire/Rescue | (25, 25) |
| 8 | FireTruck-2 | Fire/Rescue | (75, 25) |
| 9 | Police-1 | Perimeter Control | (75, 75) |
| 10 | Police-2 | Perimeter Control | (25, 75) |
| 11 | BiohazardCU-1 | Hazmat Response | (50, 75) |
| 12 | MedicalCoordinator | Hospital Mgmt | N/A |
| 13 | TrafficController | Route Optimization | N/A |
| 14 | Logger | Incident Tracking | N/A |

**Total: 14 agents (9 types including BCU)**

---

## Key Features to Observe

### ✅ Feature 1: Optimal Unit Selection
- Dispatcher uses weighted utility function
- Selects best unit based on: distance (35%), type match (25%), status (20%), severity (20%)

### ✅ Feature 2: Parallel Incident Handling
- Multiple sensors can report simultaneously
- Dispatcher handles multiple CFP/PROPOSE cycles in parallel

### ✅ Feature 3: Robustness
- Fire trucks have water reserves (100 units)
- When depleted, sends ABORT message
- Dispatcher automatically searches for replacement unit

### ✅ Feature 4: Comprehensive Logging
- JSON report with incident lifecycle
- Response time calculations
- Event timeline for analysis

### ✅ Feature 5: State Machines
- Each unit has FSM: IDLE → RESPONDING → ON_SCENE → RETURNING → IDLE
- Fire trucks track water reserve state

---

## Customization Examples

### Increase Incident Generation
Edit `SensorAgent.java`:
```java
detectionProbability = 0.8;  // 80% chance per tick (was 30%)
detectionInterval = 2000;    // Every 2 seconds (was 5 seconds)
incidentCounter = 10;        // 10 incidents per sensor (was 3)
```

### Tune Dispatcher Weights
Edit `UtilityFunction.java`:
```java
// Prioritize speed over type matching
private static final double DISTANCE_WEIGHT = 0.50;      // was 0.35
private static final double TYPE_MATCH_WEIGHT = 0.10;    // was 0.25
private static final double STATUS_WEIGHT = 0.20;
private static final double SEVERITY_WEIGHT = 0.20;
```

### Change Unit Speeds
Edit `FireTruckAgent.java`:
```java
private int speed = 5;  // Units per tick (was 3 = faster)
```

### Add New Hospital
Edit `MedicalCoordinatorAgent.initializeHospitals()`:
```java
hospitals.add(new HospitalInfo("Downtown Hospital", 
    new Location(60, 50), 60, 20));
```

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| "Port 8888 already in use" | Kill existing JADE process or change port in code |
| "No incidents generated" | Increase `detectionProbability` or reduce `detectionInterval` |
| "Compilation failed" | Ensure Java 11+ and Maven 3.6+ installed |
| "Report not found" | Check `logs/` folder exists, run longer to generate multiple reports |
| Slow movement | Reduce `detectionInterval` or increase unit `speed` |

---

## Understanding the Output

### Dispatcher Decision Log
```
[Dispatcher] Received proposal: Proposal[FireTruck-1|FireTruck|dist:15.0|utility:64.49]
                                        └─ Unit name         └─ Distance to incident
                                                                  └─ Calculated utility score
```

**Utility Calculation for FireTruck-1:**
- Distance: 15 min → Score: 33.7 × 0.35 = 11.8
- Type match: FIRE → Score: 100 × 0.25 = 25.0
- Status: IDLE → Score: 100 × 0.20 = 20.0
- Severity: 8 → Score: 80 × 0.20 = 16.0
- **Total: 64.49** ✓ (Selected!)

### Fire Truck Water Depletion
```
[FireTruck-1] Water reserve: 100
[FireTruck-1] Water reserve: 85   (decreases by 15 per tick)
[FireTruck-1] Water reserve: 70
[FireTruck-1] Water reserve: 55
[FireTruck-1] Water reserve: 40
[FireTruck-1] Water reserve: 25
[FireTruck-1] Water reserve: 10
[FireTruck-1] Water reserve: 0
[FireTruck-1] Water depleted! Aborting and returning to base.
[Dispatcher] Received FAILURE from FireTruck-1
[Dispatcher] Searching for alternative unit...
[Dispatcher] Selected unit: FireTruck-2 with utility: 58.32
```

### Completion & Response Times
```
[Ambulance-1] Arrived at incident Sensor-1_1234567890
[Logger] [10:45:30] [Ambulance-1] arrived (response time: 45.2 sec)
[Ambulance-1] Incident Sensor-1_1234567890 completed by Ambulance-1
[Logger] [10:45:45] [Ambulance-1] completed (total time: 60.5 sec)
```

---

## Next Steps

1. **Read Technical Report:** `TECHNICAL_REPORT.md` for design details
2. **View Presentation:** `PRESENTATION_OUTLINE.md` for 10-minute demo script
3. **Analyze Reports:** Check `logs/incident_report_*.json` for metrics
4. **Modify & Experiment:** Adjust agent parameters and rerun

---

## Expected Run Time

- **Compilation:** 30-45 seconds (first time), 5-10 seconds (subsequent)
- **Startup:** 3-5 seconds
- **Simulation:** 30-60 seconds (enough for 3 incidents + processing)
- **Report Generation:** 1-2 seconds

**Total Time:** ~2 minutes to see complete simulation

---

## Success Criteria

You'll know it's working when you see:

✅ 14 agents initialized (9 types)
✅ At least 1 incident reported by sensors
✅ Dispatcher selects a unit (PROPOSE/ACCEPT flow)
✅ Unit moves toward incident (console shows location updates)
✅ JSON report generated in `logs/` folder
✅ Report contains incident details and response time metrics
✅ No errors or crashes during run

---

## Commands Cheat Sheet

```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/sruu-emergency-system-1.0.0.jar

# View reports
ls logs/
cat logs/incident_report_*.json | python -m json.tool

# Count incidents
grep -c "\"completed\"" logs/incident_report_*.json

# Search for errors
grep -i "error\|exception" logs/*.json

# Monitor while running (in another terminal)
tail -f console.log | grep "Dispatcher"
```

---

**You're ready to go!** 🚨 Start the system and watch the magic happen.
