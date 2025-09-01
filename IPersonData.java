package com.example.agenda;
import java.util.List;

public interface IPersonData {
    int insertPerson(String name);
    void deletePerson(int id);
    void updatePersonName(int id, String name);
    List<Person> getAll();
}