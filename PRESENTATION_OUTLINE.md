# SRUU - Urban Emergency Response System
## Presentation Outline (10 minutes max)

---

## PART 1: OVERVIEW (1 minute)

### Key Architecture Points
- **9 Agent Types:** Sensors, Dispatcher, 2 Ambulances, 2 Fire Trucks, 2 Police, 1 Biohazard Unit
- **Core Innovation:** Dispatcher with weighted utility function for optimal unit assignment
- **Communication:** FIPA-ACL messages with Emergency Ontology
- **Robustness:** Asynchronous behaviors prevent deadlocks

### Quick Wins
1. Formal FIPA Contract Net protocol
2. Service discovery via Directory Facilitator
3. State machine FSM for all intervention units
4. Real-time incident tracking and JSON reporting

---

## PART 2: PRACTICAL DEMONSTRATION (6 minutes)

### Scenario 1: Simultaneous Fire + Medical Incident (2 min)
**Setup:**
- Sensor-1 reports FIRE at location (40, 50), severity 8
- Sensor-2 reports MEDICAL at location (70, 60), severity 6

**Expected Flow:**
1. Dispatcher receives both INFORM messages
2. Issues CFP to available Fire Trucks (for FIRE)
3. Issues CFP to available Ambulances (for MEDICAL)
4. Both receive PROPOSE messages with estimated travel times
5. Dispatcher calculates utility scores and sends ACCEPT_PROPOSAL
6. Units move toward incidents (watch movement in log)
7. Both complete and return to base

**Key Observation:** System handles parallelism - no resource conflicts

---

### Scenario 2: Resource Reallocation - Fire Truck Water Depletion (2 min)
**Setup:**
- Sensor reports FIRE incident at (30, 60), severity 9
- FireTruck-1 assigned and responds

**Expected Flow:**
1. FireTruck-1 moves to incident (3 units/tick speed)
2. Arrives on scene and enters ACTIVE state
3. Water reserve decreases each tick: 100 → 85 → 70 → 55 → 40 → 25 → 10 → DEPLETED
4. Sends ABORT message to Dispatcher when water = 0
5. Dispatcher searches for secondary unit (FireTruck-2)
6. Reassigns incident to FireTruck-2
7. FireTruck-2 completes incident while FireTruck-1 returns to refuel

**Key Observation:** Dynamic reallocation without deadlock - shows robustness

---

### Scenario 3: Report Generation & Metrics Analysis (2 min)
**Setup:**
- All three scenarios completed
- Check generated JSON report

**Expected Output:**
```json
{
  "generatedAt": "2026-04-29T10:45:00",
  "totalIncidents": 3,
  "statistics": {
    "completed": 3,
    "pending": 0,
    "aborted": 1,
    "averageResponseTimeSeconds": 52.3
  },
  "incidents": [
    {
      "id": "Sensor-1_...",
      "type": "FIRE",
      "severity": 8,
      "assignedUnit": "FireTruck-1",
      "status": "COMPLETED",
      "responseTimeSeconds": 45.2
    },
    ...
  ]
}
```

**Key Metrics to Highlight:**
- Average response time: 45-60 seconds
- 0 deadlocks
- All units properly assigned
- Abort handling documented

---

## PART 3: CRITICAL REVIEW (3 minutes)

### What Worked Well ✅
1. **Dispatcher Utility Function**
   - Successfully balanced 4 factors (distance, type match, status, severity)
   - Produced optimal assignments in all test cases
   - Easily tunable for different priorities

2. **FIPA Contract Net Protocol**
   - Clean separation of concerns
   - No hard-coded agent names (uses DF)
   - Extensible for future services

3. **Robustness & Concurrency**
   - ConcurrentHashMap prevents race conditions
   - Non-blocking message receives prevent deadlock
   - Graceful handling of unit failures (water depletion)

