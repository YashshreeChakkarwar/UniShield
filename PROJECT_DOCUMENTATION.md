# 🛡️ UniShield — Graph-Based Cyber Defense System
## Complete Project Documentation

---

# 📑 TABLE OF CONTENTS

1. [Project Overview](#1-project-overview)
2. [Architecture & Layers](#2-architecture--layers)
3. [Data Structures Used](#3-data-structures-used)
4. [Algorithms Implemented](#4-algorithms-implemented)
5. [Module-by-Module Breakdown](#5-module-by-module-breakdown)
6. [Database Design](#6-database-design)
7. [JavaFX UI Architecture](#7-javafx-ui-architecture)
8. [System Flow](#8-system-flow)
9. [Class Diagram & Relationships](#9-class-diagram--relationships)
10. [File Structure](#10-file-structure)
11. [How to Run](#11-how-to-run)

---

# 1. PROJECT OVERVIEW

**UniShield** is a Java Desktop Application built with JavaFX that simulates a university network's cyber defense system. It models the university's devices and servers as a **graph**, simulates **malware spread** using BFS, detects **critical nodes** using DFS, finds the **fastest infection path** using Dijkstra's algorithm, and applies **automated containment strategies** using greedy logic.

### Key Technologies
| Technology | Purpose |
|-----------|---------|
| Java 23 | Core programming language |
| JavaFX 23 | Desktop UI framework |
| MySQL 8.0 | Attack history database |
| JDBC | Java-to-MySQL connectivity |

### Application Type
- **Standalone Java Desktop Application**
- No heavy frameworks — pure Java backend
- Modular architecture with 13 separate Java classes

---

# 2. ARCHITECTURE & LAYERS

The application follows a **3-Layer Architecture**:

```
┌─────────────────────────────────────────────────┐
│           PRESENTATION LAYER (UI)               │
│  JavaFX Controls, Scenes, CSS Styling           │
│  ┌─────────────┐  ┌──────────────────────┐      │
│  │LoginController│ │DashboardController   │      │
│  └─────────────┘  └──────────────────────┘      │
├─────────────────────────────────────────────────┤
│           APPLICATION LAYER (Logic)             │
│  Algorithms, Analysis, Simulation               │
│  ┌──────────────┐ ┌───────────────────┐         │
│  │MalwareSimulator│ │DijkstraPathFinder │         │
│  └──────────────┘ └───────────────────┘         │
│  ┌──────────────┐ ┌───────────────────┐         │
│  │RiskAnalyzer   │ │DefenseEngine      │         │
│  └──────────────┘ └───────────────────┘         │
│  ┌────────────────────┐                         │
│  │NotificationService  │                         │
│  └────────────────────┘                         │
├─────────────────────────────────────────────────┤
│           DATA LAYER (Storage & Models)         │
│  Graph Structure, MySQL Database                │
│  ┌──────┐ ┌──────┐ ┌───────┐ ┌───────────────┐ │
│  │Node  │ │Edge  │ │Graph  │ │DatabaseManager│ │
│  └──────┘ └──────┘ └───────┘ └───────────────┘ │
└─────────────────────────────────────────────────┘
```

### Layer 1: PRESENTATION LAYER (UI)
**Purpose:** Handles all visual elements and user interaction.

| Component | File | Responsibility |
|-----------|------|---------------|
| Login Screen | `LoginController.java` | User authentication UI — username/password fields, login button, error display |
| Dashboard | `DashboardController.java` | Main application UI — graph visualization, control buttons, node table, alerts panel, output area |
| Entry Point | `MainApp.java` | JavaFX Application class — scene switching between login and dashboard |
| Launcher | `Launcher.java` | Module system workaround — bridges non-JavaFX main to Application.launch() |
| Styling | `styles.css` | Dark cybersecurity theme — colors, table styles, scrollbars, tabs |

**JavaFX Controls Used:**
- `BorderPane` — Main dashboard layout (top/left/center/right/bottom)
- `VBox` — Vertical stacking for control panels, login card
- `HBox` — Horizontal layout for header bar, stat cards
- `SplitPane` — Divides graph area and output area
- `TabPane` — Tabs for output and event log
- `TableView` — Node risk score table with sortable columns
- `ListView` — Live alerts display
- `TextArea` — Console-style output display
- `TextField` / `PasswordField` — Login inputs
- `ComboBox` — Attack source node selector
- `Button` — Action triggers (simulation, detection, defense, reset)
- `Label` — Text displays, statistics, status bar
- `ScrollPane` — Scrollable graph visualization
- `Circle` — Node visualization shapes
- `Line` — Edge visualization shapes
- `Text` — Node labels and weight labels
- `Tooltip` — Hover info on nodes
- `Timeline` — Background monitoring timer and log updater

---

### Layer 2: APPLICATION LAYER (Logic)
**Purpose:** Contains all algorithms, analysis engines, and business logic.

| Component | File | Responsibility |
|-----------|------|---------------|
| Malware Simulator | `MalwareSimulator.java` | BFS-based infection spread simulation |
| Path Finder | `DijkstraPathFinder.java` | Dijkstra's shortest path to critical servers |
| Risk Analyzer | `RiskAnalyzer.java` | DFS traversal, critical node detection, risk scoring |
| Defense Engine | `DefenseEngine.java` | Greedy containment, recommendations, before/after comparison |
| Notification Service | `NotificationService.java` | Alert queue management, event logging |

---

### Layer 3: DATA LAYER (Storage & Models)
**Purpose:** Manages data structures, graph representation, and database persistence.

| Component | File | Responsibility |
|-----------|------|---------------|
| Node Model | `Node.java` | Network device/server data model |
| Edge Model | `Edge.java` | Network connection data model |
| Graph | `Graph.java` | Adjacency list graph with CRUD operations |
| Database | `DatabaseManager.java` | MySQL JDBC connection, attack history CRUD |

---

# 3. DATA STRUCTURES USED

## 3.1 HashMap (java.util.HashMap)

**What it is:** A hash table implementation that stores key-value pairs with O(1) average lookup time.

**Where used:**

| Location | Declaration | Purpose |
|----------|------------|---------|
| `Graph.java` | `HashMap<String, Node> nodes` | Stores all nodes — maps node ID to Node object for O(1) lookup |
| `Graph.java` | `HashMap<String, ArrayList<Edge>> adjacencyList` | **Core graph structure** — maps each node ID to its list of edges (adjacency list) |
| `RiskAnalyzer.java` | `HashMap<String, Integer> riskScores` | Maps node ID to calculated risk score (0-100) |
| `DijkstraPathFinder.java` | `HashMap<String, Double> distances` | Stores shortest known distance from source to each node during Dijkstra |
| `DijkstraPathFinder.java` | `HashMap<String, String> previous` | Stores the previous node in the shortest path for path reconstruction |
| `DijkstraPathFinder.java` | `HashMap<String, Boolean> visited` | Tracks which nodes have been finalized in Dijkstra |
| `RiskAnalyzer.java` | `HashMap<String, Integer> hopCounts` | BFS hop counts for proximity calculation |
| `LoginController.java` | `HashMap<String, String[]> credentials` | In-memory credential store — username → [password, role] |
| `DashboardController.java` | `HashMap<String, Circle> nodeCircles` | Maps node ID to its Circle shape for visualization updates |
| `DashboardController.java` | `HashMap<String, Text> nodeLabels` | Maps node ID to its Text label for visualization updates |

---

## 3.2 ArrayList (java.util.ArrayList)

**What it is:** A dynamic array that grows automatically. Provides O(1) random access and O(n) insertion/deletion.

**Where used:**

| Location | Declaration | Purpose |
|----------|------------|---------|
| `Graph.java` | `ArrayList<Edge>` (in adjacency list) | Stores the list of edges for each node — the core adjacency list |
| `Graph.java` | `ArrayList<Node> getAllNodes()` | Returns all nodes in the graph |
| `Graph.java` | `ArrayList<Edge> getAllEdges()` | Returns all edges (deduplicated for undirected graph) |
| `Graph.java` | `ArrayList<Node> getNeighbors()` | Returns neighbor nodes of a given node |
| `Graph.java` | `ArrayList<Node> getCriticalNodes()` | Returns nodes marked as critical servers |
| `MalwareSimulator.java` | `ArrayList<String> infectionOrder` | Records the order in which nodes get infected |
| `MalwareSimulator.java` | `ArrayList<ArrayList<String>> levels` | BFS levels — each inner list is one "wave" of infection |
| `DijkstraPathFinder.java` | `ArrayList<String> path` | Stores the reconstructed shortest path |
| `DijkstraPathFinder.java` | `ArrayList<PathResult> results` | Collection of shortest paths to critical nodes |
| `RiskAnalyzer.java` | `ArrayList<String> visitOrder` | DFS traversal order |
| `RiskAnalyzer.java` | `ArrayList<Node> criticalNodes` | List of detected critical nodes |
| `RiskAnalyzer.java` | `ArrayList<Node> suspicious` | List of suspicious nodes |
| `DefenseEngine.java` | `ArrayList<String> recommendations` | Generated defense recommendations |
| `DefenseEngine.java` | `ArrayList<String> actionsApplied` | List of containment actions that were applied |
| `NotificationService.java` | `ArrayList<Alert> allAlerts` | Historical record of all alerts |
| `NotificationService.java` | `ArrayList<String> eventLog` | General event log entries |
| `DatabaseManager.java` | `ArrayList<AttackRecord> records` | Query results from database |
| `DashboardController.java` | `ArrayList<Line> edgeLines` | All rendered edge lines for visualization |

---

## 3.3 LinkedList (java.util.LinkedList)

**What it is:** A doubly-linked list that implements both List and Deque interfaces. Used as a **Queue (FIFO)** in this project.

**Where used:**

| Location | Declaration | Purpose |
|----------|------------|---------|
| `MalwareSimulator.java` | `LinkedList<String> queue` | **BFS Queue** — nodes to be processed in FIFO order for malware spread |
| `RiskAnalyzer.java` | `LinkedList<String> queue` | **BFS Queue** — for proximity score calculation (hop counting) |
| `NotificationService.java` | `LinkedList<Alert> alertQueue` | **Alert Queue (FIFO)** — pending alerts, dequeued when read |

**Operations used as Queue:**
- `addLast()` → Enqueue (add to back)
- `removeFirst()` → Dequeue (remove from front)
- `isEmpty()` → Check if queue is empty

---

## 3.4 HashSet (java.util.HashSet)

**What it is:** A hash table-based Set that stores unique elements with O(1) add/contains/remove.

**Where used:**

| Location | Declaration | Purpose |
|----------|------------|---------|
| `MalwareSimulator.java` | `HashSet<String> infectedNodes` | Tracks which nodes are currently infected — prevents re-infection |
| `RiskAnalyzer.java` | `HashSet<String> visited` | **DFS visited set** — prevents revisiting nodes during traversal |
| `DefenseEngine.java` | `HashSet<String> infectedNodes` | Set of infected node IDs for defense analysis |
| `DefenseEngine.java` | `HashSet<String> currentlyInfected` | Current infection state for before/after comparison |

---

## 3.5 PriorityQueue (java.util.PriorityQueue)

**What it is:** A min-heap based priority queue. Elements are ordered by natural ordering or a custom Comparator. O(log n) insert and O(log n) extract-min.

**Where used:**

| Location | Declaration | Purpose |
|----------|------------|---------|
| `DijkstraPathFinder.java` | `PriorityQueue<DijkstraEntry>` | **Dijkstra's min-heap** — always extracts the node with the smallest known distance. Uses a custom `Comparator` to compare distances. |

**Custom Comparator:**
```java
new Comparator<DijkstraEntry>() {
    public int compare(DijkstraEntry a, DijkstraEntry b) {
        if (a.distance < b.distance) return -1;
        if (a.distance > b.distance) return 1;
        return 0;
    }
}
```

---

## 3.6 Enum Types (Custom)

**What they are:** Type-safe constants defined as enumeration types.

| Enum | File | Values | Purpose |
|------|------|--------|---------|
| `NodeType` | `Node.java` | `STUDENT_DEVICE, LAB_SYSTEM, EXAM_SERVER, ATTENDANCE_SERVER` | Categorizes network nodes |
| `EdgeType` | `Edge.java` | `WIFI, FILE_SHARING` | Categorizes connection types |
| `UserRole` | `LoginController.java` | `ADMIN, ANALYST` | Role-based access control |
| `AlertLevel` | `NotificationService.java` | `INFO, WARNING, CRITICAL` | Alert severity classification |

---

## 3.7 Custom Data Classes (Inner Classes)

| Class | File | Fields | Purpose |
|-------|------|--------|---------|
| `PathResult` | `DijkstraPathFinder.java` | `ArrayList<String> path`, `double totalWeight` | Holds a Dijkstra result (path + total distance) |
| `DijkstraEntry` | `DijkstraPathFinder.java` | `String nodeId`, `double distance` | Priority queue entry for Dijkstra |
| `ComparisonResult` | `DefenseEngine.java` | `int infectedBefore`, `int infectedAfter`, `double damageReduction`, `ArrayList<String> actionsApplied` | Before vs after defense comparison |
| `AttackRecord` | `DatabaseManager.java` | `int id`, `String sourceNode`, `String path`, `String actionTaken`, `int infectedCount`, `double damageReduction`, `String timestamp` | Database record for attack history |
| `Alert` | `NotificationService.java` | `String message`, `AlertLevel level`, `String timestamp` | Individual alert object |

---

## 3.8 JavaFX Observable Collections

| Type | Location | Purpose |
|------|----------|---------|
| `ObservableList<Node>` | `DashboardController.java` | Binds node data to `TableView` for reactive updates |
| `ObservableList<String>` | `DashboardController.java` | Binds alert strings to `ListView` for reactive updates |

---

## Summary: Data Structure Count

| Data Structure | Times Used | Primary Purpose |
|---------------|------------|-----------------|
| HashMap | 10 | Key-value lookups, graph adjacency list, distances |
| ArrayList | 18+ | Dynamic lists, paths, results, edges |
| LinkedList (as Queue) | 3 | BFS traversal, alert queue |
| HashSet | 4 | Visited tracking, infection tracking |
| PriorityQueue | 1 | Dijkstra's min-heap |
| Enum | 4 | Type-safe categorization |
| Custom Inner Classes | 5 | Structured result objects |

---

# 4. ALGORITHMS IMPLEMENTED

## 4.1 Breadth-First Search (BFS)

**File:** `MalwareSimulator.java`

**Purpose:** Simulates malware spread through the network level by level (wave by wave).

**How it works:**
```
1. Start at the source node (patient zero)
2. Add source to queue and mark as infected
3. While queue is not empty:
   a. Dequeue front node
   b. For each neighbor:
      - If not infected and not protected:
        - Mark as infected
        - Add to infection order
        - Enqueue for further spreading
4. Return the infection order
```

**Time Complexity:** O(V + E) where V = vertices (nodes), E = edges
**Space Complexity:** O(V) for the queue and visited set

**Data Structures Used:** LinkedList (queue), HashSet (visited/infected), ArrayList (infection order)

---

## 4.2 Depth-First Search (DFS)

**File:** `RiskAnalyzer.java`

**Purpose:** Traverses the graph deeply to find critical/bridge nodes.

**How it works:**
```
1. Start at a node
2. Mark as visited
3. For each unvisited neighbor:
   a. Recursively call DFS on that neighbor
4. After visiting all reachable nodes, check if removing
   any node would disconnect the graph (bridge node detection)
```

**Bridge Node Detection Logic:**
```
For each node X:
  1. Temporarily "remove" X (add to visited set)
  2. Run DFS from a neighbor of X
  3. Count reachable nodes
  4. If reachable < total - 1, X is a bridge node
```

**Time Complexity:** O(V + E) per DFS, O(V × (V + E)) for bridge detection
**Space Complexity:** O(V) for recursion stack and visited set

**Data Structures Used:** HashSet (visited), ArrayList (visit order), recursion stack

---

## 4.3 Dijkstra's Shortest Path Algorithm

**File:** `DijkstraPathFinder.java`

**Purpose:** Finds the fastest (shortest weighted) path from any node to critical servers.

**How it works:**
```
1. Initialize all distances to ∞, source distance to 0
2. Add source to priority queue
3. While priority queue is not empty:
   a. Extract node with minimum distance
   b. Skip if already visited
   c. Mark as visited
   d. For each neighbor via active edge:
      - Calculate new_distance = current_distance + edge_weight
      - If new_distance < known_distance:
        - Update distance
        - Record previous node (for path reconstruction)
        - Add to priority queue
4. Reconstruct path by walking backwards through 'previous' map
```

**Time Complexity:** O((V + E) × log V) with priority queue
**Space Complexity:** O(V) for distances, previous, and visited maps

**Data Structures Used:** PriorityQueue (min-heap), HashMap (distances, previous, visited), ArrayList (path)

---

## 4.4 Greedy Defense Strategy

**File:** `DefenseEngine.java`

**Purpose:** Selects the best containment actions using priority-based greedy logic.

**How it works:**
```
Priority order (greedy choice):
  1. PROTECT critical servers (highest priority)
  2. ISOLATE infection source node
  3. BLOCK frontier edges (connections between infected & clean zones)
  4. ISOLATE highly-connected infected nodes

Each action is applied in priority order without backtracking
(greedy: make locally optimal choice at each step).
```

**Data Structures Used:** ArrayList (recommendations, actions), HashSet (infected tracking)

---

## 4.5 Multi-Factor Risk Scoring

**File:** `RiskAnalyzer.java`

**Purpose:** Calculates a 0-100 risk score for every node based on 4 factors.

**Scoring Formula:**
```
Risk Score = Factor1 + Factor2 + Factor3 + Factor4 (capped at 100)

Factor 1: Connection Count (max 25 points)
  - Score = min(connections × 8, 25)

Factor 2: Access Attempts (max 25 points)
  - Score = min(attempts × 5, 25)

Factor 3: Proximity to Critical Nodes (max 30 points)
  - Uses BFS to find hop distance
  - 1 hop → 25 pts, 2 hops → 15 pts, 3 hops → 8 pts, 4+ → 3 pts
  - Is critical node → 30 pts

Factor 4: Node Type (max 20 points)
  - EXAM_SERVER → 20, ATTENDANCE_SERVER → 18
  - LAB_SYSTEM → 10, STUDENT_DEVICE → 5
```

---

# 5. MODULE-BY-MODULE BREAKDOWN

## Module A: Authentication (`LoginController.java`)

**Purpose:** Simple login system with role-based access control.

**Credentials (in-memory):**
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| analyst | analyst123 | ANALYST |
| drsmith | cyber2024 | ADMIN |
| student1 | monitor01 | ANALYST |

**Role Permissions:**
- **ADMIN:** Full access — can run simulations, detect threats, AND apply defenses
- **ANALYST:** Read-only — can run simulations and detect threats, but CANNOT apply defenses

**Data Structures:** HashMap for credentials

---

## Module B: Graph Network (`Node.java`, `Edge.java`, `Graph.java`)

### Node.java
Represents a device or server in the university network.

**Fields:**
| Field | Type | Description |
|-------|------|-------------|
| `id` | String | Unique identifier (e.g., "STU_1", "EXAM_SVR") |
| `name` | String | Human-readable name (e.g., "Student A") |
| `type` | NodeType enum | STUDENT_DEVICE, LAB_SYSTEM, EXAM_SERVER, ATTENDANCE_SERVER |
| `riskScore` | int | Calculated risk (0-100) |
| `infected` | boolean | Whether node is infected by malware |
| `nodeProtected` | boolean | Whether defense has been applied |
| `accessAttempts` | int | Count of access attempts (monitoring) |
| `xPos`, `yPos` | double | Visualization coordinates |

### Edge.java
Represents a connection between two nodes.

**Fields:**
| Field | Type | Description |
|-------|------|-------------|
| `source` | String | Source node ID |
| `destination` | String | Destination node ID |
| `weight` | double | Connection speed/risk (lower = faster/riskier) |
| `type` | EdgeType enum | WIFI or FILE_SHARING |
| `active` | boolean | Whether connection is active (blocked = false) |

### Graph.java
The core graph structure using an adjacency list.

**Internal Structure:**
```
nodes: HashMap<String, Node>
  "STU_1" → Node(id="STU_1", name="Student A", ...)
  "LAB_1" → Node(id="LAB_1", name="Computer Lab 1", ...)
  ...

adjacencyList: HashMap<String, ArrayList<Edge>>
  "STU_1" → [Edge(STU_1→LAB_1, w=2, WIFI), Edge(STU_1→STU_2, w=1, FILE_SHARING)]
  "LAB_1" → [Edge(LAB_1→STU_1, w=2, WIFI), Edge(LAB_1→STU_2, w=3, WIFI), ...]
  ...
```

**Operations:**
| Method | Complexity | Description |
|--------|-----------|-------------|
| `addNode(Node)` | O(1) | Add node to graph |
| `addEdge(Edge)` | O(1) | Add undirected edge (adds both directions) |
| `removeNode(String)` | O(V + E) | Remove node and all its edges |
| `removeEdge(String, String)` | O(E) | Deactivate edge (mark as blocked) |
| `getNeighbors(String)` | O(degree) | Get connected nodes via active edges |
| `getConnectionCount(String)` | O(degree) | Count active connections |
| `getAllEdges()` | O(E) | Get all edges (deduplicated) |
| `resetAll()` | O(V + E) | Reset all nodes and reactivate all edges |

**University Network Topology (10 nodes, 13 edges):**
```
                [ATT_SVR]──────────[EXAM_SVR]
                /    \              /     \
              /       \           /        \
         [LAB_1]──────[LAB_2]          [LAB_3]
          / \          / \                |
        /    \       /    \               |
   [STU_1]─[STU_2]─[STU_3]─[STU_4]   [STU_5]
```

---

## Module C: Monitoring & Detection (`RiskAnalyzer.java` + `DashboardController.java`)

**Background Monitoring:**
- Uses JavaFX `Timeline` — fires every 8 seconds
- Checks each node's connection count
- Increments `accessAttempts` for high-traffic nodes
- Generates WARNING alerts when suspicious patterns detected

**Detection Criteria:**
1. Too many connections (≥ threshold)
2. Too many access attempts (≥ threshold)
3. Non-authorized access to critical nodes

**Data Structures:** HashSet (visited), Queue (events), HashMap (risk scores)

---

## Module D: Risk Scoring (`RiskAnalyzer.java`)

See Section 4.5 above for the complete scoring formula.

---

## Module E: Malware Spread Simulation (`MalwareSimulator.java`)

**Two modes:**
1. `simulateSpread()` — Full BFS, returns flat infection order
2. `simulateSpreadByLevels()` — Level-order BFS, returns waves

**Example Output:**
```
Wave 0: STU_1 (Student A)
Wave 1: LAB_1 (Computer Lab 1), STU_2 (Student B)
Wave 2: EXAM_SVR (Exam Server), LAB_2 (Computer Lab 2), STU_3 (Student C)
Wave 3: ATT_SVR (Attendance Server), STU_4 (Student D)
Wave 4: LAB_3 (Science Lab)
Wave 5: STU_5 (Student E)
```

---

## Module F: Critical Node Detection (`RiskAnalyzer.java`)

Uses DFS-based analysis. A node is critical if:
- It's an EXAM_SERVER or ATTENDANCE_SERVER (by type)
- It has more connections than average + 1 (by connectivity)
- Removing it disconnects the graph (bridge node)

---

## Module G: Fastest Infection Path (`DijkstraPathFinder.java`)

Finds shortest weighted paths from infection source to critical servers.

**Three methods:**
1. `findShortestPath(source, destination)` — Single pair
2. `findPathsToCriticalNodes(source)` — Source to all servers
3. `findMostVulnerablePath(source)` — Shortest path to ANY server

---

## Module H: Learning System (`DatabaseManager.java`)

**Database:** MySQL 8.0
**Database Name:** `unishield_db`
**Connection:** JDBC with `com.mysql.cj.jdbc.Driver`

**Table: AttackHistory**
| Column | Type | Description |
|--------|------|-------------|
| `id` | INT AUTO_INCREMENT | Primary key |
| `sourceNode` | VARCHAR(50) | Attack source node ID |
| `path` | TEXT | Infection path string |
| `actionTaken` | TEXT | Defense actions applied |
| `infectedCount` | INT | Number of infected nodes |
| `damageReduction` | DOUBLE | Percentage of damage prevented |
| `timestamp` | DATETIME | When the attack was recorded |

**Operations:**
- `saveAttackHistory()` — INSERT new attack record
- `getAttackHistory()` — SELECT all records (ORDER BY timestamp DESC)
- `findSimilarAttacks()` — SELECT WHERE sourceNode = ?
- `getBestPastAction()` — Find record with highest damageReduction

**Connection is OPTIONAL** — app works fully without MySQL.

---

## Module I: Recommendation Engine (`DefenseEngine.java`)

Generates prioritized defense suggestions:
```
Priority 1: PROTECT critical servers not yet infected
Priority 2: ALERT about already-infected critical servers
Priority 3: ISOLATE the source of infection
Priority 4: BLOCK frontier edges (infected ↔ clean boundaries)
Priority 5: ISOLATE highly-connected infected nodes (≥3 connections)
```

---

## Module J: Containment Actions (`DefenseEngine.java`)

| Action | Method | What it does |
|--------|--------|-------------|
| Isolate Node | `isolateNode(id)` | Deactivates ALL edges to/from the node |
| Block Connection | `blockConnection(src, dst)` | Deactivates a specific edge pair |
| Protect Node | `protectNode(id)` | Marks node as protected + cures infection |

All actions **modify the graph dynamically** — edges are marked inactive, nodes are marked protected.

---

## Module K: Before vs After (`DefenseEngine.java`)

```
1. Count infected nodes BEFORE defense
2. Apply all defense actions (protect, isolate, block)
3. Reset infection states
4. Re-run BFS simulation WITH defenses in place
5. Count infected nodes AFTER defense
6. Calculate: damageReduction = (before - after) / before × 100%
```

---

## Module L: Notification System (`NotificationService.java`)

**Alert Levels:**
| Level | Icon | Use Case |
|-------|------|----------|
| INFO | ℹ️ | System events, database saves, resets |
| WARNING | ⚠️ | Suspicious activity, high traffic |
| CRITICAL | 🚨 | Active infections, server compromises |

**Queue Behavior:**
- New alerts → `addLast()` (enqueue)
- Read alerts → `removeFirst()` (dequeue)
- All alerts also saved to historical ArrayList

---

# 6. DATABASE DESIGN

```
Database: unishield_db
Connection: jdbc:mysql://localhost:3306/unishield_db
Username: root
Password: uhsay@root

┌──────────────────────────────────────────┐
│            AttackHistory                 │
├──────────────────────────────────────────┤
│ id            INT AUTO_INCREMENT  PK     │
│ sourceNode    VARCHAR(50)    NOT NULL     │
│ path          TEXT           NOT NULL     │
│ actionTaken   TEXT           NOT NULL     │
│ infectedCount INT            DEFAULT 0   │
│ damageReduction DOUBLE       DEFAULT 0.0 │
│ timestamp     DATETIME       DEFAULT NOW │
└──────────────────────────────────────────┘
```

**Sample Data (pre-loaded):**
```
| sourceNode | path                              | actionTaken                              | damageReduction |
|------------|-----------------------------------|------------------------------------------|-----------------|
| STU_1      | STU_1 -> LAB_1 -> EXAM_SERVER     | Isolated STU_1, Blocked STU_1-LAB_1      | 65.0%           |
| STU_3      | STU_3 -> LAB_2 -> ATTENDANCE_SVR  | Isolated STU_3, Protected ATT_SVR        | 70.0%           |
| STU_5      | STU_5 -> LAB_3 -> ATTENDANCE_SVR  | Blocked LAB_3-ATTENDANCE_SERVER          | 80.0%           |
```

---

# 7. JAVAFX UI ARCHITECTURE

## Login Screen
```
┌──────────────────────────────────────┐
│         (dark background)            │
│    ┌──────────────────────┐          │
│    │      🛡️               │          │
│    │    UniShield          │          │
│    │  Cyber Defense System │          │
│    │                       │          │
│    │  USERNAME             │          │
│    │  ┌──────────────────┐ │          │
│    │  │                  │ │          │
│    │  └──────────────────┘ │          │
│    │  PASSWORD             │          │
│    │  ┌──────────────────┐ │          │
│    │  │                  │ │          │
│    │  └──────────────────┘ │          │
│    │                       │          │
│    │  [ ══ Sign In ══ ]    │          │
│    │                       │          │
│    │  Demo: admin/admin123 │          │
│    └──────────────────────┘          │
└──────────────────────────────────────┘
```

## Dashboard Screen
```
┌──────────────────────────────────────────────────────────┐
│ 🛡️ UniShield — Cyber Defense System    [ADMIN] 👤admin  │
├──────────┬───────────────────────────────┬───────────────┤
│ STATS    │                               │ 🔔 ALERTS    │
│ ┌──┐┌──┐│   🌐 Network Topology         │               │
│ │10││ 0││                                │ [18:05] ℹ️    │
│ │Nd││In││    [ATT]────[EXAM]             │ System init   │
│ └──┘└──┘│   /    \    /    \             │               │
│ ┌──────┐│  [L1]──[L2]    [L3]           │ [18:05] ℹ️    │
│ │ LOW  ││  / \    / \      |             │ Monitoring    │
│ └──────┘│ [S1][S2][S3][S4][S5]           │               │
│ ACTIONS ├───────────────────────────────┤│ [18:13] ⚠️    │
│         │ 📋 Output                     ││ High traffic  │
│ Source: │                                ││               │
│ [▼ STU1]│ UniShield v1.0 — Ready        ││               │
│         │ ═══════════════════            ││ DATABASE      │
│ [▶ Sim ]│ Select source and run...      ││ ⚪ Not conn   │
│ [🔍 Det]│                                ││ [Connect]     │
│ [🛡 Def]│────────────────────────────────┤│               │
│ [↺ Rst ]│ 🔔 Event Log                  ││               │
│         │                                ││               │
│ NODES   │ [18:05] System initialized    ││               │
│ ┌──────┐│ [18:05] Monitoring active     ││               │
│ │Table ││                                ││               │
│ │View  ││                                ││               │
│ └──────┘│                                ││               │
├──────────┴───────────────────────────────┴───────────────┤
│ ✓ System Ready — Monitoring Active          UniShield v1 │
└──────────────────────────────────────────────────────────┘
```

**Color Scheme:**
| Element | Color | Hex Code |
|---------|-------|----------|
| Background | Very dark blue | `#0a0e17` |
| Panel backgrounds | Dark blue | `#0f1520`, `#141b2d` |
| Borders | Subtle blue-gray | `#1e2a3a` |
| Primary accent | Cyan | `#00d4ff` |
| Success/Protected | Green | `#00ff88` |
| Danger/Infected | Red | `#ff4d4d` |
| Warning/Critical | Gold | `#ffd700` |
| Text primary | Light gray | `#c0c8d8` |
| Text secondary | Medium gray | `#6c7a8e`, `#8892a4` |
| Text muted | Dark gray | `#4a5568` |

---

# 8. SYSTEM FLOW

```
Step 1:  USER opens application
            ↓
Step 2:  LOGIN SCREEN appears
            ↓
Step 3:  User enters credentials → LoginController authenticates
            ↓
Step 4:  DASHBOARD loads → Graph initialized with 10 nodes + 13 edges
            ↓
Step 5:  DATABASE connection attempted (optional, continues without)
            ↓
Step 6:  MONITORING starts (Timeline, every 8 seconds)
            ↓
Step 7:  User selects attack source → clicks "Start Simulation"
            ↓
Step 8:  BFS runs → malware spreads level by level
         Dijkstra runs → finds fastest paths to critical servers
         Results displayed in output + graph colors updated
            ↓
Step 9:  User clicks "Detect Threats"
            ↓
Step 10: Risk scores calculated (4-factor formula)
         DFS runs → finds critical/bridge nodes
         Suspicious nodes flagged
         Past attacks queried from MySQL
            ↓
Step 11: User clicks "Apply Defense" (ADMIN only)
            ↓
Step 12: Recommendations generated (greedy priority logic)
         Containment applied (protect servers, isolate source, block edges)
         Before vs After comparison calculated
         Attack logged to MySQL database
         Graph redrawn with new colors
            ↓
Step 13: User can RESET and repeat with different source
```

---

# 9. CLASS DIAGRAM & RELATIONSHIPS

```
┌──────────┐     creates      ┌─────────────────┐
│ Launcher │ ───────────────→ │    MainApp       │
└──────────┘                  │ (JavaFX App)     │
                              └────────┬─────────┘
                                       │ switches scenes
                         ┌─────────────┴──────────────┐
                         ↓                             ↓
               ┌─────────────────┐          ┌──────────────────────┐
               │LoginController  │          │DashboardController   │
               │                 │          │                      │
               │ HashMap creds   │          │ Uses all modules:    │
               │ UserRole enum   │          │                      │
               └─────────────────┘          │ ┌──────────────────┐ │
                                            │ │ Graph            │ │
                   ┌────────────────────────│─│  ├ Node           │ │
                   │                        │ │  └ Edge           │ │
                   │                        │ └──────────────────┘ │
                   │                        │ ┌──────────────────┐ │
                   │  ┌─── uses ──────────→ │ │MalwareSimulator  │ │
                   │  │                     │ │ (BFS)            │ │
                   │  │                     │ └──────────────────┘ │
          ┌────────┴──┴───┐                 │ ┌──────────────────┐ │
          │    Graph      │  ← used by ──── │ │DijkstraPathFinder│ │
          │               │                 │ │ (Dijkstra)       │ │
          │ HashMap nodes │                 │ └──────────────────┘ │
          │ HashMap adjLst│                 │ ┌──────────────────┐ │
          │               │  ← used by ──── │ │RiskAnalyzer      │ │
          │ Node objects  │                 │ │ (DFS + Scoring)  │ │
          │ Edge objects  │                 │ └──────────────────┘ │
          └───────────────┘                 │ ┌──────────────────┐ │
                                            │ │DefenseEngine     │ │
                                            │ │ (Greedy + Comp)  │ │
                                            │ └──────────────────┘ │
                                            │ ┌──────────────────┐ │
                                            │ │DatabaseManager   │ │
                                            │ │ (MySQL JDBC)     │ │
                                            │ └──────────────────┘ │
                                            │ ┌──────────────────┐ │
                                            │ │NotificationSvc   │ │
                                            │ │ (Alert Queue)    │ │
                                            │ └──────────────────┘ │
                                            └──────────────────────┘
```

---

# 10. FILE STRUCTURE

```
c:\Users\yashs\OneDrive\Desktop\Unishield\
│
├── src\com\unishield\          # Source code (13 Java files)
│   ├── Launcher.java           #  15 lines  - Entry point bridge
│   ├── MainApp.java            #  70 lines  - JavaFX Application
│   ├── LoginController.java    # 200 lines  - Auth UI + logic
│   ├── DashboardController.java# 680 lines  - Full dashboard UI
│   ├── Node.java               # 115 lines  - Node data model
│   ├── Edge.java               #  80 lines  - Edge data model
│   ├── Graph.java              # 230 lines  - Adjacency list graph
│   ├── MalwareSimulator.java   # 140 lines  - BFS spread simulation
│   ├── DijkstraPathFinder.java # 180 lines  - Dijkstra's algorithm
│   ├── RiskAnalyzer.java       # 230 lines  - DFS + risk scoring
│   ├── DefenseEngine.java      # 220 lines  - Containment engine
│   ├── DatabaseManager.java    # 200 lines  - MySQL JDBC manager
│   └── NotificationService.java# 140 lines  - Alert queue system
│
├── resources\
│   └── styles.css              # 200 lines  - Dark cybersecurity theme
│
├── lib\                        # Downloaded dependencies
│   ├── javafx-sdk-23.0.1\      # JavaFX SDK (controls, graphics, base)
│   └── mysql-connector-j-8.3.0.jar  # MySQL JDBC driver
│
├── out\                        # Compiled .class files
│   └── com\unishield\*.class
│
├── setup.ps1                   # PowerShell: downloads dependencies
├── build_and_run.bat           # Batch: compiles + runs application
├── db_setup.sql                # SQL: creates database + table
└── PROJECT_DOCUMENTATION.md    # This file
```

**Total Lines of Code:** ~2,500+ lines (Java + CSS + scripts)

---

# 11. HOW TO RUN

### First Time Setup
```bash
# 1. Download dependencies (JavaFX SDK + MySQL Connector)
powershell -ExecutionPolicy Bypass -File setup.ps1

# 2. Set up MySQL database (optional)
#    Open MySQL CLI and run:
mysql -u root -p"uhsay@root"
source db_setup.sql

# 3. Compile and run
build_and_run.bat
```

### Subsequent Runs
```bash
build_and_run.bat
```

### Login Credentials
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN (full access) |
| analyst | analyst123 | ANALYST (view only) |

---

*Document generated for UniShield v1.0 — Graph-Based Cyber Defense System*
*University Network Simulation with BFS, DFS, Dijkstra, and Greedy Defense*
