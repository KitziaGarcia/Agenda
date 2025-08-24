package com.example.agenda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

/**
 * Clase que gestiona las altas, bajas y modificaciones de una agenda.
 */
public class AgendaDB {
    private static final String URL = "jdbc:mariadb://localhost:3307/agenda";
    private static final String USER = "usuario1";
    private static final String PASSWORD = "superpassword";
    private ObservableList<Person> people;

    public AgendaDB() {
        getData();
        System.out.println("PEOPLE: \n" + people);
    }

    /**
     * Método que obtiene toda la información de la base de datos y la almacena en una lista.
     */
    public void getData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        people = FXCollections.observableArrayList();

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
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
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public ObservableList<Person> getPeople() {
        return people;
    }

    /**
     * Método que inserta una persona en la base de datos.
     * @param name El nombre ha registrar.
     * @param address La dirección de la persona a registrar.
     * @param phone El número de teléfono de la persona a registrar.
     */
    public void insertPerson(String name, String address, String phone) {
        Connection conn = null;
        PreparedStatement psPerson = null;
        PreparedStatement psAddress = null;
        PreparedStatement psPersonAddress = null;
        PreparedStatement psPhone = null;
        ResultSet keys = null;
        ResultSet rsAddress = null;

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String person = "INSERT INTO Personas (nombre) VALUES (?)";
            psPerson = conn.prepareStatement(person, Statement.RETURN_GENERATED_KEYS);
            psPerson.setString(1, name);
            psPerson.executeUpdate();

            keys = psPerson.getGeneratedKeys();
            int personId = -1;
            if (keys.next()) {
                personId = keys.getInt(1);
            }

            String findAddress = "SELECT id_direccion FROM Direcciones WHERE direccion = ?";
            psAddress = conn.prepareStatement(findAddress);
            psAddress.setString(1, address);
            rsAddress = psAddress.executeQuery();

            int addressId = -1;
            if (rsAddress.next()) {
                addressId = rsAddress.getInt("id_direccion");
            } else {
                String insertAddress = "INSERT INTO Direcciones (direccion) VALUES (?)";
                psAddress = conn.prepareStatement(insertAddress, Statement.RETURN_GENERATED_KEYS);
                psAddress.setString(1, address);
                psAddress.executeUpdate();
                ResultSet addressKey = psAddress.getGeneratedKeys();
                if (addressKey.next()) {
                    addressId = addressKey.getInt(1);
                }
                addressKey.close();
            }

            rsAddress.close();

            String sqlPersonAddress = "INSERT INTO Personas_Direcciones (id_persona, id_direccion) VALUES (?, ?)";
            psPersonAddress = conn.prepareStatement(sqlPersonAddress);
            psPersonAddress.setInt(1, personId);
            psPersonAddress.setInt(2, addressId);
            psPersonAddress.executeUpdate();

            String phoneNumber = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
            psPhone = conn.prepareStatement(phoneNumber);
            psPhone.setInt(1, personId);
            psPhone.setString(2, phone);
            psPhone.executeUpdate();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (keys != null) keys.close();
                if (psPerson != null) psPerson.close();
                if (psAddress != null) psAddress.close();
                if (psPersonAddress != null) psPersonAddress.close();
                if (psPhone != null) psPhone.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /**
     * Método que elimina a una persona de la base de datos de acuerdo a su id.
     * @param id El número de identificación de la persona a eliminar.
     */
    public void deletePerson(int id) {
        String phone = "DELETE FROM Telefonos WHERE personaId = ?";
        String personAddresses = "DELETE FROM Personas_Direcciones WHERE id_persona = ?";
        String person = "DELETE FROM Personas WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement psPhone = conn.prepareStatement(phone);
             PreparedStatement psPersonAddress = conn.prepareStatement(personAddresses);
             PreparedStatement psPerson = conn.prepareStatement(person)) {

            psPhone.setInt(1, id);
            psPhone.executeUpdate();
            psPersonAddress.setInt(1, id);
            psPersonAddress.executeUpdate();
            psPerson.setInt(1, id);
            psPerson.executeUpdate();

            String deleteUnusedAddresses = "DELETE FROM Direcciones WHERE id_direccion NOT IN (SELECT id_direccion FROM Personas_Direcciones)";
            try (PreparedStatement psDeleteUnused = conn.prepareStatement(deleteUnusedAddresses)) {
                psDeleteUnused.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que actualiza la información de la persona seleccionada.
     * @param id El nuevo id a registrar.
     * @param name El nuevo nombre a registrar.
     * @param address La nueva dirección a registrar.
     * @param phone El nuevo teléfono a registrar.
     */
    public void updatePerson(int id, String name, String address, String phone) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sqlPerson = "UPDATE Personas SET nombre = ? WHERE id = ?";
            try (PreparedStatement stmtPerson = conn.prepareStatement(sqlPerson)) {
                stmtPerson.setString(1, name);
                stmtPerson.setInt(2, id);
                stmtPerson.executeUpdate();
            }

            String sqlPhone  = "UPDATE Telefonos SET telefono = ? WHERE personaId = ?";
            try (PreparedStatement stmtPhone = conn.prepareStatement(sqlPhone)) {
                stmtPhone.setString(1, phone);
                stmtPhone.setInt(2, id);
                stmtPhone.executeUpdate();
            }

            int addressId = -1;
            String findAddress = "SELECT id_direccion FROM Direcciones WHERE direccion = ?";
            try (PreparedStatement stmtAddress = conn.prepareStatement(findAddress)) {
                stmtAddress.setString(1, address);
                ResultSet rs = stmtAddress.executeQuery();

                if (rs.next()) {
                    addressId = rs.getInt("id_direccion");
                } else {
                    String insertAddress = "INSERT INTO Direcciones (direccion) VALUES (?)";
                    try (PreparedStatement stmtInsert = conn.prepareStatement(insertAddress, Statement.RETURN_GENERATED_KEYS)) {
                        stmtInsert.setString(1, address);
                        stmtInsert.executeUpdate();
                        ResultSet keys = stmtInsert.getGeneratedKeys();
                        if (keys.next()) {
                            addressId = keys.getInt(1);
                        }
                        keys.close();
                    }
                }
                rs.close();
            }

            String deletePersonAddresses = "DELETE FROM Personas_Direcciones WHERE id_persona = ?";
            try (PreparedStatement stmtDelete = conn.prepareStatement(deletePersonAddresses)) {
                stmtDelete.setInt(1, id);
                stmtDelete.executeUpdate();
            }

            String updatePersonAddresses = "INSERT INTO Personas_Direcciones (id_persona, id_direccion) VALUES (?, ?)";
            try (PreparedStatement stmtUpdate = conn.prepareStatement(updatePersonAddresses)) {
                stmtUpdate.setInt(1, id);
                stmtUpdate.setInt(2, addressId);
                stmtUpdate.executeUpdate();
            }

            String deleteUnusedAddresses = "DELETE FROM Direcciones WHERE id_direccion NOT IN (SELECT id_direccion FROM Personas_Direcciones)";
            try (PreparedStatement psDeleteUnused = conn.prepareStatement(deleteUnusedAddresses)) {
                psDeleteUnused.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que agrega un nuevo teléfono de una persona.
     * @param personId El id de la persona.
     * @param phone El número de teléfono a registrar.
     */
    public void addPhone(int personId, String phone) {
        String sql = "INSERT INTO Telefonos(personaId, telefono) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personId);
            stmt.setString(2, phone);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAddress(int personId, String address) {
        int addressId = -1;
        String sqlAddress = "INSERT INTO Direcciones(direccion) VALUES (?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sqlAddress, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, address);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                addressId = keys.getInt(1);
            }
            keys.close();

            String sqlPersonAddresses = "INSERT INTO Personas_Direcciones (id_persona, id_direccion) VALUES (?, ?)";
            try (PreparedStatement stmtPersonAddresses = conn.prepareStatement(sqlPersonAddresses)) {
                stmtPersonAddresses.setInt(1, personId);
                stmtPersonAddresses.setInt(2, addressId);
                stmtPersonAddresses.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}