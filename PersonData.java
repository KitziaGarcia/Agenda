package com.example.agenda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonData implements IPersonData {
    @Override
    public int insertPerson(String name) {
        PreparedStatement psPerson = null;
        ResultSet keys = null;
        int personId = -1;

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO Personas (nombre) VALUES (?)";
            psPerson = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psPerson.setString(1, name);
            psPerson.executeUpdate();

            keys = psPerson.getGeneratedKeys();
            if (keys.next()) {
                personId = keys.getInt(1);
            }
            psPerson.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (keys != null) keys.close();
                if (psPerson != null) psPerson.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return personId;
    }

    @Override
    public void deletePerson(int id) {
        PreparedStatement psPerson = null;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM Personas WHERE id = ?";
            psPerson = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psPerson.setInt(1, id);
            psPerson.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePersonName(int id, String name) {
        PreparedStatement psPerson;
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE Personas SET nombre = ? WHERE id = ?";
            psPerson = conn.prepareStatement(sql);
            psPerson.setString(1, name);
            psPerson.setInt(2, id);
            psPerson.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Person> getAll() {
        Statement stmt = null;
        ResultSet rs = null;
        List<Person> people = new ArrayList<>(List.of());

        try (Connection conn = DatabaseConnection.getConnection()) {
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
        return people;
    }
}