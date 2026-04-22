package com.unishield;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * ============================================================
 * DASHBOARD CONTROLLER - Main Application UI
 * ============================================================
 * Creates the full dashboard with graph visualization,
 * node table, control buttons, output area, and alerts panel.
 * Orchestrates all modules: simulation, detection, defense.
 */
public class DashboardController {

    // ---- Module References ----
    private Graph graph;
    private MalwareSimulator simulator;
    private DijkstraPathFinder pathFinder;
    private RiskAnalyzer riskAnalyzer;
    private DefenseEngine defenseEngine;
    private DatabaseManager dbManager;
    private NotificationService notificationService;

    // ---- Session Info ----
    private String currentUser;
    private LoginController.UserRole currentRole;
    private Runnable onLogout;

    // ---- UI Components ----
    private Pane graphPane;               // Graph visualization area
    private TextArea outputArea;          // Main output display
    private TableView<Node> nodeTable;    // Node risk score table
    private ListView<String> alertsList;  // Alerts display
    private Label statusLabel;            // Status bar text
    private Label infectedCountLabel;     // Dashboard stat
    private Label riskLevelLabel;         // Dashboard stat
    private Label nodeCountLabel;         // Dashboard stat

    // ---- Graph Visualization Maps ----
    private HashMap<String, Circle> nodeCircles;   // Node ID -> Circle shape
    private HashMap<String, Text> nodeLabels;      // Node ID -> Label text
    private ArrayList<Line> edgeLines;              // All edge lines

    // ---- Simulation State ----
    private String simulationSource;     // Source node for current simulation
    private boolean simulationRunning;   // Is a simulation active?
    private Timeline monitoringTimeline; // Background monitoring timer

    // ---- Constants for visualization ----
    private static final double NODE_RADIUS = 22;
    private static final Color COLOR_NORMAL = Color.web("#00d4ff");
    private static final Color COLOR_INFECTED = Color.web("#ff4d4d");
    private static final Color COLOR_CRITICAL = Color.web("#ffd700");
    private static final Color COLOR_PROTECTED = Color.web("#00ff88");
    private static final Color COLOR_EDGE_NORMAL = Color.web("#1e2a3a");
    private static final Color COLOR_EDGE_INFECTED = Color.web("#ff4d4d", 0.6);
    private static final Color COLOR_EDGE_BLOCKED = Color.web("#666666", 0.3);

    // ---- Constructor ----
    public DashboardController(String user, LoginController.UserRole role) {
        this.currentUser = user;
        this.currentRole = role;
        this.nodeCircles = new HashMap<>();
        this.nodeLabels = new HashMap<>();
        this.edgeLines = new ArrayList<>();
        this.simulationRunning = false;

        // Initialize all modules
        initializeModules();
    }

    // ============================================================
    // INITIALIZE MODULES - Set up all backend components
    // ============================================================
    private void initializeModules() {
        // Create the graph
        graph = new Graph();
        initializeUniversityNetwork();

        // Create algorithm & service instances
        simulator = new MalwareSimulator(graph);
        pathFinder = new DijkstraPathFinder(graph);
        riskAnalyzer = new RiskAnalyzer(graph);
        defenseEngine = new DefenseEngine(graph);
        notificationService = new NotificationService();

        // Database connection (optional)
        dbManager = new DatabaseManager();
    }

    // ============================================================
    // INITIALIZE UNIVERSITY NETWORK - Create the graph topology
    // ============================================================
    private void initializeUniversityNetwork() {
        // ---- Create Nodes ----
        // Critical Servers (top row)
        graph.addNode(new Node("EXAM_SVR", "Exam Server", Node.NodeType.EXAM_SERVER, 480, 55));
        graph.addNode(new Node("ATT_SVR", "Attendance Server", Node.NodeType.ATTENDANCE_SERVER, 220, 55));

        // Lab Systems (middle row)
        graph.addNode(new Node("LAB_1", "Computer Lab 1", Node.NodeType.LAB_SYSTEM, 120, 200));
        graph.addNode(new Node("LAB_2", "Computer Lab 2", Node.NodeType.LAB_SYSTEM, 350, 200));
        graph.addNode(new Node("LAB_3", "Science Lab", Node.NodeType.LAB_SYSTEM, 570, 200));

        // Student Devices (bottom row)
        graph.addNode(new Node("STU_1", "Student A", Node.NodeType.STUDENT_DEVICE, 50, 370));
        graph.addNode(new Node("STU_2", "Student B", Node.NodeType.STUDENT_DEVICE, 180, 370));
        graph.addNode(new Node("STU_3", "Student C", Node.NodeType.STUDENT_DEVICE, 310, 370));
        graph.addNode(new Node("STU_4", "Student D", Node.NodeType.STUDENT_DEVICE, 440, 370));
        graph.addNode(new Node("STU_5", "Student E", Node.NodeType.STUDENT_DEVICE, 570, 370));

        // ---- Create Edges (Connections) ----
        // WiFi connections: students to labs
        graph.addEdge(new Edge("STU_1", "LAB_1", 2.0, Edge.EdgeType.WIFI));
        graph.addEdge(new Edge("STU_2", "LAB_1", 3.0, Edge.EdgeType.WIFI));
        graph.addEdge(new Edge("STU_3", "LAB_2", 2.0, Edge.EdgeType.WIFI));
        graph.addEdge(new Edge("STU_4", "LAB_2", 4.0, Edge.EdgeType.WIFI));
        graph.addEdge(new Edge("STU_5", "LAB_3", 1.0, Edge.EdgeType.WIFI));

        // File sharing links: between students
        graph.addEdge(new Edge("STU_1", "STU_2", 1.0, Edge.EdgeType.FILE_SHARING));
        graph.addEdge(new Edge("STU_3", "STU_4", 1.0, Edge.EdgeType.FILE_SHARING));
        graph.addEdge(new Edge("STU_2", "STU_3", 2.0, Edge.EdgeType.FILE_SHARING));

        // WiFi connections: labs to servers
        graph.addEdge(new Edge("LAB_1", "EXAM_SVR", 5.0, Edge.EdgeType.WIFI));
        graph.addEdge(new Edge("LAB_2", "EXAM_SVR", 4.0, Edge.EdgeType.WIFI));
        graph.addEdge(new Edge("LAB_2", "ATT_SVR", 3.0, Edge.EdgeType.WIFI));
        graph.addEdge(new Edge("LAB_3", "ATT_SVR", 6.0, Edge.EdgeType.WIFI));

        // WiFi connections: between labs
        graph.addEdge(new Edge("LAB_1", "LAB_2", 2.0, Edge.EdgeType.WIFI));
    }

