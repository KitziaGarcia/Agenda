package com.example.agenda;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class AgendaDB {
    // Datos de conexión a la base de datos
    private static final String URL = "jdbc:mariadb://localhost:3307/agenda";
    private static final String USER = "usuario1";
    private static final String PASSWORD = "superpassword";
    private ObservableList<Person> people;

    public AgendaDB() {
        getData();
        System.out.println(people);
    }

    public static void main(String[] args) {
        AgendaDB a = new AgendaDB();
    }

    private void getData() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        people = FXCollections.observableArrayList();

        try {
            // 1. Registrar el driver JDBC
            Class.forName("org.mariadb.jdbc.Driver");

            // 2. Establecer la conexión
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // 3. Consultar la tabla Personas
            System.out.println("\n=== LISTADO DE PERSONAS ===");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM Personas");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nombre");
                String address = rs.getString("direccion");

                // 4. Consultar los teléfonos de cada persona
                Statement stmtTelefonos = conn.createStatement();
                ResultSet rsTelefonos = stmtTelefonos.executeQuery(
                        "SELECT telefono FROM Telefonos WHERE personaId = " + id);

                StringBuilder phoneNumbers = new StringBuilder();
                while (rsTelefonos.next()) {
                    if (phoneNumbers.length() > 0) phoneNumbers.append(", "); // separa con coma
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
            // 5. Cerrar recursos
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

    public void updatePerson(int id, String name, String address) {
        String sql = "UPDATE Personas SET nombre = ?, direccion = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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