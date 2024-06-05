package server.dao;

import client.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthenticationDAO implements AuthDAO {
    private final DataSource dataSource;

    public AuthenticationDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public boolean isUserRegistered(String username) {
        String sql = "SELECT username FROM User " + "WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setString(1, username);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                if (rs.getString(1) != null) {
                    System.out.println("User exists");
                    return true;
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public boolean passwordIsCorrect(String username, String password) {
        // TODO Auto-generated method stub
        String sql = "SELECT password from User where username = ?";
        try(Connection conn = dataSource.getConnection();
        PreparedStatement prep = conn.prepareStatement(sql)) {
            prep.setString(1, username);
            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                if (rs.getString(1).equals(password)) {
                    return true;
                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        }
        return false;
    }

}