4. **Biohazard Containment Unit**
   - Successfully integrated 9th agent type
   - Handles BIOHAZARD and CRYOGENIC_LEAK incidents
   - Separate containment capacity tracking

### Technical Challenges Overcome 🔧
1. **Challenge:** State coordination across distributed agents
   - **Solution:** FSM with strict state transitions + INFORM messages for status updates

2. **Challenge:** Preventing dispatcher bottleneck
   - **Solution:** Asynchronous proposal evaluation via CyclicBehaviour (non-blocking)

3. **Challenge:** Handling unit failures mid-incident
   - **Solution:** FAILURE message + automatic reassignment search

4. **Challenge:** Realistic movement physics
   - **Solution:** Incremental position updates in TickerBehaviour (1-second ticks)

### Lessons Learned 📚
1. Formal ontologies are essential for multi-agent system clarity
2. Weight tuning in utility functions is domain-specific (no "one size fits all")
3. Asynchronous patterns scale better than synchronous RPC-style calls
4. JSON logging enables post-simulation analysis and debugging

### Future Enhancements 🚀
1. **Predictive Dispatch:** ML model to predict incident types based on location
2. **Multi-Dispatcher Failover:** Backup coordinator for high-availability
3. **Traffic Simulation:** Congestion model affects movement speeds
4. **Incident Priority Queue:** Pre-emption for critical incidents
5. **Cross-City Coordination:** Multiple dispatchers per region
6. **Real-time Dashboard:** Web UI showing live incident status

---

## TALKING POINTS

### On Utility Function Weights
*"We weighted distance at 35% because proximity is critical in emergencies. Type matching is 25% because a slightly distant fire truck beats a nearby ambulance for a fire. Status is 20% because an idle unit is much more reliable than one already active. Severity is 20% to ensure critical incidents get appropriate resources. These weights are based on emergency response best practices and can be adjusted per city requirements."*

### On Biohazard Unit Integration
*"The BCU was added to handle specialized incidents like BIOHAZARD and CRYOGENIC_LEAK. It registers with the DF under BIOHAZARD_CONTAINMENT service and follows the same FSM pattern as other units. In the utility function, it receives perfect score (100) for matching incident types, but lower proximity scores (2 units/tick speed) since contamination prevention is more critical than speed."*

### On System Scalability
*"The design scales linearly with number of units. Each additional unit registers with DF and responds to CFP calls independently. Dispatcher load remains constant O(n) for n proposals received. The main bottleneck would be the single dispatcher for incidents > 50/minute, which could be addressed with hierarchical dispatchers per zone."*

---

## DEMO COMMANDS TO RUN

```bash
# Compile
mvn clean package

# Run
java -jar target/sruu-emergency-system-1.0.0.jar

# Watch logs for:
# [Dispatcher] Received incident...
# [Dispatcher] Selected unit...
# [FireTruck-1] Water reserve: X
# [FireTruck-1] Water depleted! Aborting...
# [Dispatcher] Searching for alternative...
# [Logger] Generated report at: logs/incident_report_...json

# Check JSON report
cat logs/incident_report_*.json | jq .
```

---

## TIME ALLOCATION

| Section | Time | Audience Focus |
|---------|------|-----------------|
| Architecture Overview | 1:00 | Understanding system design |
| Scenario 1 (Parallel incidents) | 2:00 | Parallelism & coordination |
| Scenario 2 (Water depletion) | 2:00 | Robustness & reallocation |
| Scenario 3 (Report analysis) | 2:00 | Metrics & auditability |
| Review & Future Work | 3:00 | Lessons & improvements |
| Q&A | Buffer | Clarifications |

---

## VISUAL AIDS TO PREPARE

1. **Architecture Diagram:** 9 agents with message flow
2. **Utility Function Formula:** With example calculation
3. **State Machine Charts:** For each unit type
4. **Screenshot of JSON Report:** Sample metrics output
5. **Timeline Graph:** Incident creation → assignment → completion

