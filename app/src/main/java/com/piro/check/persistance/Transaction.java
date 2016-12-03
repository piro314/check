package com.piro.check.persistance;

import java.util.Date;

/**
 * Created by piro on 17.10.2016 г..
 */

public class Transaction {

    private int id;
    private String giverName;
    private Double amount;
    private String receiverNames;
    private String description;
    private Date date;

    public Transaction() {
    }

    public Transaction(String giverName, Double amount, String receiverNames, String description) {
        this.giverName = giverName;
        this.amount = amount;
        this.receiverNames = receiverNames;
        this.description = description;
        this.date = new Date();

    }

    public Transaction(int id, String giverName, Double amount, String receiverNames, String description, Date date) {
        this.id = id;
        this.giverName = giverName;
        this.amount = amount;
        this.receiverNames = receiverNames;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGiverName() {
        return giverName;
    }

    public void setGiverName(String giverName) {
        this.giverName = giverName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReceiverNames() {
        return receiverNames;
    }

    public void setReceiverNames(String receiverNames) {
        this.receiverNames = receiverNames;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", giverName='" + giverName + '\'' +
                ", amount=" + amount +
                ", receiverNames='" + receiverNames + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }
}
