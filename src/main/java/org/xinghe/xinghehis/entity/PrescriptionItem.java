package org.xinghe.xinghehis.entity;

import java.math.BigDecimal;

public class PrescriptionItem {
    private Long id;
    private Long prescriptionId;
    private Long medicineId;
    private String dosage;
    private Integer quantity;
    private BigDecimal price;

    private String medicineName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }
    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
}
