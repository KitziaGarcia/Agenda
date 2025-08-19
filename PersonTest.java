package com.example.agenda;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void testCreatePerson() {
        Person person = new Person(1, "Kitzia", "Mexicali", "1234");

        assertEquals(1, person.getId());
        assertEquals("Kitzia", person.getName());
        assertEquals("Mexicali", person.getAddress());
        assertEquals("1234", person.getPhoneNumbers());
    }

    @Test
    void testSetters() {
        Person person = new Person(1, "Kitzia", "Mexicali", "1234");
        person.setName("Daniela");
        person.setAddress("MXL");
        person.setPhoneNumbers("6789");

        assertEquals("Daniela", person.getName());
        assertEquals("MXL", person.getAddress());
        assertEquals("6789", person.getPhoneNumbers());
    }
}
