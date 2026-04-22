package com.unishield;

import java.util.ArrayList;
import java.util.LinkedList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================
 * NOTIFICATION SERVICE - Alert System & Event Logging
 * ============================================================
 * Manages alerts at different severity levels and maintains
 * an event log. Uses a LinkedList as a Queue for alerts.
 */
public class NotificationService {

    // ---- Alert severity levels ----
    public enum AlertLevel {
        INFO,       // General information
        WARNING,    // Suspicious activity detected
        CRITICAL    // Active threat or critical event
    }

    // ---- Inner class for alert objects ----
    public static class Alert {
        public String message;
        public AlertLevel level;
        public String timestamp;

        public Alert(String message, AlertLevel level) {
            this.message = message;
            this.level = level;
            // Format timestamp manually
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            this.timestamp = LocalDateTime.now().format(formatter);
        }

        // Get alert with icon based on severity
        public String getFormattedAlert() {
            String icon;
            switch (level) {
                case CRITICAL:
                    icon = "🚨";
                    break;
                case WARNING:
                    icon = "⚠️";
                    break;
                default:
                    icon = "ℹ️";
                    break;
            }
            return String.format("[%s] %s %s", timestamp, icon, message);
        }

        @Override
        public String toString() {
            return getFormattedAlert();
        }
    }

    // ---- Data Structures ----
    private LinkedList<Alert> alertQueue;     // Queue of pending alerts (FIFO)
    private ArrayList<Alert> allAlerts;       // Historical record of all alerts
    private ArrayList<String> eventLog;      // General event log

    // ---- Constructor ----
    public NotificationService() {
        this.alertQueue = new LinkedList<>();
        this.allAlerts = new ArrayList<>();
        this.eventLog = new ArrayList<>();
    }

    // ============================================================
    // ADD ALERT - Create and queue a new alert
    // ============================================================
    public void addAlert(String message, AlertLevel level) {
        Alert alert = new Alert(message, level);

        // Add to queue (FIFO)
        alertQueue.addLast(alert);

        // Add to historical record
        allAlerts.add(alert);

        // Also add to event log
        addToLog(alert.getFormattedAlert());
    }

    // ============================================================
    // GET NEXT ALERT - Dequeue the oldest pending alert
    // ============================================================
    public Alert getNextAlert() {
        if (alertQueue.isEmpty()) {
            return null;
        }
        return alertQueue.removeFirst();
    }

    // ============================================================
    // HAS PENDING ALERTS - Check if there are unread alerts
    // ============================================================
    public boolean hasPendingAlerts() {
        return !alertQueue.isEmpty();
    }

    // ============================================================
    // GET ALL ALERTS - Get complete alert history
    // ============================================================
    public ArrayList<Alert> getAllAlerts() {
        return allAlerts;
    }

    // ============================================================
    // GET ALERTS BY LEVEL - Filter alerts by severity
    // ============================================================
    public ArrayList<Alert> getAlertsByLevel(AlertLevel level) {
        ArrayList<Alert> filtered = new ArrayList<>();
        for (int i = 0; i < allAlerts.size(); i++) {
            if (allAlerts.get(i).level == level) {
                filtered.add(allAlerts.get(i));
            }
        }
        return filtered;
    }

    // ============================================================
    // GET ALERT COUNT - Total number of alerts
    // ============================================================
    public int getAlertCount() {
        return allAlerts.size();
    }

    // ============================================================
    // ADD TO EVENT LOG - Log a general event
    // ============================================================
    public void addToLog(String event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        eventLog.add("[" + timestamp + "] " + event);
    }

    // ============================================================
    // GET EVENT LOG - Retrieve the full event log
    // ============================================================
    public ArrayList<String> getEventLog() {
        return eventLog;
    }

    // ============================================================
    // CLEAR ALL - Reset the notification system
    // ============================================================
    public void clearAll() {
        alertQueue.clear();
        allAlerts.clear();
        eventLog.clear();
    }

    // ============================================================
    // GET FORMATTED ALERT LIST - Get all alerts as formatted strings
    // ============================================================
    public ArrayList<String> getFormattedAlerts() {
        ArrayList<String> formatted = new ArrayList<>();
        for (int i = 0; i < allAlerts.size(); i++) {
            formatted.add(allAlerts.get(i).getFormattedAlert());
        }
        return formatted;
    }
}
