package com.example.agenda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhoneNumbersData implements IPhoneNumbersData {

    @Override
    public void insertPhoneNumber(int id, List<String> phones) {
        PreparedStatement psPhone;

        for (String phone : phones) {
            String sql = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
            try {
                Connection conn = DatabaseConnection.getConnection();
                psPhone = conn.prepareStatement(sql);
                psPhone.setInt(1, id);
                psPhone.setString(2, phone);
                psPhone.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deletePhonesByPerson(int id) {
        PreparedStatement psPhone;

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM Telefonos WHERE personaId = ?";
            psPhone = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psPhone.setInt(1, id);
            psPhone.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getPhonesByPerson(int id) {
        List<String> phones = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT telefono FROM telefonos WHERE personaId = ?";
            PreparedStatement psPhones = conn.prepareStatement(sql);
            psPhones.setInt(1, id);
            ResultSet rs = psPhones.executeQuery();

            while (rs.next()) {
                phones.add(rs.getString("telefono"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phones;
    }
}