package com.example.agenda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase que gestiona las altas, bajas y modificaciones de una agenda.
 */
public class AgendaDB {
    private PersonData personData = new PersonData();
    private PhoneNumbersData phoneNumbersData = new PhoneNumbersData();
    private AddressesData addressesData = new AddressesData();
    private ObservableList<Person> people;

    public AgendaDB() {
        getData();
    }

    /**
     * Método que obtiene toda la información de la base de datos y la almacena en una lista.
     */
    public void getData() {
        Statement stmt = null;
        ResultSet rs = null;
        people = FXCollections.observableArrayList();

        try {
            Connection conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Personas");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nombre");

                Statement stmtTelefonos = conn.createStatement();
                ResultSet rsTelefonos = stmtTelefonos.executeQuery(
                        "SELECT telefono FROM Telefonos WHERE personaId = " + id);

                StringBuilder phoneNumbers = new StringBuilder();
                while (rsTelefonos.next()) {
                    if (phoneNumbers.length() > 0) phoneNumbers.append("\n");
                    phoneNumbers.append(rsTelefonos.getString("telefono"));
                }

                rsTelefonos.close();
                stmtTelefonos.close();

                Statement stmtAddresses = conn.createStatement();
                ResultSet rsAddresses = stmtAddresses.executeQuery(
                        "SELECT d.direccion FROM Direcciones d JOIN Personas_Direcciones pd " +
                                "ON d.id_direccion = pd.id_direccion WHERE pd.id_persona = " + id
                );

                StringBuilder addresses = new StringBuilder();
                while (rsAddresses.next()) {
                    if (addresses.length() > 0) addresses.append("\n");
                    addresses.append(rsAddresses.getString("direccion"));
                }
                rsAddresses.close();
                stmtAddresses.close();
                people.add(new Person(id, name, addresses.toString(), phoneNumbers.toString()));
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /**
     * Método que inserta una persona en la base de datos.
     * @param name El nombre ha registrar.
     * @param address La dirección de la persona a registrar.
     * @param phone El número de teléfono de la persona a registrar.
     */
    public void insertPerson(String name, String address, String phone) {
        int personId = personData.insertPerson(name);
        addressesData.insertAddress(personId, address);
        phoneNumbersData.insertPhoneNumber(personId, phone);
    }

    /**
     * Método que elimina a una persona de la base de datos de acuerdo a su id.
     * @param //id El número de identificación de la persona a eliminar.
     */
//    public void deletePerson(int id) {
//        String phone = "DELETE FROM Telefonos WHERE personaId = ?";
//        String personAddresses = "DELETE FROM Personas_Direcciones WHERE id_persona = ?";
//        String person = "DELETE FROM Personas WHERE id = ?";
//
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement psPhone = conn.prepareStatement(phone);
//             PreparedStatement psPersonAddress = conn.prepareStatement(personAddresses);
//             PreparedStatement psPerson = conn.prepareStatement(person)) {
//
//            psPhone.setInt(1, id);
//            psPhone.executeUpdate();
//            psPersonAddress.setInt(1, id);
//            psPersonAddress.executeUpdate();
//            psPerson.setInt(1, id);
//            psPerson.executeUpdate();
//
//            String deleteUnusedAddresses = "DELETE FROM Direcciones WHERE id_direccion NOT IN (SELECT id_direccion FROM Personas_Direcciones)";
//            try (PreparedStatement psDeleteUnused = conn.prepareStatement(deleteUnusedAddresses)) {
//                psDeleteUnused.executeUpdate();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Método que actualiza la información de la persona seleccionada.
//     * @param id El id de la persona seleccionada.
//     * @param name El nombre de la persona seleccionada.
//     * @param addresses Las direcciones de la persona.
//     * @param phones Los números de la persona.
//     */
//    public void updatePerson(int id, String name, List<String> addresses, List<String> phones) {
//
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
//            String sqlPerson = "UPDATE Personas SET nombre = ? WHERE id = ?";
//            try (PreparedStatement stmtPerson = conn.prepareStatement(sqlPerson)) {
//                stmtPerson.setString(1, name);
//                stmtPerson.setInt(2, id);
//                stmtPerson.executeUpdate();
//            }
//
//            String deletePersonAddresses = "DELETE FROM Personas_Direcciones WHERE id_persona = ?";
//            try (PreparedStatement stmtDel = conn.prepareStatement(deletePersonAddresses)) {
//                stmtDel.setInt(1, id);
//                stmtDel.executeUpdate();
//            }
//
//            for (String address : addresses) {
//                int addressId = -1;
//                String findAddress = "SELECT id_direccion FROM Direcciones WHERE direccion = ?";
//                try (PreparedStatement stmt = conn.prepareStatement(findAddress)) {
//                    stmt.setString(1, address);
//                    ResultSet rs = stmt.executeQuery();
//                    if (rs.next()) {
//                        addressId = rs.getInt("id_direccion");
//                    } else {
//                        String insertAddress = "INSERT INTO Direcciones (direccion) VALUES (?)";
//                        try (PreparedStatement stmtInsert = conn.prepareStatement(insertAddress, Statement.RETURN_GENERATED_KEYS)) {
//                            stmtInsert.setString(1, address);
//                            stmtInsert.executeUpdate();
//                            ResultSet keys = stmtInsert.getGeneratedKeys();
//                            if (keys.next()) {
//                                addressId = keys.getInt(1);
//                            }
//                            keys.close();
//                        }
//                    }
//                    rs.close();
//                }
//
//                String insertRelation = "INSERT INTO Personas_Direcciones (id_persona, id_direccion) VALUES (?, ?)";
//                try (PreparedStatement stmtRel = conn.prepareStatement(insertRelation)) {
//                    stmtRel.setInt(1, id);
//                    stmtRel.setInt(2, addressId);
//                    stmtRel.executeUpdate();
//                }
//            }
//
//            String deletePhones = "DELETE FROM Telefonos WHERE personaId = ?";
//            try (PreparedStatement stmtDel = conn.prepareStatement(deletePhones)) {
//                stmtDel.setInt(1, id);
//                stmtDel.executeUpdate();
//            }
//
//            for (String phone : phones) {
//                String insertPhone = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
//                try (PreparedStatement stmtPhone = conn.prepareStatement(insertPhone)) {
//                    stmtPhone.setInt(1, id);
//                    stmtPhone.setString(2, phone);
//                    stmtPhone.executeUpdate();
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Método que agrega un nuevo teléfono de una persona.
//     * @param personId El id de la persona.
//     * @param phone El número de teléfono a registrar.
//     */
//    public void addPhone(int personId, String phone) {
//        String sql = "INSERT INTO Telefonos(personaId, telefono) VALUES (?, ?)";
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setInt(1, personId);
//            stmt.setString(2, phone);
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void addAddress(int personId, String address) {
//        int addressId = -1;
//        String sqlAddress = "INSERT INTO Direcciones(direccion) VALUES (?)";
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sqlAddress, Statement.RETURN_GENERATED_KEYS)) {
//            stmt.setString(1, address);
//            stmt.executeUpdate();
//
//            ResultSet keys = stmt.getGeneratedKeys();
//            if (keys.next()) {
//                addressId = keys.getInt(1);
//            }
//            keys.close();
//
//            String sqlPersonAddresses = "INSERT INTO Personas_Direcciones (id_persona, id_direccion) VALUES (?, ?)";
//            try (PreparedStatement stmtPersonAddresses = conn.prepareStatement(sqlPersonAddresses)) {
//                stmtPersonAddresses.setInt(1, personId);
//                stmtPersonAddresses.setInt(2, addressId);
//                stmtPersonAddresses.executeUpdate();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
    public ObservableList<Person> getPeople() {
        return people;
    }
//
//    /**
//     * Método que regresa las direcciones de la persona de acuerdo a su id.
//     * @param personId El id de la persona.
//     * @return Una lista con todas las direcciones.
//     */
//    public List<String> getAddressesByPerson(int personId) {
//        List<String> addresses = new ArrayList<>();
//        String sql = "SELECT d.direccion FROM Direcciones d JOIN Personas_Direcciones pd ON d.id_direccion = pd.id_direccion WHERE pd.id_persona = ?";
//
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, personId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                addresses.add(rs.getString("direccion"));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return addresses;
//    }
//
//    /**
//     * Método que regresa los números de la persona de acuerdo a su id.
//     * @param personId El id de la persona.
//     * @return Una lista con todos los números.
//     */
//    public List<String> getPhonesByPerson(int personId) {
//        List<String> phones = new ArrayList<>();
//        String sql = "SELECT telefono FROM telefonos WHERE personaId = ?";
//
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setInt(1, personId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                phones.add(rs.getString("telefono"));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return phones;
//    }
}