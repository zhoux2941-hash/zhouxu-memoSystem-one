package com.example.taskmanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

@Autowired
private JdbcTemplate jdbcTemplate;

@Override
public void run(String... args) throws Exception {
// Create tables if not exist
jdbcTemplate.execute("""
CREATE TABLE IF NOT EXISTS tasks (
id INTEGER PRIMARY KEY AUTOINCREMENT,
title VARCHAR(200) NOT NULL,
description VARCHAR(2000),
status VARCHAR(20) DEFAULT 'PENDING',
priority VARCHAR(20) DEFAULT 'MEDIUM',
due_date TIMESTAMP,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
completed_at TIMESTAMP,
category VARCHAR(100),
tags VARCHAR(500)
)
""");

jdbcTemplate.execute("""
CREATE TABLE IF NOT EXISTS categories (
id INTEGER PRIMARY KEY AUTOINCREMENT,
name VARCHAR(100) NOT NULL UNIQUE,
description VARCHAR(500),
color VARCHAR(20),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
""");

jdbcTemplate.execute("""
CREATE TABLE IF NOT EXISTS task_audit_log (
id INTEGER PRIMARY KEY AUTOINCREMENT,
task_id INTEGER,
action VARCHAR(50),
old_value VARCHAR(500),
new_value VARCHAR(500),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)
""");

// Insert sample categories if empty
Long count = jdbcTemplate.queryForObject(
"SELECT COUNT(*) FROM categories", Long.class);

if (count != null && count == 0) {
jdbcTemplate.update("INSERT INTO categories (name, description, color) VALUES (?, ?, ?)",
"Work", "Work-related tasks", "#FF5722");
jdbcTemplate.update("INSERT INTO categories (name, description, color) VALUES (?, ?, ?)",
"Personal", "Personal tasks", "#4CAF50");
jdbcTemplate.update("INSERT INTO categories (name, description, color) VALUES (?, ?, ?)",
"Shopping", "Shopping list", "#2196F3");
jdbcTemplate.update("INSERT INTO categories (name, description, color) VALUES (?, ?, ?)",
"Health", "Health and fitness", "#9C27B0");
}

// Insert sample tasks if empty
Long taskCount = jdbcTemplate.queryForObject(
"SELECT COUNT(*) FROM tasks", Long.class);

if (taskCount != null && taskCount == 0) {
jdbcTemplate.update("""
INSERT INTO tasks (title, description, status, priority, category, due_date) 
VALUES (?, ?, ?, ?, ?, datetime('now', '+7 days'))
""", "Complete project documentation", 
"Write comprehensive documentation for the task manager application", 
"PENDING", "HIGH", "Work");

jdbcTemplate.update("""
INSERT INTO tasks (title, description, status, priority, category, due_date) 
VALUES (?, ?, ?, ?, ?, datetime('now', '+3 days'))
""", "Review code changes", 
"Review pull requests and merge approved changes", 
"IN_PROGRESS", "MEDIUM", "Work");

jdbcTemplate.update("""
INSERT INTO tasks (title, description, status, priority, category, due_date) 
VALUES (?, ?, ?, ?, ?, datetime('now', '+1 day'))
""", "Buy groceries", 
"Milk, bread, eggs, vegetables, fruits", 
"PENDING", "HIGH", "Shopping");

jdbcTemplate.update("""
INSERT INTO tasks (title, description, status, priority, category) 
VALUES (?, ?, ?, ?, ?)
""", "Morning workout", 
"30 minutes cardio and strength training", 
"PENDING", "MEDIUM", "Health");

jdbcTemplate.update("""
INSERT INTO tasks (title, description, status, priority, category, due_date) 
VALUES (?, ?, ?, ?, ?, datetime('now', '-2 days'))
""", "Pay utility bills", 
"Electricity and water bills are due", 
"PENDING", "URGENT", "Personal");
}
}
}