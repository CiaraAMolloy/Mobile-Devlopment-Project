package com.example.mobiledevlopmentproject;

public class Login {

    //simple setting and getting data

    String username;
    String email;
    String password;

    public Login(String user, String em, String pass){
        username=user;
        email=em;
        password=pass;

    }

    public String getUser(){return username;}
    public String getEmail(){return email;}
    public String getPass(){return password;}
}
