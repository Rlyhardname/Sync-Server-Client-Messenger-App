package server.dao;

public interface AuthDAO<T> {

    boolean isUserRegistered(String username);

    boolean passwordIsCorrect(String username, String password);

}