    // ============================================================
    // CREATE DASHBOARD SCENE - Build the complete UI
    // ============================================================
    public Scene createDashboardScene(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0a0e17;");

        // ---- TOP: Header Bar ----
        root.setTop(createHeaderBar(primaryStage));

        // ---- LEFT: Control Panel ----
        root.setLeft(createControlPanel());

        // ---- CENTER: Graph + Output (split vertically) ----
        SplitPane centerSplit = new SplitPane();
        centerSplit.setOrientation(javafx.geometry.Orientation.VERTICAL);
        centerSplit.setDividerPositions(0.6);
        centerSplit.setStyle("-fx-background-color: #0a0e17;");

        // Graph visualization pane
        graphPane = new Pane();
        graphPane.setStyle("-fx-background-color: #0d1520; -fx-border-color: #1e2a3a; -fx-border-radius: 8; -fx-background-radius: 8;");
        graphPane.setPrefSize(700, 430);

        ScrollPane graphScroll = new ScrollPane(graphPane);
        graphScroll.setFitToWidth(true);
        graphScroll.setFitToHeight(true);
        graphScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Wrap graph in a container with title
        VBox graphContainer = new VBox(5);
        graphContainer.setPadding(new Insets(10, 10, 5, 10));
        graphContainer.setStyle("-fx-background-color: #0a0e17;");
        Label graphTitle = new Label("🌐  Network Topology");
        graphTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        graphTitle.setTextFill(Color.web("#8892a4"));
        graphContainer.getChildren().addAll(graphTitle, graphScroll);
        VBox.setVgrow(graphScroll, Priority.ALWAYS);

        // Bottom: Tabbed output area
        TabPane outputTabs = createOutputTabs();

        centerSplit.getItems().addAll(graphContainer, outputTabs);

        root.setCenter(centerSplit);

        // ---- RIGHT: Alerts Panel ----
        root.setRight(createAlertsPanel());

        // ---- BOTTOM: Status Bar ----
        root.setBottom(createStatusBar());

        // ---- Draw the initial graph ----
        drawGraph();

        // ---- Calculate initial risk scores ----
        riskAnalyzer.calculateRiskScores();
        refreshNodeTable();

        // ---- Start background monitoring ----
        startMonitoring();

        // ---- Add initial notifications ----
        notificationService.addAlert("System initialized. Network loaded with " +
                graph.getNodeCount() + " nodes.", NotificationService.AlertLevel.INFO);
        notificationService.addAlert("Monitoring active. Watching for threats...",
                NotificationService.AlertLevel.INFO);
        refreshAlerts();

        // ---- Try database connection ----
        connectDatabase();

        // Create and style scene
        Scene scene = new Scene(root, 1280, 780);

        // Load CSS stylesheet
        try {
            File cssFile = new File("resources/styles.css");
            if (cssFile.exists()) {
                scene.getStylesheets().add(cssFile.toURI().toString());
            }
        } catch (Exception e) {
            System.out.println("Could not load CSS: " + e.getMessage());
        }

        return scene;
    }

