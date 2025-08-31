package com.example.agenda;

import java.sql.*;

public class AddressesData {

    public void insertAddress(int personId, String address) {
        PreparedStatement psAddress = null;
        ResultSet rsAddress = null;
        PreparedStatement psPersonAddress = null;

        try {
            Connection conn = DatabaseConnection.getConnection();
            String findAddress = "SELECT id_direccion FROM Direcciones WHERE direccion = ?";
            psAddress = conn.prepareStatement(findAddress, Statement.RETURN_GENERATED_KEYS);
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
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (psAddress != null) psAddress.close();
                if (psPersonAddress != null) psPersonAddress.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}