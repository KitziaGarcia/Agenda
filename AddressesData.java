package com.example.agenda;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressesData implements IAddressesData {
    @Override
    public void insertAddress(int id, List<String> addresses) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (String address : addresses) {
                int addressId = -1;

                String sqlFind = "SELECT id_direccion FROM Direcciones WHERE direccion = ?";
                try (PreparedStatement psFindAddress = conn.prepareStatement(sqlFind, Statement.RETURN_GENERATED_KEYS);) {
                    psFindAddress.setString(1, address);
                    ResultSet rs = psFindAddress.executeQuery();

                    if (rs.next()) {
                        addressId = rs.getInt("id_direccion");
                    } else {
                        String sqlInsert = "INSERT INTO Direcciones (direccion) VALUES (?)";
                        try (PreparedStatement psInsertAddress = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                            psInsertAddress.setString(1, address);
                            psInsertAddress.executeUpdate();
                            ResultSet keys = psInsertAddress.getGeneratedKeys();
                            if (keys.next()) {
                                addressId = keys.getInt(1);
                            }
                            keys.close();
                        }
                    }
                    rs.close();
                }

                String sqlInsertRelation = "INSERT INTO Personas_Direcciones (id_persona, id_direccion) VALUES (?, ?)";
                try (PreparedStatement psInsertRelation = conn.prepareStatement(sqlInsertRelation, Statement.RETURN_GENERATED_KEYS)){
                    psInsertRelation.setInt(1, id);
                    psInsertRelation.setInt(2, addressId);
                    psInsertRelation.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deletePersonAddresses(int id) {
        PreparedStatement psAddress = null;

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM Personas_Direcciones WHERE id_persona = ?";
            psAddress = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psAddress.setInt(1, id);
            psAddress.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUnusedAddresses() {
        PreparedStatement psDeleteUnused = null;

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM Direcciones WHERE id_direccion NOT IN (SELECT id_direccion FROM Personas_Direcciones)";
            psDeleteUnused = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psDeleteUnused.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getAddressesByPerson(int id) {
        List<String> addresses = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT d.direccion FROM Direcciones d JOIN Personas_Direcciones pd ON d.id_direccion = pd.id_direccion WHERE pd.id_persona = ?";
            PreparedStatement psAddresses = conn.prepareStatement(sql);
            psAddresses.setInt(1, id);
            ResultSet rs = psAddresses.executeQuery();

            while (rs.next()) {
                addresses.add(rs.getString("direccion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return addresses;
    }
}