package com.lab.system.model;

import java.io.Serializable;

public class Equipment implements Serializable {
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

    // Getters
    public String getEquipmentId() { return equipmentId; }
    public String getEquipmentName() { return equipmentName; }
    public String getCategory() { return category; }
    public int getQuantity() { return quantity; }
    public int getAvailableQuantity() { return availableQuantity; }
    public String getCondition() { return condition; }
    public String getStatus() { return status; }

    // Setters
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }
    public void setCategory(String category) { this.category = category; }

    public void setQuantity(int quantity) {
        int diff = quantity - this.quantity;
        this.quantity = quantity;
        this.availableQuantity = Math.max(0, this.availableQuantity + diff);
    }

    public void setCondition(String condition) { this.condition = condition; }
    public void setStatus(String status) { this.status = status; }

    // Business Logic Methods
    public boolean allocateQuantity(int qty) {
        if (qty > 0 && qty <= availableQuantity) {
            availableQuantity -= qty;
            if (availableQuantity == 0) {
                this.status = "In-Use";
            }
            return true;
        }
        return false;
    }

    public void releaseQuantity(int qty) {
        if (qty > 0) {
            availableQuantity = Math.min(quantity, availableQuantity + qty);
            if (availableQuantity > 0) {
                this.status = "Available";
            }
        }
    }

    @Override
    public String toString() {
        return String.format("ID: %-8s | Name: %-18s | Category: %-12s | Stock: %d/%d | Condition: %-12s | Status: %s",
                equipmentId, equipmentName, category, availableQuantity, quantity, condition, status);
    }
}