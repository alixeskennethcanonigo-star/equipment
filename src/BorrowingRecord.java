package com.lab.system.model;

import java.io.Serializable;

public class BorrowingRecord implements Serializable {
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

    // Getters
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

    // Setters
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