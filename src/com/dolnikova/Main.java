package com.dolnikova;

import com.dolnikova.com.dao.UserDao;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try {
            UserDao userDao = new UserDao();
            userDao.removeAll();

            User user1 = new User(Helper.generateId(), "Max", 27);
            userDao.addUser(user1);

            User user2 = new User(Helper.generateId(), "Irina", 40);
            userDao.addUser(user2);

            User user3 = new User(Helper.generateId(), "Alex", 19);
            userDao.addUser(user3);

            User user4 = new User(Helper.generateId(), "Dolnikova Yulia", 17);
            userDao.addUser(user4);

            user2.setAge(45);
            userDao.updateUser(user2);

            userDao.removeUser(user1.getId());

            User user5 = new User(Helper.generateId(), "Taras", 13);
            userDao.addUser(user5);

            User user6 = new User(Helper.generateId(), "Olga", 22);
            userDao.addUser(user6);

            userDao.removeUserByName("olga");

            List<User> userList = userDao.getAllUsers();
            System.out.println(userList);

            User getUser = userDao.getUser(user3.getId());
            System.out.println(getUser);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
