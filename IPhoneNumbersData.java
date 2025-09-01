package com.example.agenda;
import java.util.List;

public interface IPhoneNumbersData {
    void insertPhoneNumber(int id, List<String> phones);
    void deletePhonesByPerson(int id);
    List<String> getPhonesByPerson(int id);
}