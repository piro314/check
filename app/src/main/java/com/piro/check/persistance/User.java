package com.piro.check.persistance;

/**
 * Created by piro on 10.10.2016 г..
 */

public class User implements Comparable<User>{

    private int id;
    private String name;
    private Double balance;


    public User() {
    }

    public User(int id, String name, Double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public User(String name, Double balance) {
        this.name = name;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(! (obj instanceof User)){
            return false;
        }
        return ((User) obj).getName().equals(this.getName());
}

    @Override
    public int compareTo(User o) {
        if(Math.abs(this.balance) < 0.01 && Math.abs(o.balance) >= 0.01){
            return -1;
        }
        return  this.balance > o.balance ? 1 :
                this.balance < o.balance ? -1 : 0;

    }
}
