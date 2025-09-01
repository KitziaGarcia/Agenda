package com.example.agenda;
import java.util.List;

public interface IAddressesData {
    void insertAddress(int id, List<String> addresses);
    void deletePersonAddresses(int id);
    void deleteUnusedAddresses();
    List<String> getAddressesByPerson(int id);
}