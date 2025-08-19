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
        System.out.println(people);
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
                String address = rs.getString("direccion");
                Statement stmtTelefonos = conn.createStatement();
                ResultSet rsTelefonos = stmtTelefonos.executeQuery(
                        "SELECT telefono FROM Telefonos WHERE personaId = " + id);

                StringBuilder phoneNumbers = new StringBuilder();
                while (rsTelefonos.next()) {
                    if (phoneNumbers.length() > 0) phoneNumbers.append(", ");
                    phoneNumbers.append(rsTelefonos.getString("telefono"));
                }

                rsTelefonos.close();
                stmtTelefonos.close();

                people.add(new Person(id, name, address, phoneNumbers.toString()));
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
        String person = "INSERT INTO Personas (nombre, direccion) VALUES (?, ?)";
        String phoneNumber = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement psPerson = null;
        PreparedStatement psPhone = null;
        ResultSet keys = null;

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            psPerson = conn.prepareStatement(person, Statement.RETURN_GENERATED_KEYS);
            psPerson.setString(1, name);
            psPerson.setString(2, address);
            psPerson.executeUpdate();

            keys = psPerson.getGeneratedKeys();
            int personId = -1;
            if (keys.next()) {
                personId = keys.getInt(1);
            }

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
        String person = "DELETE FROM Personas WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement psPhone = conn.prepareStatement(phone);
             PreparedStatement psPerson = conn.prepareStatement(person)) {

            psPhone.setInt(1, id);
            psPhone.executeUpdate();
            psPerson.setInt(1, id);
            psPerson.executeUpdate();

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
        String sqlPerson = "UPDATE Personas SET nombre = ?, direccion = ? WHERE id = ?";
        String sqlPhone  = "UPDATE Telefonos SET telefono = ? WHERE personaId = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmtPerson = conn.prepareStatement(sqlPerson);
             PreparedStatement stmtPhone = conn.prepareStatement(sqlPhone)) {
            stmtPerson.setString(1, name);
            stmtPerson.setString(2, address);
            stmtPerson.setInt(3, id);
            stmtPerson.executeUpdate();

            stmtPhone.setString(1, phone);
            stmtPhone.setInt(2, id);
            stmtPhone.executeUpdate();
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


}