    // ============================================================
    // CREATE HEADER BAR
    // ============================================================
    private HBox createHeaderBar(Stage primaryStage) {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 20, 12, 20));
        header.setStyle(
            "-fx-background-color: linear-gradient(to right, #141b2d, #0d1520);" +
            "-fx-border-color: transparent transparent #1e2a3a transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        // Left: Logo + Title
        Label logo = new Label("🛡️");
        logo.setFont(Font.font(22));

        Label title = new Label("UniShield");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#00d4ff"));

        Label subtitle = new Label("Cyber Defense System");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        subtitle.setTextFill(Color.web("#6c7a8e"));

        VBox titleBox = new VBox(0, title, subtitle);

        // Spacer to push right content
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right: User info + Logout
        Label roleLabel = new Label(currentRole.toString());
        roleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        roleLabel.setTextFill(Color.web("#0a0e17"));
        roleLabel.setPadding(new Insets(3, 8, 3, 8));
        roleLabel.setStyle(
            "-fx-background-color: " + (currentRole == LoginController.UserRole.ADMIN ? "#00d4ff" : "#ffd700") + ";" +
            "-fx-background-radius: 4;"
        );

        Label userLabel = new Label("👤 " + currentUser);
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        userLabel.setTextFill(Color.web("#00ff88"));

        Button logoutBtn = new Button("Logout");
        logoutBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        logoutBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #6c7a8e;" +
            "-fx-border-color: #1e2a3a;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 14;" +
            "-fx-cursor: hand;"
        );
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
            "-fx-background-color: #ff4d4d22;" +
            "-fx-text-fill: #ff4d4d;" +
            "-fx-border-color: #ff4d4d44;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 14;" +
            "-fx-cursor: hand;"
        ));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #6c7a8e;" +
            "-fx-border-color: #1e2a3a;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 14;" +
            "-fx-cursor: hand;"
        ));
        logoutBtn.setOnAction(e -> {
            if (monitoringTimeline != null) monitoringTimeline.stop();
            dbManager.disconnect();
            if (onLogout != null) onLogout.run();
        });

        header.getChildren().addAll(logo, titleBox, spacer, roleLabel, userLabel, logoutBtn);
        return header;
    }

    // ============================================================
    // CREATE CONTROL PANEL (Left Sidebar)
    // ============================================================
    private VBox createControlPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(15));
        panel.setPrefWidth(255);
        panel.setStyle(
            "-fx-background-color: #0f1520;" +
            "-fx-border-color: transparent #1e2a3a transparent transparent;" +
            "-fx-border-width: 0 1 0 0;"
        );

        // ---- Stats Cards ----
        Label statsTitle = new Label("DASHBOARD STATS");
        statsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        statsTitle.setTextFill(Color.web("#6c7a8e"));

        // Node count stat
        nodeCountLabel = new Label(String.valueOf(graph.getNodeCount()));
        VBox nodeCountCard = createStatCard(nodeCountLabel, "Total Nodes", "#00d4ff");

        // Infected count stat
        infectedCountLabel = new Label("0");
        VBox infectedCard = createStatCard(infectedCountLabel, "Infected", "#ff4d4d");

        // Risk level stat
        riskLevelLabel = new Label("LOW");
        VBox riskCard = createStatCard(riskLevelLabel, "Threat Level", "#ffd700");

        HBox statsRow1 = new HBox(8, nodeCountCard, infectedCard);
        HBox statsRow2 = new HBox(8, riskCard);

        // ---- Action Buttons ----
        Label actionsTitle = new Label("ACTIONS");
        actionsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        actionsTitle.setTextFill(Color.web("#6c7a8e"));
        actionsTitle.setPadding(new Insets(10, 0, 0, 0));

        // Source node selector
        Label sourceLabel = new Label("Attack Source:");
        sourceLabel.setFont(Font.font("Segoe UI", 11));
        sourceLabel.setTextFill(Color.web("#8892a4"));

        ComboBox<String> sourceSelector = new ComboBox<>();
        sourceSelector.setStyle(
            "-fx-background-color: #0d1520; -fx-border-color: #1e2a3a;" +
            "-fx-border-radius: 6; -fx-background-radius: 6; -fx-font-size: 12px;"
        );
        sourceSelector.setPrefWidth(220);
        ArrayList<Node> allNodes = graph.getAllNodes();
        for (int i = 0; i < allNodes.size(); i++) {
            Node n = allNodes.get(i);
            sourceSelector.getItems().add(n.getId() + " - " + n.getName());
        }
        sourceSelector.getSelectionModel().selectFirst();

        // Buttons
        Button simButton = createActionButton("▶  Start Simulation", "#00d4ff");
        Button detectButton = createActionButton("🔍  Detect Threats", "#ffd700");
        Button defendButton = createActionButton("🛡️  Apply Defense", "#00ff88");
        Button resetButton = createActionButton("↺  Reset Network", "#ff4d4d");

        // Disable defense button for analysts
        if (currentRole == LoginController.UserRole.ANALYST) {
            defendButton.setDisable(true);
            defendButton.setStyle(defendButton.getStyle() + "-fx-opacity: 0.4;");
        }

        // ---- Button Actions ----
        simButton.setOnAction(e -> {
            String selected = sourceSelector.getValue();
            if (selected != null) {
                simulationSource = selected.split(" - ")[0];
                runSimulation();
            }
        });

        detectButton.setOnAction(e -> runDetection());
        defendButton.setOnAction(e -> applyDefenseAction());
        resetButton.setOnAction(e -> resetNetwork());

        // ---- Node Table ----
        Label tableTitle = new Label("NODE RISK SCORES");
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        tableTitle.setTextFill(Color.web("#6c7a8e"));
        tableTitle.setPadding(new Insets(10, 0, 0, 0));

        nodeTable = createNodeTable();

        // ---- Assemble Panel ----
        panel.getChildren().addAll(
            statsTitle, statsRow1, statsRow2,
            actionsTitle, sourceLabel, sourceSelector,
            simButton, detectButton, defendButton, resetButton,
            tableTitle, nodeTable
        );

        VBox.setVgrow(nodeTable, Priority.ALWAYS);

        return panel;
    }

    // ---- Helper: Create a stat card ----
    private VBox createStatCard(Label valueLabel, String label, String color) {
        VBox card = new VBox(2);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setPrefWidth(110);
        card.setStyle(
            "-fx-background-color: #141b2d;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + color + "22;" +
            "-fx-border-radius: 8;" +
            "-fx-border-width: 1;"
        );

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        valueLabel.setTextFill(Color.web(color));

        Label nameLabel = new Label(label);
        nameLabel.setFont(Font.font("Segoe UI", 10));
        nameLabel.setTextFill(Color.web("#6c7a8e"));

        card.getChildren().addAll(valueLabel, nameLabel);
        return card;
    }

    // ---- Helper: Create an action button ----
    private Button createActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(220);
        btn.setPrefHeight(36);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        String baseStyle = String.format(
            "-fx-background-color: %s18; -fx-text-fill: %s;" +
            "-fx-border-color: %s33; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-cursor: hand;" +
            "-fx-padding: 8 15;", color, color, color
        );
        String hoverStyle = String.format(
            "-fx-background-color: %s33; -fx-text-fill: %s;" +
            "-fx-border-color: %s66; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-cursor: hand;" +
            "-fx-padding: 8 15;", color, color, color
        );
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));
        return btn;
    }

    // ============================================================
    // CREATE NODE TABLE (TableView)
    // ============================================================
    @SuppressWarnings("unchecked")
    private TableView<Node> createNodeTable() {
        TableView<Node> table = new TableView<>();
        table.setPrefHeight(200);

        // ID Column
        TableColumn<Node, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getId()));
        idCol.setPrefWidth(65);

        // Name Column
        TableColumn<Node, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(85);

        // Type Column
        TableColumn<Node, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> {
            Node.NodeType type = data.getValue().getType();
            String shortType;
            switch (type) {
                case EXAM_SERVER: shortType = "EXAM"; break;
                case ATTENDANCE_SERVER: shortType = "ATT"; break;
                case LAB_SYSTEM: shortType = "LAB"; break;
                default: shortType = "STU"; break;
            }
            return new SimpleStringProperty(shortType);
        });
        typeCol.setPrefWidth(45);

        // Risk Score Column
        TableColumn<Node, Number> riskCol = new TableColumn<>("Risk");
        riskCol.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getRiskScore()));
        riskCol.setPrefWidth(40);

        // Status Column
        TableColumn<Node, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> {
            Node node = data.getValue();
            String status;
            if (node.isInfected()) status = "☠ INF";
            else if (node.isNodeProtected()) status = "🛡 PRO";
            else status = "✓ OK";
            return new SimpleStringProperty(status);
        });
        statusCol.setPrefWidth(55);

        table.getColumns().addAll(idCol, nameCol, typeCol, riskCol, statusCol);

        // Populate with node data
        refreshNodeTable();

        return table;
    }

    // ---- Refresh node table data ----
    private void refreshNodeTable() {
        if (nodeTable == null) return;
        ObservableList<Node> nodeData = FXCollections.observableArrayList();
        ArrayList<Node> allNodes = graph.getAllNodes();
        for (int i = 0; i < allNodes.size(); i++) {
            nodeData.add(allNodes.get(i));
        }
        nodeTable.setItems(nodeData);
        nodeTable.refresh();
    }

    // ============================================================
    // CREATE OUTPUT TABS
    // ============================================================
    private TabPane createOutputTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Output Tab
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setFont(Font.font("Consolas", 12));
        outputArea.setStyle(
            "-fx-control-inner-background: #0d1520;" +
            "-fx-text-fill: #c0c8d8;" +
            "-fx-font-family: 'Consolas';"
        );
        outputArea.setText("UniShield v1.0 — Cyber Defense System Ready\n" +
            "═══════════════════════════════════════════\n" +
            "Select an attack source and click 'Start Simulation'\n");

        Tab outputTab = new Tab("📋 Output", outputArea);

        // Alerts Tab (in the tab pane too)
        Tab alertsTab = new Tab("🔔 Event Log");
        TextArea eventLogArea = new TextArea();
        eventLogArea.setEditable(false);
        eventLogArea.setWrapText(true);
        eventLogArea.setFont(Font.font("Consolas", 11));
        eventLogArea.setStyle(
            "-fx-control-inner-background: #0d1520;" +
            "-fx-text-fill: #c0c8d8;" +
            "-fx-font-family: 'Consolas';"
        );
        alertsTab.setContent(eventLogArea);

        tabPane.getTabs().addAll(outputTab, alertsTab);

        // Update event log periodically
        Timeline logUpdater = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            ArrayList<String> log = notificationService.getEventLog();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < log.size(); i++) {
                sb.append(log.get(i)).append("\n");
            }
            eventLogArea.setText(sb.toString());
        }));
        logUpdater.setCycleCount(Timeline.INDEFINITE);
        logUpdater.play();

        return tabPane;
    }

    // ============================================================
    // CREATE ALERTS PANEL (Right Sidebar)
    // ============================================================
    private VBox createAlertsPanel() {
        VBox panel = new VBox(8);
        panel.setPadding(new Insets(15));
        panel.setPrefWidth(250);
        panel.setStyle(
            "-fx-background-color: #0f1520;" +
            "-fx-border-color: transparent transparent transparent #1e2a3a;" +
            "-fx-border-width: 0 0 0 1;"
        );

        Label title = new Label("🔔  LIVE ALERTS");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        title.setTextFill(Color.web("#8892a4"));

        alertsList = new ListView<>();
        alertsList.setStyle("-fx-background-color: #0d1520; -fx-border-color: #1e2a3a;");
        VBox.setVgrow(alertsList, Priority.ALWAYS);

        // DB Connection status
        Label dbTitle = new Label("DATABASE");
        dbTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        dbTitle.setTextFill(Color.web("#6c7a8e"));
        dbTitle.setPadding(new Insets(10, 0, 0, 0));

        Label dbStatus = new Label("⚪ Not connected");
        dbStatus.setFont(Font.font("Segoe UI", 11));
        dbStatus.setTextFill(Color.web("#6c7a8e"));
        dbStatus.setId("dbStatusLabel");

        Button dbConnectBtn = new Button("Connect MySQL");
        dbConnectBtn.setPrefWidth(218);
        dbConnectBtn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        dbConnectBtn.setStyle(
            "-fx-background-color: #141b2d; -fx-text-fill: #8892a4;" +
            "-fx-border-color: #1e2a3a; -fx-border-radius: 6;" +
            "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 6 12;"
        );
        dbConnectBtn.setOnAction(e -> {
            if (connectDatabase()) {
                dbStatus.setText("🟢 Connected");
                dbStatus.setTextFill(Color.web("#00ff88"));
                notificationService.addAlert("Database connected successfully!",
                    NotificationService.AlertLevel.INFO);
                refreshAlerts();
            } else {
                dbStatus.setText("🔴 Connection failed");
                dbStatus.setTextFill(Color.web("#ff4d4d"));
            }
        });

        panel.getChildren().addAll(title, alertsList, dbTitle, dbStatus, dbConnectBtn);
        return panel;
    }

    // ============================================================
    // CREATE STATUS BAR
    // ============================================================
    private HBox createStatusBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(8, 15, 8, 15));
        bar.setStyle(
            "-fx-background-color: #0d1520;" +
            "-fx-border-color: #1e2a3a transparent transparent transparent;" +
            "-fx-border-width: 1 0 0 0;"
        );

        statusLabel = new Label("✓ System Ready — Monitoring Active");
        statusLabel.setFont(Font.font("Segoe UI", 11));
        statusLabel.setTextFill(Color.web("#00ff88"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label versionLabel = new Label("UniShield v1.0  |  Nodes: " + graph.getNodeCount() +
            "  |  Edges: " + graph.getAllEdges().size());
        versionLabel.setFont(Font.font("Segoe UI", 10));
        versionLabel.setTextFill(Color.web("#4a5568"));

        bar.getChildren().addAll(statusLabel, spacer, versionLabel);
        return bar;
    }

    // ============================================================
    // DRAW GRAPH - Render network visualization on the pane
    // ============================================================
    private void drawGraph() {
        graphPane.getChildren().clear();
        nodeCircles.clear();
        nodeLabels.clear();
        edgeLines.clear();

        // ---- Step 1: Draw edges first (so they appear behind nodes) ----
        ArrayList<Edge> allEdges = graph.getAllEdges();
        for (int i = 0; i < allEdges.size(); i++) {
            Edge edge = allEdges.get(i);
            Node src = graph.getNode(edge.getSource());
            Node dst = graph.getNode(edge.getDestination());
            if (src == null || dst == null) continue;

            Line line = new Line(src.getXPos(), src.getYPos(), dst.getXPos(), dst.getYPos());

            if (!edge.isActive()) {
                line.setStroke(COLOR_EDGE_BLOCKED);
                line.getStrokeDashArray().addAll(8.0, 6.0);
            } else {
                line.setStroke(COLOR_EDGE_NORMAL);
            }
            line.setStrokeWidth(2);

            edgeLines.add(line);
            graphPane.getChildren().add(line);

            // Edge weight label
            double midX = (src.getXPos() + dst.getXPos()) / 2;
            double midY = (src.getYPos() + dst.getYPos()) / 2;
            Text weightText = new Text(midX - 5, midY - 5, String.format("%.0f", edge.getWeight()));
            weightText.setFill(Color.web("#4a5568"));
            weightText.setFont(Font.font("Segoe UI", 9));
            graphPane.getChildren().add(weightText);
        }

        // ---- Step 2: Draw nodes ----
        ArrayList<Node> allNodes = graph.getAllNodes();
        for (int i = 0; i < allNodes.size(); i++) {
            Node node = allNodes.get(i);
            double x = node.getXPos();
            double y = node.getYPos();

            // Determine node color based on state
            Color nodeColor;
            if (node.isInfected()) {
                nodeColor = COLOR_INFECTED;
            } else if (node.isNodeProtected()) {
                nodeColor = COLOR_PROTECTED;
            } else if (node.isCritical()) {
                nodeColor = COLOR_CRITICAL;
            } else {
                nodeColor = COLOR_NORMAL;
            }

            // Create circle for the node
            Circle circle = new Circle(x, y, NODE_RADIUS);
            circle.setFill(Color.web("#0d1520"));
            circle.setStroke(nodeColor);
            circle.setStrokeWidth(3);

            // Glow effect for critical/infected nodes
            if (node.isCritical() || node.isInfected()) {
                circle.setEffect(new javafx.scene.effect.DropShadow(15, nodeColor));
            }

            nodeCircles.put(node.getId(), circle);
            graphPane.getChildren().add(circle);

            // Node ID label
            Text label = new Text(x - 15, y + 4, node.getId());
            label.setFill(nodeColor);
            label.setFont(Font.font("Consolas", FontWeight.BOLD, 9));
            nodeLabels.put(node.getId(), label);
            graphPane.getChildren().add(label);

            // Node name below
            Text nameText = new Text(x - 25, y + NODE_RADIUS + 14, node.getName());
            nameText.setFill(Color.web("#6c7a8e"));
            nameText.setFont(Font.font("Segoe UI", 9));
            graphPane.getChildren().add(nameText);

            // Tooltip on hover
            Tooltip tooltip = new Tooltip(
                node.getName() + "\nType: " + node.getType() +
                "\nRisk: " + node.getRiskScore() +
                "\nStatus: " + (node.isInfected() ? "INFECTED" :
                    (node.isNodeProtected() ? "PROTECTED" : "NORMAL"))
            );
            Tooltip.install(circle, tooltip);
        }

        // ---- Step 3: Add legend ----
        drawLegend();
    }

    // ---- Draw legend in the graph pane ----
    private void drawLegend() {
        double startX = 10;
        double startY = 15;
        String[][] legendItems = {
            {"Normal", "#00d4ff"}, {"Critical", "#ffd700"},
            {"Infected", "#ff4d4d"}, {"Protected", "#00ff88"}
        };

        for (int i = 0; i < legendItems.length; i++) {
            Circle dot = new Circle(startX, startY + (i * 18), 5);
            dot.setFill(Color.web(legendItems[i][1]));
            Text txt = new Text(startX + 10, startY + (i * 18) + 4, legendItems[i][0]);
            txt.setFill(Color.web("#6c7a8e"));
            txt.setFont(Font.font("Segoe UI", 10));
            graphPane.getChildren().addAll(dot, txt);
        }
    }

    // ============================================================
    // RUN SIMULATION - BFS Malware Spread
    // ============================================================
    private void runSimulation() {
        if (simulationSource == null) {
            appendOutput("⚠ No source node selected.\n");
            return;
        }

        simulationRunning = true;
        updateStatus("🦠 Running malware spread simulation...", "#ff4d4d");

        // Clear previous simulation effects
        graph.resetAll();

        appendOutput("\n═══════════════════════════════════════════\n");
        appendOutput("🦠 MALWARE SPREAD SIMULATION (BFS)\n");
        appendOutput("═══════════════════════════════════════════\n");
        appendOutput("Source: " + simulationSource + " (" +
            graph.getNode(simulationSource).getName() + ")\n\n");

        // Simulate access attempts on random nodes for realism
        simulateAccessAttempts();

        // ---- Run BFS step by step ----
        MalwareSimulator sim = new MalwareSimulator(graph);
        ArrayList<ArrayList<String>> levels = sim.simulateSpreadByLevels(simulationSource);

        appendOutput("Infection Spread (Level by Level):\n");
        for (int level = 0; level < levels.size(); level++) {
            ArrayList<String> nodes = levels.get(level);
            StringBuilder sb = new StringBuilder();
            sb.append("  Wave ").append(level).append(": ");
            for (int j = 0; j < nodes.size(); j++) {
                Node n = graph.getNode(nodes.get(j));
                sb.append(nodes.get(j));
                if (n != null) sb.append(" (").append(n.getName()).append(")");
                if (j < nodes.size() - 1) sb.append(", ");
            }
            appendOutput(sb.toString() + "\n");
        }

        appendOutput("\nTotal Infected: " + sim.getInfectedCount() + " / " +
            graph.getNodeCount() + " nodes\n");

        // Update infected count display
        infectedCountLabel.setText(String.valueOf(sim.getInfectedCount()));

        // ---- Run Dijkstra to find fastest paths to critical nodes ----
        appendOutput("\n⚡ FASTEST INFECTION PATHS (Dijkstra):\n");
        ArrayList<DijkstraPathFinder.PathResult> criticalPaths =
            pathFinder.findPathsToCriticalNodes(simulationSource);

        for (int i = 0; i < criticalPaths.size(); i++) {
            DijkstraPathFinder.PathResult pr = criticalPaths.get(i);
            String targetId = pr.path.get(pr.path.size() - 1);
            Node targetNode = graph.getNode(targetId);
            appendOutput("  → " + (targetNode != null ? targetNode.getName() : targetId) +
                ": " + pr.getPathString() + " (weight: " +
                String.format("%.1f", pr.totalWeight) + ")\n");
        }

        // ---- Notifications ----
        notificationService.addAlert("Malware simulation started from " + simulationSource,
            NotificationService.AlertLevel.CRITICAL);

        int infectedCritical = 0;
        ArrayList<Node> criticalNodes = graph.getCriticalNodes();
        for (int i = 0; i < criticalNodes.size(); i++) {
            if (criticalNodes.get(i).isInfected()) {
                infectedCritical++;
                notificationService.addAlert(
                    criticalNodes.get(i).getName() + " has been COMPROMISED!",
                    NotificationService.AlertLevel.CRITICAL);
            }
        }

        // Update threat level
        if (sim.getInfectedCount() > graph.getNodeCount() / 2) {
            riskLevelLabel.setText("CRIT");
            riskLevelLabel.setTextFill(Color.web("#ff4d4d"));
        } else if (sim.getInfectedCount() > 2) {
            riskLevelLabel.setText("HIGH");
            riskLevelLabel.setTextFill(Color.web("#ffd700"));
        }

        // Redraw graph and refresh UI
        drawGraph();
        refreshNodeTable();
        refreshAlerts();

        updateStatus("🦠 Simulation complete — " + sim.getInfectedCount() + " nodes infected", "#ff4d4d");
    }

    // ============================================================
    // RUN DETECTION - DFS + Risk Analysis
    // ============================================================
    private void runDetection() {
        updateStatus("🔍 Running threat detection...", "#ffd700");

        appendOutput("\n═══════════════════════════════════════════\n");
        appendOutput("🔍 THREAT DETECTION & RISK ANALYSIS\n");
        appendOutput("═══════════════════════════════════════════\n");

        // ---- Step 1: Calculate risk scores ----
        appendOutput("\n📊 Risk Score Calculation:\n");
        HashMap<String, Integer> riskScores = riskAnalyzer.calculateRiskScores();
        ArrayList<Node> allNodes = graph.getAllNodes();
        for (int i = 0; i < allNodes.size(); i++) {
            Node node = allNodes.get(i);
            int score = riskScores.containsKey(node.getId()) ? riskScores.get(node.getId()) : 0;
            String bar = createProgressBar(score, 100, 20);
            appendOutput(String.format("  %-10s %-18s %s %d/100\n",
                node.getId(), node.getName(), bar, score));
        }

        // ---- Step 2: Find critical nodes using DFS ----
        appendOutput("\n🎯 Critical Node Detection (DFS):\n");
        ArrayList<Node> criticalNodes = riskAnalyzer.findCriticalNodes();
        for (int i = 0; i < criticalNodes.size(); i++) {
            Node cn = criticalNodes.get(i);
            appendOutput("  ★ " + cn.getId() + " — " + cn.getName() +
                " [" + cn.getType() + "]\n");
        }

        // ---- Step 3: DFS traversal order ----
        appendOutput("\n🔎 DFS Traversal Order:\n  ");
        ArrayList<String> dfsOrder = riskAnalyzer.performDFS(
            allNodes.get(0).getId());
        for (int i = 0; i < dfsOrder.size(); i++) {
            appendOutput(dfsOrder.get(i));
            if (i < dfsOrder.size() - 1) appendOutput(" → ");
        }
        appendOutput("\n");

        // ---- Step 4: Detect suspicious nodes ----
        appendOutput("\n⚠️ Suspicious Nodes Detected:\n");
        ArrayList<Node> suspicious = riskAnalyzer.detectSuspiciousNodes(3, 2);
        if (suspicious.isEmpty()) {
            appendOutput("  No suspicious activity detected.\n");
        } else {
            for (int i = 0; i < suspicious.size(); i++) {
                Node sn = suspicious.get(i);
                appendOutput("  ⚠ " + sn.getId() + " — " + sn.getName() +
                    " (connections: " + graph.getConnectionCount(sn.getId()) +
                    ", access attempts: " + sn.getAccessAttempts() + ")\n");
                notificationService.addAlert("Suspicious: " + sn.getName(),
                    NotificationService.AlertLevel.WARNING);
            }
        }

        // ---- Step 5: Check database for past patterns ----
        if (dbManager.isConnected() && simulationSource != null) {
            appendOutput("\n🧬 Learning System — Past Attack Patterns:\n");
            ArrayList<DatabaseManager.AttackRecord> history =
                dbManager.findSimilarAttacks(simulationSource);
            if (history.isEmpty()) {
                appendOutput("  No previous attacks from this source found.\n");
            } else {
                for (int i = 0; i < history.size(); i++) {
                    appendOutput("  📜 " + history.get(i).toString() + "\n");
                }
                String bestAction = dbManager.getBestPastAction(simulationSource);
                if (bestAction != null) {
                    appendOutput("  💡 " + bestAction + "\n");
                }
            }
        } else {
            appendOutput("\n🧬 Learning System: Database not connected (skipped)\n");
        }

        refreshNodeTable();
        refreshAlerts();
        updateStatus("🔍 Detection complete — Risk scores updated", "#ffd700");
    }

    // ============================================================
    // APPLY DEFENSE - Containment Actions
    // ============================================================
    private void applyDefenseAction() {
        if (currentRole != LoginController.UserRole.ADMIN) {
            appendOutput("\n⚠ Access Denied: Only ADMIN users can apply defenses.\n");
            return;
        }

        updateStatus("🛡️ Applying defense strategies...", "#00ff88");

        appendOutput("\n═══════════════════════════════════════════\n");
        appendOutput("🛡️ DEFENSE & CONTAINMENT\n");
        appendOutput("═══════════════════════════════════════════\n");

        // Collect currently infected nodes
        HashSet<String> infectedNodes = new HashSet<>();
        ArrayList<Node> allNodes = graph.getAllNodes();
        for (int i = 0; i < allNodes.size(); i++) {
            if (allNodes.get(i).isInfected()) {
                infectedNodes.add(allNodes.get(i).getId());
            }
        }

        int infectedBefore = infectedNodes.size();

        if (infectedBefore == 0) {
            appendOutput("\n✓ No infected nodes. Run a simulation first.\n");
            return;
        }

        // ---- Step 1: Generate recommendations ----
        appendOutput("\n🤖 Recommendations:\n");
        ArrayList<Node> criticalNodes = graph.getCriticalNodes();
        ArrayList<String> recommendations = defenseEngine.generateRecommendations(
            infectedNodes, criticalNodes, simulationSource);

        for (int i = 0; i < recommendations.size(); i++) {
            appendOutput("  " + (i + 1) + ". " + recommendations.get(i) + "\n");
        }

        // ---- Step 2: Apply containment ----
        appendOutput("\n🚧 Applying Containment Actions:\n");
        DefenseEngine.ComparisonResult comparison =
            defenseEngine.compareBeforeAfter(simulationSource);

        for (int i = 0; i < comparison.actionsApplied.size(); i++) {
            appendOutput("  ✓ " + comparison.actionsApplied.get(i) + "\n");
        }

        // ---- Step 3: Show Before vs After ----
        appendOutput("\n📊 BEFORE vs AFTER Comparison:\n");
        appendOutput("  ┌──────────────────────────────────────┐\n");
        appendOutput(String.format("  │  Before Defense: %d nodes infected     │\n", comparison.infectedBefore));
        appendOutput(String.format("  │  After Defense:  %d nodes infected     │\n", comparison.infectedAfter));
        appendOutput(String.format("  │  Damage Reduction: %.1f%%              │\n", comparison.damageReduction));
        appendOutput("  └──────────────────────────────────────┘\n");

        // ---- Step 4: Save to database ----
        if (dbManager.isConnected()) {
            // Build path string
            StringBuilder pathStr = new StringBuilder();
            MalwareSimulator tempSim = new MalwareSimulator(graph);
            ArrayList<String> order = tempSim.getInfectionOrder();
            if (order.isEmpty() && simulationSource != null) {
                pathStr.append(simulationSource);
            } else {
                for (int i = 0; i < order.size(); i++) {
                    pathStr.append(order.get(i));
                    if (i < order.size() - 1) pathStr.append(" -> ");
                }
            }

            // Build action string
            StringBuilder actionStr = new StringBuilder();
            for (int i = 0; i < comparison.actionsApplied.size(); i++) {
                actionStr.append(comparison.actionsApplied.get(i));
                if (i < comparison.actionsApplied.size() - 1) actionStr.append("; ");
            }

            boolean saved = dbManager.saveAttackHistory(
                simulationSource != null ? simulationSource : "Unknown",
                pathStr.toString(),
                actionStr.toString(),
                comparison.infectedBefore,
                comparison.damageReduction
            );

            if (saved) {
                appendOutput("\n🧬 Attack logged to database for future learning.\n");
                notificationService.addAlert("Attack data saved to database",
                    NotificationService.AlertLevel.INFO);
            }
        }

        // Update UI
        infectedCountLabel.setText(String.valueOf(comparison.infectedAfter));
        if (comparison.infectedAfter == 0) {
            riskLevelLabel.setText("SAFE");
            riskLevelLabel.setTextFill(Color.web("#00ff88"));
        } else {
            riskLevelLabel.setText("MED");
            riskLevelLabel.setTextFill(Color.web("#ffd700"));
        }

        notificationService.addAlert("Defense applied! Damage reduced by " +
            String.format("%.1f", comparison.damageReduction) + "%",
            NotificationService.AlertLevel.INFO);

        drawGraph();
        riskAnalyzer.calculateRiskScores();
        refreshNodeTable();
        refreshAlerts();

        updateStatus("🛡️ Defense applied — " + String.format("%.1f", comparison.damageReduction) +
            "% damage reduction", "#00ff88");
    }

    // ============================================================
    // RESET NETWORK - Clear all simulation state
    // ============================================================
    private void resetNetwork() {
        graph.resetAll();
        simulationRunning = false;
        simulationSource = null;

        infectedCountLabel.setText("0");
        riskLevelLabel.setText("LOW");
        riskLevelLabel.setTextFill(Color.web("#ffd700"));

        notificationService.clearAll();
        notificationService.addAlert("Network reset to clean state.",
            NotificationService.AlertLevel.INFO);
        notificationService.addAlert("Monitoring resumed.",
            NotificationService.AlertLevel.INFO);

        riskAnalyzer.calculateRiskScores();
        drawGraph();
        refreshNodeTable();
        refreshAlerts();

        outputArea.setText("UniShield v1.0 — Cyber Defense System Ready\n" +
            "═══════════════════════════════════════════\n" +
            "Network has been reset. Select a source and run simulation.\n");

        updateStatus("✓ Network reset — All systems normal", "#00ff88");
    }

    // ============================================================
    // BACKGROUND MONITORING - Periodic threat check
    // ============================================================
    private void startMonitoring() {
        monitoringTimeline = new Timeline(new KeyFrame(Duration.seconds(8), e -> {
            // Simulate background monitoring
            // Check for nodes with high connection counts
            ArrayList<Node> allNodes = graph.getAllNodes();
            for (int i = 0; i < allNodes.size(); i++) {
                Node node = allNodes.get(i);
                int connections = graph.getConnectionCount(node.getId());
                if (connections >= 4 && !node.isInfected()) {
                    node.incrementAccessAttempts();
                    if (node.getAccessAttempts() % 3 == 0) {
                        notificationService.addAlert(
                            "High traffic on " + node.getName() +
                            " (" + connections + " connections)",
                            NotificationService.AlertLevel.WARNING);
                        refreshAlerts();
                    }
                }
            }
        }));
        monitoringTimeline.setCycleCount(Timeline.INDEFINITE);
        monitoringTimeline.play();
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private void simulateAccessAttempts() {
        // Simulate random access attempts for realism
        ArrayList<Node> allNodes = graph.getAllNodes();
        for (int i = 0; i < allNodes.size(); i++) {
            Node node = allNodes.get(i);
            int random = (int) (Math.random() * 5);
            for (int j = 0; j < random; j++) {
                node.incrementAccessAttempts();
            }
        }
    }

    private void appendOutput(String text) {
        if (outputArea != null) {
            outputArea.appendText(text);
        }
    }

    private void refreshAlerts() {
        if (alertsList == null) return;
        ObservableList<String> alerts = FXCollections.observableArrayList();
        ArrayList<NotificationService.Alert> allAlerts = notificationService.getAllAlerts();
        // Show most recent alerts first
        for (int i = allAlerts.size() - 1; i >= 0; i--) {
            alerts.add(allAlerts.get(i).getFormattedAlert());
        }
        alertsList.setItems(alerts);
    }

    private void updateStatus(String message, String color) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setTextFill(Color.web(color));
        }
    }

    private String createProgressBar(int value, int max, int width) {
        int filled = (int) ((double) value / max * width);
        StringBuilder bar = new StringBuilder("│");
        for (int i = 0; i < width; i++) {
            if (i < filled) bar.append("█");
            else bar.append("░");
        }
        bar.append("│");
        return bar.toString();
    }

    private boolean connectDatabase() {
        boolean success = dbManager.connect();
        if (success) {
            notificationService.addToLog("Database connected: MySQL");
        } else {
            notificationService.addToLog("Database connection failed (MySQL not available)");
        }
        return success;
    }

    // ---- Setter for logout callback ----
    public void setOnLogout(Runnable callback) {
        this.onLogout = callback;
    }
}
