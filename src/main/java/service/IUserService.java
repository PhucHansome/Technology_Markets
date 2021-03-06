package service;

import model.User;

import java.util.List;

public interface IUserService extends IGeneralService<User> {
    User findById(long userId);

    User findFullById(long userId);

    boolean existsByEmail(String email);

    boolean existByUsername(String username);

    boolean existByPassWord1(String password);

    boolean existByUserId(long userId);

    boolean existByRoles(String Roles);


    List<User> findAll();


    List<User> findNameUser(String query);
}
