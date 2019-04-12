package com.dolnikova.com.dao;

import com.dolnikova.User;

import java.util.List;

public interface Storage {

    void addUser(User user);
    void updateUser(User user);

    void removeAll();
    void removeUser(String id);
    void removeUserByName(String name);

    User getUser(String id);
    List<User> getAllUsers();
}