package com.lab.system.service;

import com.lab.system.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataService {
    private static final String DATA_FILE = "lab_system_data.dat";

    private List<User> users = new ArrayList<>();
    private List<Equipment> equipmentList = new ArrayList<>();
    private List<BorrowingRecord> borrowingRecords = new ArrayList<>();
    private List<ActivityLog> activityLogs = new ArrayList<>();

    public DataService() {
        loadData();
        if (users.isEmpty()) {
            seedInitialUsers();
        }
    }

    private void seedInitialUsers() {
        users.add(new User("U001", "System Admin", "admin", "admin123", "Administrator", "IT Services"));
        users.add(new User("U002", "John Staff", "staff", "staff123", "Equipment Staff", "Laboratory"));
        users.add(new User("U003", "Alice Student", "student", "student123", "Borrower", "Computer Science"));
        users.add(new User("U004", "Dr. Smith", "depthead", "head123", "Department Head", "Computer Science"));
        saveData();
    }

    // --- GETTERS ---
    public List<User> getUsers() { return users; }
    public List<Equipment> getEquipmentList() { return equipmentList; }
    public List<BorrowingRecord> getBorrowingRecords() { return borrowingRecords; }
    public List<ActivityLog> getActivityLogs() { return activityLogs; }

    // --- HELPER / LOOKUP METHODS ---
    public User findUserById(String userId) {
        return users.stream()
                .filter(u -> u.getUserId().equalsIgnoreCase(userId))
                .findFirst()
                .orElse(null);
    }

    public Equipment findEquipmentById(String equipmentId) {
        return equipmentList.stream()
                .filter(e -> e.getEquipmentId().equalsIgnoreCase(equipmentId))
                .findFirst()
                .orElse(null);
    }

    public BorrowingRecord findBorrowingRecordById(String borrowingId) {
        return borrowingRecords.stream()
                .filter(r -> r.getBorrowingId().equalsIgnoreCase(borrowingId))
                .findFirst()
                .orElse(null);
    }

    // --- LOGGING UTILITY ---
    public void logActivity(String userId, String action) {
        String logId = "L" + (activityLogs.size() + 1001);
        activityLogs.add(new ActivityLog(logId, userId, action));
        saveData();
    }

    // --- FILE I/O PERSISTENCE ---
    @SuppressWarnings("unchecked")
    public void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (List<User>) ois.readObject();
            equipmentList = (List<Equipment>) ois.readObject();
            borrowingRecords = (List<BorrowingRecord>) ois.readObject();
            activityLogs = (List<ActivityLog>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Notice: Failed to load existing data file (" + e.getMessage() + "). Initializing clean storage.");
            users = new ArrayList<>();
            equipmentList = new ArrayList<>();
            borrowingRecords = new ArrayList<>();
            activityLogs = new ArrayList<>();
        }
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
            oos.writeObject(equipmentList);
            oos.writeObject(borrowingRecords);
            oos.writeObject(activityLogs);
        } catch (IOException e) {
            System.err.println("Error saving system data: " + e.getMessage());
        }
    }
}