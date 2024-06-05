package server.dao;

public interface AuthDAO {

    boolean isUserRegistered(String username);

    boolean passwordIsCorrect(String username, String password);

}
