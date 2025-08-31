package com.example.agenda;

public class Person {
    private int id;
    private String name;
    private String addresses;
    private String phoneNumbers;

    public Person(int id, String name, String addresses, String phoneNumbers) {
        this.id = id;
        this.name = name;
        this.addresses = addresses;
        this.phoneNumbers = phoneNumbers;
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

    public String getAddresses() {
        return addresses;
    }

    public String getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public void setPhoneNumbers(String phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
}