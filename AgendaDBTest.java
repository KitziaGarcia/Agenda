package com.example.agenda;
import org.junit.jupiter.api.*;
import javafx.collections.ObservableList;

import static org.junit.jupiter.api.Assertions.*;

class AgendaDBTest {
    static AgendaDB agenda;

    @BeforeAll
    static void setUp() {
        agenda = new AgendaDB();
    }

    @Test
    void testInsertAndGetPerson() {
        agenda.insertPerson("John Doe", "Calle Falsa 123", "555-7654321");
        agenda.getData();
        ObservableList<Person> people = agenda.getPeople();
        boolean found = people.stream().anyMatch(p -> p.getName().equals("John Doe") && p.getAddress().equals("Calle Falsa 123"));
        assertTrue(found);
    }

    @Test
    void testDeletePerson() {
        agenda.insertPerson("Eliminar", "Direccion", "9999");
        agenda.getData();
        Person inserted = agenda.getPeople().stream().filter(p -> p.getName().equals("Eliminar")).findFirst().orElse(null);
        assertNotNull(inserted);

        agenda.deletePerson(inserted.getId());
        agenda = new AgendaDB();
        boolean stillExists = agenda.getPeople().stream().anyMatch(p -> p.getId() == inserted.getId());
        assertFalse(stillExists);
    }
}
