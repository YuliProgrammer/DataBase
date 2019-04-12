package com.dolnikova.com.dao;

import com.dolnikova.User;
import com.dolnikova.local.properties.MySettings;
import com.google.gson.Gson;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDao implements Storage {

    private Connection connection;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public UserDao() throws SQLException {

        Gson gson = new Gson();
        String json = readSettings();

        MySettings mySettings = gson.fromJson(json, MySettings.class);
        String user = mySettings.getUser();
        String password = mySettings.getPassword();

        connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/myFirstDB", user, password);
        maybeCreateUsersTable();
    }

    private String readSettings() {
        StringBuilder sb = null;

        try {
            InputStream inputStream = new FileInputStream("settings.json");
            BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream));

            String line = buf.readLine();
            sb = new StringBuilder();

            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }

            String json = sb.toString();
            return json;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void maybeCreateUsersTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (\n" +
                    "_id uuid PRIMARY KEY,\n" +
                    "name varchar(100),\n" +
                    "age int\n" +
                    ");");
        }
    }

    @Override
    public void addUser(User user) {
        try {
            try (Statement statement = connection.createStatement()) {
                String request = String.format("INSERT INTO users VALUES ('%s', '%s', '%d');", user.getId(), user.getName(), user.getAge());
                statement.execute(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUser(User user) {
        try {

            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE users SET name = ?, age = ? WHERE _id = ?");

            UUID uid = UUID.fromString(user.getId());

            statement.setString(1, user.getName());
            statement.setInt(2, user.getAge());
            statement.setObject(3, uid);

            statement.executeUpdate();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAll() {

        try (Statement statement = connection.createStatement()) {
            int count = statement.executeUpdate("DELETE FROM users;");
            System.out.println("Deleted " + count + " rows from table users");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void removeUser(String id) {
        try {

            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM users WHERE _id = ?");

            UUID uid = UUID.fromString(id);
            statement.setObject(1, uid);

            statement.executeUpdate();
            statement.close();
            System.out.println("Deletion successful.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeUserByName(String name) {
        try {

            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM users WHERE LOWER(name) = LOWER(?)");
            statement.setString(1, name);

            statement.executeUpdate();
            statement.close();

            System.out.println("Deletion successful.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUser(String id) {
        try (Statement statement = connection.createStatement()) {
            UUID uid = UUID.fromString(id);

            String request = String.format("SELECT * FROM users WHERE _id = '%s';", uid);
            ResultSet resultSet = statement.executeQuery(request);

            if (resultSet.next()) {
                String userId = resultSet.getString("_id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                return new User(userId, name, age);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {

            String request = String.format("SELECT * FROM users;");
            ResultSet resultSet = statement.executeQuery(request);

            while (resultSet.next()) {
                String id = resultSet.getString("_id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                users.add(new User(id, name, age));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

}
