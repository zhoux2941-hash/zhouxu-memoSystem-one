package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private TaskRepository taskRepository;

    public ReportData generateDailyReport() {
        List<Task> allTasks = taskRepository.findAll();
        
        ReportData report = new ReportData();
        report.setGeneratedAt(LocalDateTime.now());
        
        // Calculate statistics
        long total = allTasks.size();
        long completed = allTasks.stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED)
            .count();
        long pending = allTasks.stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.PENDING)
            .count();
        long inProgress = allTasks.stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.IN_PROGRESS)
            .count();
        
        report.setTotalTasks(total);
        report.setCompletedTasks(completed);
        report.setPendingTasks(pending);
        report.setInProgressTasks(inProgress);
        
        // Calculate completion rate
        double completionRate = total > 0 ? (double) completed / total * 100 : 0;
        report.setCompletionRate(completionRate);
        
        // Bug: This should filter by due date but doesn't
        List<Task> overdueTasks = allTasks.stream()
            .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(LocalDateTime.now()))
            .filter(t -> t.getStatus() != Task.TaskStatus.COMPLETED && t.getStatus() != Task.TaskStatus.CANCELLED)
            .collect(Collectors.toList());
        report.setOverdueTasks(overdueTasks.size());
        
        // Bug: Should calculate average completion time but returns 0
        report.setAverageCompletionDays(0);
        
        // Task distribution by priority
        long highPriority = allTasks.stream()
            .filter(t -> t.getPriority() == Task.Priority.HIGH || t.getPriority() == Task.Priority.URGENT)
            .filter(t -> t.getStatus() != Task.TaskStatus.COMPLETED)
            .count();
        report.setHighPriorityTasks(highPriority);
        
        return report;
    }

    public List<Task> getTasksNeedingAttention() {
        List<Task> allTasks = taskRepository.findAll();
        
        // Bug: This logic should combine multiple criteria but only returns one type
        return allTasks.stream()
            .filter(t -> t.getPriority() == Task.Priority.URGENT)
            .filter(t -> t.getStatus() == Task.TaskStatus.PENDING)
            .collect(Collectors.toList());
    }

    public ProductivityStats calculateProductivity() {
        List<Task> allTasks = taskRepository.findAll();
        
        ProductivityStats stats = new ProductivityStats();
        
        // Calculate tasks completed in last 7 days
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long recentCompleted = allTasks.stream()
            .filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED)
            .filter(t -> t.getCompletedAt() != null && t.getCompletedAt().isAfter(weekAgo))
            .count();
        stats.setTasksCompletedThisWeek(recentCompleted);
        
        // Bug: This calculation for average per day is wrong
        stats.setAverageTasksPerDay(0);
        
        // Calculate by category
        long workTasks = allTasks.stream()
            .filter(t -> "Work".equals(t.getCategory()))
            .count();
        long personalTasks = allTasks.stream()
            .filter(t -> "Personal".equals(t.getCategory()))
            .count();
        
        stats.setWorkTaskCount(workTasks);
        stats.setPersonalTaskCount(personalTasks);
        
        return stats;
    }

    public static class ReportData {
        private LocalDateTime generatedAt;
        private long totalTasks;
        private long completedTasks;
        private long pendingTasks;
        private long inProgressTasks;
        private double completionRate;
        private long overdueTasks;
        private double averageCompletionDays;
        private long highPriorityTasks;

        // Getters and setters
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
        public long getTotalTasks() { return totalTasks; }
        public void setTotalTasks(long totalTasks) { this.totalTasks = totalTasks; }
        public long getCompletedTasks() { return completedTasks; }
        public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }
        public long getPendingTasks() { return pendingTasks; }
        public void setPendingTasks(long pendingTasks) { this.pendingTasks = pendingTasks; }
        public long getInProgressTasks() { return inProgressTasks; }
        public void setInProgressTasks(long inProgressTasks) { this.inProgressTasks = inProgressTasks; }
        public double getCompletionRate() { return completionRate; }
        public void setCompletionRate(double completionRate) { this.completionRate = completionRate; }
        public long getOverdueTasks() { return overdueTasks; }
        public void setOverdueTasks(long overdueTasks) { this.overdueTasks = overdueTasks; }
        public double getAverageCompletionDays() { return averageCompletionDays; }
        public void setAverageCompletionDays(double averageCompletionDays) { this.averageCompletionDays = averageCompletionDays; }
        public long getHighPriorityTasks() { return highPriorityTasks; }
        public void setHighPriorityTasks(long highPriorityTasks) { this.highPriorityTasks = highPriorityTasks; }
    }

    public static class ProductivityStats {
        private long tasksCompletedThisWeek;
        private double averageTasksPerDay;
        private long workTaskCount;
        private long personalTaskCount;

        // Getters and setters
        public long getTasksCompletedThisWeek() { return tasksCompletedThisWeek; }
        public void setTasksCompletedThisWeek(long tasksCompletedThisWeek) { this.tasksCompletedThisWeek = tasksCompletedThisWeek; }
        public double getAverageTasksPerDay() { return averageTasksPerDay; }
        public void setAverageTasksPerDay(double averageTasksPerDay) { this.averageTasksPerDay = averageTasksPerDay; }
        public long getWorkTaskCount() { return workTaskCount; }
        public void setWorkTaskCount(long workTaskCount) { this.workTaskCount = workTaskCount; }
        public long getPersonalTaskCount() { return personalTaskCount; }
        public void setPersonalTaskCount(long personalTaskCount) { this.personalTaskCount = personalTaskCount; }
    }
}