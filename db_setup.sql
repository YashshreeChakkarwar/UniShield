-- ============================================================
-- UniShield - Cyber Defense System
-- Database Setup Script
-- ============================================================

-- Create the database
CREATE DATABASE IF NOT EXISTS unishield_db;
USE unishield_db;

-- Create AttackHistory table to store past attack data
CREATE TABLE IF NOT EXISTS AttackHistory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sourceNode VARCHAR(50) NOT NULL,
    path TEXT NOT NULL,
    actionTaken TEXT NOT NULL,
    infectedCount INT DEFAULT 0,
    damageReduction DOUBLE DEFAULT 0.0,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Insert some sample historical data for the learning system
INSERT INTO AttackHistory (sourceNode, path, actionTaken, infectedCount, damageReduction) VALUES
('STU_1', 'STU_1 -> LAB_1 -> EXAM_SERVER', 'Isolated STU_1, Blocked STU_1-LAB_1', 4, 65.0),
('STU_3', 'STU_3 -> LAB_2 -> ATTENDANCE_SERVER', 'Isolated STU_3, Protected ATTENDANCE_SERVER', 3, 70.0),
('STU_5', 'STU_5 -> LAB_3 -> ATTENDANCE_SERVER', 'Blocked LAB_3-ATTENDANCE_SERVER', 2, 80.0);

SELECT 'UniShield database setup complete!' AS Status;
