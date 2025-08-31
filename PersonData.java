package com.example.agenda;

import java.sql.*;

public class PersonData {
    private PhoneNumbersData phoneNumbersData = new PhoneNumbersData();
    private AddressesData addressesData = new AddressesData();


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
}
