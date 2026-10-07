package com.lab.system.service;

import com.lab.system.model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReportService {

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
                        escapeCsv(eq.getEquipmentId()),
                        escapeCsv(eq.getEquipmentName()),
                        escapeCsv(eq.getCategory()),
                        eq.getQuantity(),
                        eq.getAvailableQuantity(),
                        escapeCsv(eq.getCondition()),
                        escapeCsv(eq.getStatus()));
            }

            writer.println();
            writer.println("Borrowing Records");
            writer.println("Borrowing ID,User ID,Equipment ID,Quantity,Borrow Date,Due Date,Return Date,Status,Fine");
            for (BorrowingRecord rec : records) {
                writer.printf("%s,%s,%s,%d,%s,%s,%s,%s,%.2f\n",
                        escapeCsv(rec.getBorrowingId()),
                        escapeCsv(rec.getUserId()),
                        escapeCsv(rec.getEquipmentId()),
                        rec.getQuantity(),
                        rec.getBorrowDate(),
                        rec.getDueDate(),
                        rec.getReturnDate(),
                        escapeCsv(rec.getBorrowStatus()),
                        rec.getFineOrPenalty());
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

        long borrowedCount = records.stream()
                .filter(r -> r.getBorrowStatus().equalsIgnoreCase("Borrowed"))
                .count();

        long returnedCount = records.stream()
                .filter(r -> r.getBorrowStatus().equalsIgnoreCase("Returned"))
                .count();

        long damagedCount = equipment.stream()
                .filter(e -> e.getCondition() != null && e.getCondition().toLowerCase().contains("damage"))
                .count();

        // Dynamically counts marked overdue records OR active borrowed items past due date
        LocalDate today = LocalDate.now();
        long overdueCount = records.stream()
                .filter(r -> isRecordOverdue(r, today))
                .count();

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
        if ("Overdue".equalsIgnoreCase(record.getBorrowStatus())) {
            return true;
        }
        if ("Borrowed".equalsIgnoreCase(record.getBorrowStatus())) {
            try {
                LocalDate dueDate = LocalDate.parse(record.getDueDate());
                return today.isAfter(dueDate);
            } catch (DateTimeParseException | NullPointerException ignored) {
                // Fallback if due date string format is unparseable
            }
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