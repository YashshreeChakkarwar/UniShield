# 🛡️ UniShield — Graph-Based Cyber Defense System

A **Java Desktop Application** built with **JavaFX** that simulates, detects, and defends against cyberattacks in a university network using core Data Structures and Algorithms.

---

## 📌 Problem Statement

Design a graph-based cyber defense system that simulates malware spread in a network and uses efficient Data Structures and Algorithms to identify critical nodes and apply optimized containment strategies to minimize damage.
## Discription
University networks are complex — hundreds of devices (student laptops, lab computers, exam servers) are interconnected via WiFi and file-sharing links. A single compromised device can rapidly spread malware across the entire network, threatening critical infrastructure like exam and attendance servers.

**UniShield** models this university network as a **graph** and provides:
- **Malware spread simulation** to visualize how infections propagate
- **Automated threat detection** to identify vulnerable and suspicious nodes
- **Optimized containment strategies** to minimize damage
- **Historical learning** from past attacks to improve future defense

The system uses fundamental DSA concepts — BFS, DFS, Dijkstra's Algorithm, and Greedy strategies — implemented manually without relying on built-in shortcuts.

---

## 🧩 Data Structures Used

| Data Structure | Implementation | Where Used | Why |
|---|---|---|---|
| **HashMap** | `java.util.HashMap` | Graph adjacency list, risk scores, Dijkstra distances, credentials | O(1) key-value lookup for fast node/edge access |
| **ArrayList** | `java.util.ArrayList` | Edge lists, infection order, paths, recommendations | Dynamic arrays for ordered collections |
| **LinkedList (as Queue)** | `java.util.LinkedList` | BFS traversal, alert system | FIFO queue for level-order processing |
| **HashSet** | `java.util.HashSet` | Visited tracking in BFS/DFS, infected node tracking | O(1) membership check to avoid revisiting |
| **PriorityQueue** | `java.util.PriorityQueue` | Dijkstra's algorithm (min-heap) | Always extracts node with shortest distance |
| **Adjacency List** | `HashMap<String, ArrayList<Edge>>` | Core graph representation | Space-efficient for sparse university networks |

---

## ⚙️ Algorithms Implemented

| Algorithm | Purpose | File |
|---|---|---|
| **BFS** (Breadth-First Search) | Simulates malware spread wave-by-wave | `MalwareSimulator.java` |
| **DFS** (Depth-First Search) | Detects critical/bridge nodes in the network | `RiskAnalyzer.java` |
| **Dijkstra's Algorithm** | Finds fastest infection path to critical servers | `DijkstraPathFinder.java` |
| **Greedy Strategy** | Selects optimal containment actions by priority | `DefenseEngine.java` |

---

## 🏗️ Architecture

```
├── Presentation Layer    →  JavaFX UI (Login + Dashboard)
├── Application Layer     →  Algorithms (BFS, DFS, Dijkstra, Greedy)
└── Data Layer            →  Graph (Adjacency List) + MySQL Database
```

---

## 🖥️ Modules

| # | Module | Description |
|---|--------|-------------|
| A | **Authentication** | Role-based login (ADMIN / ANALYST) |
| B | **Graph Network** | 10-node university network with adjacency list |
| C | **Monitoring** | Timer-based background threat detection |
| D | **Risk Scoring** | 4-factor scoring (connections, access, proximity, type) |
| E | **Malware Spread** | BFS infection simulation with level-by-level output |
| F | **Critical Nodes** | DFS-based bridge node and high-connectivity detection |
| G | **Fastest Path** | Dijkstra's shortest path to critical servers |
| H | **Learning System** | MySQL storage of past attacks for pattern matching |
| I | **Recommendations** | Automated defense suggestions using greedy logic |
| J | **Containment** | Isolate nodes, block edges, protect servers |
| K | **Before vs After** | Damage reduction percentage comparison |
| L | **Notifications** | Live alert queue with severity levels |

---

## 🚀 Quick Start

```bash
# 1. Download dependencies (first time only)
powershell -ExecutionPolicy Bypass -File setup.ps1

# 2. (Optional) Set up MySQL database
mysql -u root -p < db_setup.sql

# 3. Compile and run
build_and_run.bat
```

**Login:** `admin` / `admin123` &nbsp;or&nbsp; `analyst` / `analyst123`

---

## 📁 Project Structure

```
Unishield/
├── src/com/unishield/
│   ├── MainApp.java              # JavaFX entry point
│   ├── Launcher.java             # Module system bridge
│   ├── LoginController.java      # Authentication UI
│   ├── DashboardController.java  # Main dashboard UI
│   ├── Node.java                 # Network node model
│   ├── Edge.java                 # Network edge model
│   ├── Graph.java                # Adjacency list graph
│   ├── MalwareSimulator.java     # BFS malware spread
│   ├── DijkstraPathFinder.java   # Dijkstra shortest path
│   ├── RiskAnalyzer.java         # DFS + risk scoring
│   ├── DefenseEngine.java        # Containment engine
│   ├── DatabaseManager.java      # MySQL JDBC manager
│   └── NotificationService.java  # Alert queue system
├── resources/styles.css          # Dark cybersecurity theme
├── setup.ps1                     # Dependency downloader
├── build_and_run.bat             # Compile & run script
└── db_setup.sql                  # MySQL setup script
```

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 23 |
| UI | JavaFX 23 |
| Database | MySQL 8.0 |
| Connectivity | JDBC |
| Build | Manual compilation (javac) |

---

## VIDEO LINK
https://drive.google.com/file/d/1IT_pAh6blmT2tqGbwaVUGZfnchltHdM6/view?usp=sharing

## 📜 License

This project was built for academic purposes — Graph-Based Cyber Defense System for University Networks.

## GROUP NUMBER :- 17
--> Group Members
   - Sharyu Chavan
   - Vaishnavi chalke 
   - Shruti Pohankar
   -Yashshree Chakkarwar

