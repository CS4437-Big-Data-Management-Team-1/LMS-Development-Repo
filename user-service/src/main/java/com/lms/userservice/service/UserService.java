package com.lms.userservice.service;

import com.lms.userservice.database.UserDatabaseConnector;

public class UserService {
    public static void main(String[] args) {
        UserDatabaseConnector database = new UserDatabaseConnector();
        database.connectToDB();
        database.addUserToDB();
        database.disconnectFromDB();

    }
}
