package com.example.agenda;

import java.sql.*;

public class PhoneNumbersData {

    public void insertPhoneNumber(int personId, String phone) {
        PreparedStatement psPhone = null;
        ResultSet rsPhone = null;

        try {
            Connection conn = DatabaseConnection.getConnection();
            String phoneNumber = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
            psPhone = conn.prepareStatement(phoneNumber);
            psPhone.setInt(1, personId);
            psPhone.setString(2, phone);
            psPhone.executeUpdate();
            psPhone.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (psPhone != null) psPhone.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }
}