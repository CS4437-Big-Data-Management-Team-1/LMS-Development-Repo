package com.lms.userservice.service;

import com.lms.userservice.database.UserDatabaseConnector;

public class UserService {
    public static void main(String[] args) {

        //just sample test to check if userservice can add values to DB
        UserDatabaseConnector database = new UserDatabaseConnector();
        database.connectToDB();
        database.addUserToDB();
        database.disconnectFromDB();

    }
}
