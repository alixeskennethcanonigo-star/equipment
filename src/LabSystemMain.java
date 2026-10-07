package com.lab.system.main;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LabSystemMain {

    // =========================================================================
    // 1. DATA MODELS
    // =========================================================================

    public static class User implements Serializable {
        private static final long serialVersionUID = 1L;

        private String userId;
        private String name;
        private String username;
        private String password;
        private String role; // Administrator, Equipment Staff, Borrower, Department Head
        private String department;

        public User(String userId, String name, String username, String password, String role, String department) {
            this.userId = userId;
            this.name = name;
            this.username = username;
            this.password = password;
            this.role = role;
            this.department = department;
        }

        public String getUserId() { return userId; }
        public String getName() { return name; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
        public String getDepartment() { return department; }

        public void setName(String name) { this.name = name; }
        public void setUsername(String username) { this.username = username; }
        public void setPassword(String password) { this.password = password; }
        public void setRole(String role) { this.role = role; }
        public void setDepartment(String department) { this.department = department; }

        @Override
        public String toString() {
            return String.format("ID: %-8s | Name: %-18s | Username: %-12s | Role: %-15s | Dept: %s",
                    userId, name, username, role, department);
        }
    }

    public static class Equipment implements Serializable {
        private static final long serialVersionUID = 1L;

        private String equipmentId;
        private String equipmentName;
        private String category;
        private int quantity;
        private int availableQuantity;
        private String condition; // Good, Minor Damage, Severely Damaged
        private String status;    // Available, In-Use, Maintenance

        public Equipment(String equipmentId, String equipmentName, String category, int quantity, String condition) {
            this.equipmentId = equipmentId;
            this.equipmentName = equipmentName;
            this.category = category;
            this.quantity = Math.max(0, quantity);
            this.availableQuantity = this.quantity;
            this.condition = condition;
            this.status = "Available";
        }

        public String getEquipmentId() { return equipmentId; }
        public String getEquipmentName() { return equipmentName; }
        public String getCategory() { return category; }
        public int getQuantity() { return quantity; }
        public int getAvailableQuantity() { return availableQuantity; }
        public String getCondition() { return condition; }
        public String getStatus() { return status; }

        public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
        public void setCategory(String category) { this.category = category; }
        public void setCondition(String condition) { this.condition = condition; }
        public void setStatus(String status) { this.status = status; }

        public void setQuantity(int quantity) {
            int diff = quantity - this.quantity;
            this.quantity = quantity;
            this.availableQuantity = Math.max(0, this.availableQuantity + diff);
        }

        public boolean allocateQuantity(int qty) {
            if (qty > 0 && qty <= availableQuantity) {
                availableQuantity -= qty;
                if (availableQuantity == 0) this.status = "In-Use";
                return true;
            }
            return false;
        }

        public void releaseQuantity(int qty) {
            if (qty > 0) {
                availableQuantity = Math.min(quantity, availableQuantity + qty);
                if (availableQuantity > 0) this.status = "Available";
            }
        }

        @Override
        public String toString() {
            return String.format("ID: %-8s | Name: %-18s | Category: %-12s | Stock: %d/%d | Condition: %-12s | Status: %s",
                    equipmentId, equipmentName, category, availableQuantity, quantity, condition, status);
        }
    }

    public static class BorrowingRecord implements Serializable {
        private static final long serialVersionUID = 1L;

        private String borrowingId;
        private String userId;
        private String equipmentId;
        private int quantity;
        private String borrowDate;
        private String dueDate;
        private String returnDate;
        private String borrowStatus; // Pending Approval, Approved, Borrowed, Returned, Rejected, Overdue
        private String returnCondition;
        private double fineOrPenalty;

        public BorrowingRecord(String borrowingId, String userId, String equipmentId, int quantity, String borrowDate, String dueDate) {
            this.borrowingId = borrowingId;
            this.userId = userId;
            this.equipmentId = equipmentId;
            this.quantity = quantity;
            this.borrowDate = borrowDate;
            this.dueDate = dueDate;
            this.returnDate = "N/A";
            this.borrowStatus = "Pending Approval";
            this.returnCondition = "Pending";
            this.fineOrPenalty = 0.0;
        }

        public String getBorrowingId() { return borrowingId; }
        public String getUserId() { return userId; }
        public String getEquipmentId() { return equipmentId; }
        public int getQuantity() { return quantity; }
        public String getBorrowDate() { return borrowDate; }
        public String getDueDate() { return dueDate; }
        public String getReturnDate() { return returnDate; }
        public String getBorrowStatus() { return borrowStatus; }
        public String getReturnCondition() { return returnCondition; }
        public double getFineOrPenalty() { return fineOrPenalty; }

        public void setUserId(String userId) { this.userId = userId; }
        public void setEquipmentId(String equipmentId) { this.equipmentId = equipmentId; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
        public void setDueDate(String dueDate) { this.dueDate = dueDate; }
        public void setBorrowStatus(String borrowStatus) { this.borrowStatus = borrowStatus; }
        public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
        public void setReturnCondition(String returnCondition) { this.returnCondition = returnCondition; }
        public void setFineOrPenalty(double fineOrPenalty) { this.fineOrPenalty = fineOrPenalty; }

        @Override
        public String toString() {
            return String.format("BorrowID: %-8s | UserID: %-8s | EqID: %-8s | Qty: %-2d | Due: %-10s | Status: %-16s | Fine: $%.2f",
                    borrowingId, userId, equipmentId, quantity, dueDate, borrowStatus, fineOrPenalty);
        }
    }

    public static class ActivityLog implements Serializable {
        private static final long serialVersionUID = 1L;

        private String logId;
        private String userId;
        private String action;
        private String timestamp;

        public ActivityLog(String logId, String userId, String action) {
            this.logId = logId;
            this.userId = userId;
            this.action = action;
            this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        public String getLogId() { return logId; }
        public String getUserId() { return userId; }
        public String getAction() { return action; }
        public String getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("[%s] LogID: %-6s | User: %-8s | Event: %s", timestamp, logId, userId, action);
        }
    }

    // =========================================================================
    // 2. DATA SERVICE & PERSISTENCE
    // =========================================================================

    public static class DataService {
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

        public List<User> getUsers() { return users; }
        public List<Equipment> getEquipmentList() { return equipmentList; }
        public List<BorrowingRecord> getBorrowingRecords() { return borrowingRecords; }
        public List<ActivityLog> getActivityLogs() { return activityLogs; }

        public User findUserByUsername(String username) {
            return users.stream()
                    .filter(u -> u.getUsername().equalsIgnoreCase(username))
                    .findFirst()
                    .orElse(null);
        }

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

        public void logActivity(String userId, String action) {
            String logId = "L" + (activityLogs.size() + 1001);
            activityLogs.add(new ActivityLog(logId, userId, action));
            saveData();
        }

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
                System.err.println("Notice: Initializing clean storage arrays.");
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

    // =========================================================================
    // 3. REPORT SERVICE
    // =========================================================================

    public static class ReportService {

        public static void generateCsvReport(DataService dataService, String filePath) {
            List<Equipment> equipment = dataService.getEquipmentList();
            List<BorrowingRecord> records = dataService.getBorrowingRecords();

            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                writer.println("Equipment Borrowing Summary Report");
                writer.println("Generated Date: " + LocalDate.now());
                writer.println();

                writer.println("Equipment ID,Equipment Name,Category,Total Qty,Available Qty,Condition,Status");
                for (Equipment eq : equipment) {
                    writer.printf("%s,%s,%s,%d,%d,%s,%s\n",
                            escapeCsv(eq.getEquipmentId()), escapeCsv(eq.getEquipmentName()),
                            escapeCsv(eq.getCategory()), eq.getQuantity(), eq.getAvailableQuantity(),
                            escapeCsv(eq.getCondition()), escapeCsv(eq.getStatus()));
                }

                writer.println();
                writer.println("Borrowing Records");
                writer.println("Borrowing ID,User ID,Equipment ID,Quantity,Borrow Date,Due Date,Return Date,Status,Fine");
                for (BorrowingRecord rec : records) {
                    writer.printf("%s,%s,%s,%d,%s,%s,%s,%s,%.2f\n",
                            escapeCsv(rec.getBorrowingId()), escapeCsv(rec.getUserId()),
                            escapeCsv(rec.getEquipmentId()), rec.getQuantity(),
                            rec.getBorrowDate(), rec.getDueDate(), rec.getReturnDate(),
                            escapeCsv(rec.getBorrowStatus()), rec.getFineOrPenalty());
                }

                System.out.println("Report successfully saved to CSV: " + filePath);
            } catch (IOException e) {
                System.out.println("Failed to write CSV report: " + e.getMessage());
            }
        }

        public static void printSummaryReport(DataService dataService) {
            List<Equipment> equipment = dataService.getEquipmentList();
            List<BorrowingRecord> records = dataService.getBorrowingRecords();

            int totalEq = equipment.stream().mapToInt(Equipment::getQuantity).sum();
            int availableEq = equipment.stream().mapToInt(Equipment::getAvailableQuantity).sum();
            long borrowedCount = records.stream().filter(r -> r.getBorrowStatus().equalsIgnoreCase("Borrowed")).count();
            long returnedCount = records.stream().filter(r -> r.getBorrowStatus().equalsIgnoreCase("Returned")).count();
            long damagedCount = equipment.stream().filter(e -> e.getCondition() != null && e.getCondition().toLowerCase().contains("damage")).count();

            LocalDate today = LocalDate.now();
            long overdueCount = records.stream().filter(r -> isRecordOverdue(r, today)).count();

            System.out.println("\n=======================================================");
            System.out.println("        EQUIPMENT BORROWING SUMMARY REPORT            ");
            System.out.println("=======================================================");
            System.out.printf("Total Units Registered : %d\n", totalEq);
            System.out.printf("Total Units Available  : %d\n", availableEq);
            System.out.printf("Active Borrowed Items  : %d\n", borrowedCount);
            System.out.printf("Total Returned Items   : %d\n", returnedCount);
            System.out.printf("Damaged Equipment Items: %d\n", damagedCount);
            System.out.printf("Overdue Transactions   : %d\n", overdueCount);
            System.out.println("=======================================================");
        }

        private static boolean isRecordOverdue(BorrowingRecord record, LocalDate today) {
            if ("Overdue".equalsIgnoreCase(record.getBorrowStatus())) return true;
            if ("Borrowed".equalsIgnoreCase(record.getBorrowStatus())) {
                try {
                    LocalDate dueDate = LocalDate.parse(record.getDueDate());
                    return today.isAfter(dueDate);
                } catch (DateTimeParseException | NullPointerException ignored) { }
            }
            return false;
        }

        private static String escapeCsv(String value) {
            if (value == null) return "";
            if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                value = value.replace("\"", "\"\"");
                return "\"" + value + "\"";
            }
            return value;
        }
    }

    // =========================================================================
    // 4. MAIN CONTROLLER WITH SIGN UP & AUTHENTICATION
    // =========================================================================

    private DataService dataService = new DataService();
    private Scanner scanner = new Scanner(System.in);
    private User currentUser = null;

    public static void main(String[] args) {
        LabSystemMain app = new LabSystemMain();
        app.start();
    }

    public void start() {
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showRoleMenu();
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("\n=== SCHOOL LABORATORY EQUIPMENT SYSTEM ===");
        System.out.println("1. Log In");
        System.out.println("2. Sign Up (New Borrower Account)");
        System.out.println("3. Exit System");
        System.out.print("Select option (1-3): ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1" -> handleLogin();
            case "2" -> handleSignUp();
            case "3" -> {
                System.out.println("Exiting system. Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Invalid selection. Please try again.");
        }
    }

    private void handleLogin() {
        System.out.println("\n--- USER LOGIN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = dataService.findUserByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            dataService.logActivity(user.getUserId(), "User logged in");
            System.out.println("\nLogin successful! Welcome, " + user.getName() + " (" + user.getRole() + ")");
        } else {
            System.out.println("Error: Invalid username or password.");
        }
    }

    private void handleSignUp() {
        System.out.println("\n--- NEW USER SIGN UP ---");

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Choose a Username: ");
        String username = scanner.nextLine().trim();

        if (dataService.findUserByUsername(username) != null) {
            System.out.println("Error: Username already exists! Please choose another one.");
            return;
        }

        System.out.print("Enter Choose a Password: ");
        String password = scanner.nextLine().trim();

        if (password.length() < 4) {
            System.out.println("Error: Password must be at least 4 characters long.");
            return;
        }

        System.out.print("Enter Department / Course (e.g., Computer Science, Engineering): ");
        String dept = scanner.nextLine().trim();

        // Auto-generate ID for public registration (Role defaults to 'Borrower')
        String userId = "U" + String.format("%03d", dataService.getUsers().size() + 1);
        User newUser = new User(userId, name, username, password, "Borrower", dept);

        dataService.getUsers().add(newUser);
        dataService.logActivity(userId, "Self-registered new Borrower account: " + username);
        dataService.saveData();

        System.out.println("Registration successful! Account ID assigned: " + userId);
        System.out.println("You can now log in with your credentials.");
    }

    private void showRoleMenu() {
        System.out.println("\n=== MAIN MENU [" + currentUser.getRole().toUpperCase() + "] ===");
        switch (currentUser.getRole()) {
            case "Administrator" -> showAdminMenu();
            case "Equipment Staff" -> showStaffMenu();
            case "Borrower" -> showBorrowerMenu();
            case "Department Head" -> showDeptHeadMenu();
            default -> logout();
        }
    }

    private void showAdminMenu() {
        System.out.println("1. Add Equipment Record (Create)");
        System.out.println("2. View Equipment List (Read)");
        System.out.println("3. Remove Equipment Record (Delete)");
        System.out.println("4. Register New User Account (Any Role)");
        System.out.println("5. View Activity Logs");
        System.out.println("6. View & Export Summary Reports");
        System.out.println("7. Log Out");
        System.out.print("Choice: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> addEquipment();
            case "2" -> viewEquipment();
            case "3" -> deleteEquipment();
            case "4" -> registerUserByAdmin();
            case "5" -> viewLogs();
            case "6" -> manageReports();
            case "7" -> logout();
            default -> System.out.println("Invalid option.");
        }
    }

    private void showStaffMenu() {
        System.out.println("1. Register New Borrower Account");
        System.out.println("2. Search & View Equipment");
        System.out.println("3. Update Equipment Condition / Info (Update)");
        System.out.println("4. Issue Approved Equipment to Borrower");
        System.out.println("5. Process Equipment Return & Penalties");
        System.out.println("6. View Activity Logs");
        System.out.println("7. Log Out");
        System.out.print("Choice: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> registerUserByAdmin();
            case "2" -> searchEquipment();
            case "3" -> updateEquipment();
            case "4" -> issueEquipment();
            case "5" -> processReturn();
            case "6" -> viewLogs();
            case "7" -> logout();
            default -> System.out.println("Invalid option.");
        }
    }

    private void showBorrowerMenu() {
        System.out.println("1. View Available Equipment");
        System.out.println("2. Request Equipment to Borrow");
        System.out.println("3. View My Borrowing History");
        System.out.println("4. Log Out");
        System.out.print("Choice: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> viewEquipment();
            case "2" -> requestBorrowing();
            case "3" -> viewMyRecords();
            case "4" -> logout();
            default -> System.out.println("Invalid option.");
        }
    }

    private void showDeptHeadMenu() {
        System.out.println("1. View Pending Department Requests");
        System.out.println("2. Approve / Reject Borrow Request");
        System.out.println("3. Monitor Department Borrowing & Conditions");
        System.out.println("4. Log Out");
        System.out.print("Choice: ");

        switch (scanner.nextLine().trim()) {
            case "1" -> viewPendingRequests();
            case "2" -> approveRejectRequest();
            case "3" -> viewEquipment();
            case "4" -> logout();
            default -> System.out.println("Invalid option.");
        }
    }

    private void addEquipment() {
        System.out.print("Enter Equipment ID (e.g., LAP-01): ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Equipment Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Category (Laptops, Projectors, Microphones, etc.): ");
        String cat = scanner.nextLine().trim();
        System.out.print("Enter Quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        Equipment eq = new Equipment(id, name, cat, qty, "Good");
        dataService.getEquipmentList().add(eq);
        dataService.logActivity(currentUser.getUserId(), "Added equipment: " + id);
        dataService.saveData();
        System.out.println("Equipment added successfully!");
    }

    private void viewEquipment() {
        System.out.println("\n--- EQUIPMENT INVENTORY ---");
        for (Equipment eq : dataService.getEquipmentList()) {
            System.out.println(eq);
        }
    }

    private void searchEquipment() {
        System.out.print("Enter Search Term (Name/Category): ");
        String term = scanner.nextLine().trim().toLowerCase();
        for (Equipment eq : dataService.getEquipmentList()) {
            if (eq.getEquipmentName().toLowerCase().contains(term) || eq.getCategory().toLowerCase().contains(term)) {
                System.out.println(eq);
            }
        }
    }

    private void updateEquipment() {
        System.out.print("Enter Equipment ID to update: ");
        String id = scanner.nextLine().trim();
        Equipment eq = dataService.findEquipmentById(id);
        if (eq != null) {
            System.out.print("Enter New Condition (Good/Minor Damage/Severely Damaged): ");
            String cond = scanner.nextLine().trim();
            eq.setCondition(cond);
            dataService.logActivity(currentUser.getUserId(), "Updated equipment condition for: " + id);
            dataService.saveData();
            System.out.println("Equipment updated successfully!");
        } else {
            System.out.println("Equipment ID not found.");
        }
    }

    private void deleteEquipment() {
        System.out.print("Enter Equipment ID to delete: ");
        String id = scanner.nextLine().trim();
        boolean removed = dataService.getEquipmentList().removeIf(eq -> eq.getEquipmentId().equalsIgnoreCase(id));
        if (removed) {
            dataService.logActivity(currentUser.getUserId(), "Deleted equipment: " + id);
            dataService.saveData();
            System.out.println("Equipment record removed.");
        } else {
            System.out.println("Equipment ID not found.");
        }
    }

    private void registerUserByAdmin() {
        System.out.print("Enter User ID: ");
        String uid = scanner.nextLine().trim();
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Username: ");
        String uname = scanner.nextLine().trim();

        if (dataService.findUserByUsername(uname) != null) {
            System.out.println("Error: Username already exists.");
            return;
        }

        System.out.print("Enter Password: ");
        String pwd = scanner.nextLine().trim();
        System.out.print("Enter Role (Administrator/Equipment Staff/Borrower/Department Head): ");
        String role = scanner.nextLine().trim();
        System.out.print("Enter Department: ");
        String dept = scanner.nextLine().trim();

        dataService.getUsers().add(new User(uid, name, uname, pwd, role, dept));
        dataService.logActivity(currentUser.getUserId(), "Admin registered user: " + uid);
        dataService.saveData();
        System.out.println("User account created successfully!");
    }

    private void requestBorrowing() {
        viewEquipment();
        System.out.print("Enter Equipment ID to borrow: ");
        String eqId = scanner.nextLine().trim();
        System.out.print("Enter Quantity: ");
        int qty = Integer.parseInt(scanner.nextLine().trim());

        String bId = "B" + (dataService.getBorrowingRecords().size() + 101);
        String today = LocalDate.now().toString();
        String due = LocalDate.now().plusDays(3).toString();

        BorrowingRecord record = new BorrowingRecord(bId, currentUser.getUserId(), eqId, qty, today, due);
        dataService.getBorrowingRecords().add(record);
        dataService.logActivity(currentUser.getUserId(), "Requested borrowing for: " + eqId);
        dataService.saveData();
        System.out.println("Request submitted successfully! Awaiting Department Head Approval.");
    }

    private void viewPendingRequests() {
        System.out.println("\n--- PENDING BORROW REQUESTS ---");
        for (BorrowingRecord rec : dataService.getBorrowingRecords()) {
            if (rec.getBorrowStatus().equalsIgnoreCase("Pending Approval")) {
                System.out.println(rec);
            }
        }
    }

    private void approveRejectRequest() {
        viewPendingRequests();
        System.out.print("Enter Borrow ID to process: ");
        String bId = scanner.nextLine().trim();
        System.out.print("Decision (1: Approve, 2: Reject): ");
        String dec = scanner.nextLine().trim();

        BorrowingRecord rec = dataService.findBorrowingRecordById(bId);
        if (rec != null) {
            if (dec.equals("1")) {
                rec.setBorrowStatus("Approved");
                dataService.logActivity(currentUser.getUserId(), "Approved borrow request: " + bId);
            } else {
                rec.setBorrowStatus("Rejected");
                dataService.logActivity(currentUser.getUserId(), "Rejected borrow request: " + bId);
            }
            dataService.saveData();
            System.out.println("Decision recorded!");
        } else {
            System.out.println("Record ID not found.");
        }
    }

    private void issueEquipment() {
        System.out.print("Enter Borrow ID to issue: ");
        String bId = scanner.nextLine().trim();
        BorrowingRecord rec = dataService.findBorrowingRecordById(bId);
        if (rec != null && rec.getBorrowStatus().equalsIgnoreCase("Approved")) {
            Equipment eq = dataService.findEquipmentById(rec.getEquipmentId());
            if (eq != null && eq.allocateQuantity(rec.getQuantity())) {
                rec.setBorrowStatus("Borrowed");
                dataService.logActivity(currentUser.getUserId(), "Issued equipment for borrow ID: " + bId);
                dataService.saveData();
                System.out.println("Equipment issued to borrower successfully.");
            } else {
                System.out.println("Insufficient available stock to issue.");
            }
        } else {
            System.out.println("Valid approved request not found.");
        }
    }

    private void processReturn() {
        System.out.print("Enter Borrow ID to return: ");
        String bId = scanner.nextLine().trim();
        BorrowingRecord rec = dataService.findBorrowingRecordById(bId);
        if (rec != null && rec.getBorrowStatus().equalsIgnoreCase("Borrowed")) {
            System.out.print("Enter Equipment Condition on Return (Good/Damaged): ");
            String cond = scanner.nextLine().trim();
            System.out.print("Enter Fine/Penalty Amount (0 if none): ");
            double fine = Double.parseDouble(scanner.nextLine().trim());

            rec.setReturnDate(LocalDate.now().toString());
            rec.setReturnCondition(cond);
            rec.setFineOrPenalty(fine);
            rec.setBorrowStatus("Returned");

            Equipment eq = dataService.findEquipmentById(rec.getEquipmentId());
            if (eq != null) {
                eq.releaseQuantity(rec.getQuantity());
                if (cond.equalsIgnoreCase("Damaged")) {
                    eq.setCondition("Minor Damage");
                }
            }

            dataService.logActivity(currentUser.getUserId(), "Processed return for borrow ID: " + bId + " with fine $" + fine);
            dataService.saveData();
            System.out.println("Return processed successfully.");
        } else {
            System.out.println("Active borrowed record not found.");
        }
    }

    private void viewMyRecords() {
        for (BorrowingRecord rec : dataService.getBorrowingRecords()) {
            if (rec.getUserId().equalsIgnoreCase(currentUser.getUserId())) {
                System.out.println(rec);
            }
        }
    }

    private void viewLogs() {
        System.out.println("\n--- SYSTEM ACTIVITY LOGS ---");
        for (ActivityLog log : dataService.getActivityLogs()) {
            System.out.println(log);
        }
    }

    private void manageReports() {
        ReportService.printSummaryReport(dataService);
        System.out.print("Export to CSV file? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            ReportService.generateCsvReport(dataService, "Equipment_Borrowing_Summary_Report.csv");
        }
    }

    private void logout() {
        if (currentUser != null) {
            dataService.logActivity(currentUser.getUserId(), "User logged out");
            currentUser = null;
        }
        System.out.println("Logged out successfully.");
    }
}