package com.example.agenda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona las altas, bajas y modificaciones de una agenda.
 */
public class AgendaDB {
    private IPersonData personData;
    private IPhoneNumbersData phoneData;
    private IAddressesData addressData;
    private ObservableList<Person> people;

    public AgendaDB(IPersonData personData, IPhoneNumbersData phoneData, IAddressesData addressData) {
        this.personData = personData;
        this.phoneData = phoneData;
        this.addressData = addressData;
        getData();
    }

    /**
     * Método que obtiene toda la información de la agenda y la almacena en una lista.
     */
    public void getData() {
        List<Person> list = personData.getAll();
        people = FXCollections.observableArrayList(list);;
    }

    /**
     * Método que inserta una persona en la agenda..
     * @param name El nombre ha registrar.
     * @param address La dirección de la persona a registrar.
     * @param phone El número de teléfono de la persona a registrar.
     */
    public void insertPerson(String name, String address, String phone) {
        int personId = personData.insertPerson(name);
        List<String> addresses = new ArrayList<>();
        addresses.add(address);
        addressData.insertAddress(personId, addresses);
        List<String> phones = new ArrayList<>();
        phones.add(phone);
        phoneData.insertPhoneNumber(personId, phones);
    }

    /**
     * Método que elimina a una persona de la agenda de acuerdo a su id.
     * @param id El número de identificación de la persona a eliminar.
     */
    public void deletePerson(int id) {
        phoneData.deletePhonesByPerson(id);
        addressData.deletePersonAddresses(id);
        personData.deletePerson(id);
        addressData.deleteUnusedAddresses();
    }

    /**
     * Método que actualiza el nombre de la persona seleccionada.
     * @param id El id de la persona seleccionada.
     * @param name El nombre de la persona seleccionada.
     */
    public void updatePersonName(int id, String name) {
        personData.updatePersonName(id, name);
    }

    /**
     * Método que actualiza la información de la persona seleccionada.
     * @param id El id de la persona seleccionada.
     * @param name El nombre de la persona seleccionada.
     * @param addresses Las direcciones de la persona.
     * @param phones Los números de la persona.
     */
    public void updatePerson(int id, String name, List<String> addresses, List<String> phones) {
        personData.updatePersonName(id, name);
        addressData.deletePersonAddresses(id);
        addressData.insertAddress(id, addresses);
        phoneData.deletePhonesByPerson(id);
        phoneData.insertPhoneNumber(id, phones);
    }

    /**
     * Método que agrega un nuevo teléfono de una persona.
     * @param id El id de la persona.
     * @param phone El número de teléfono a registrar.
     */
    public void addPhone(int id, String phone) {
        List<String> phones = new ArrayList<>();
        phones.add(phone);
        phoneData.insertPhoneNumber(id, phones);
    }

    /**
     * Método que agrega una nueva dirección de una persona.
     * @param id El id de la persona.
     * @param address La dirección a registrar.
     */
    public void addAddress(int id, String address) {
        List<String> addresses = new ArrayList<>();
        addresses.add(address);
        addressData.insertAddress(id, addresses);
    }

    public ObservableList<Person> getPeople() {
        return people;
    }

    /**
     * Método que regresa las direcciones de la persona de acuerdo a su id.
     * @param id El id de la persona.
     * @return Una lista con todas las direcciones.
     */
    public List<String> getAddressesByPerson(int id) {
        List<String> addresses = new ArrayList<>();
        addresses = addressData.getAddressesByPerson(id);
        return addresses;
    }

    /**
     * Método que regresa los números de la persona de acuerdo a su id.
     * @param id El id de la persona.
     * @return Una lista con todos los números.
     */
    public List<String> getPhonesByPerson(int id) {
        List<String> phones = new ArrayList<>();
        phones = phoneData.getPhonesByPerson(id);
        return phones;
    }
}