package com.example.mobiledevlopmentproject;

import java.util.ArrayList;
import android.content.Context;

public abstract class LoginStore {
    private String username;
    private String email;
    private String password;

    public LoginStore(){

    }

    public LoginStore(String user, String em, String pass){
        this.username=user;
        this.email=em;
        this.password=pass;
    }

    public void setLogin(String user, String em, String pass){
        username=user;
        email=em;
        password=pass;
    }

    public String getUsername(){return username;}
    public void setUsername(String user){username=user;}

    public String getEmail(){return email;}
    public void setEmail(String em){email=em;}

    public String getPassword(){return password;}
    public void setPassword(String pass){password=pass;}

    public abstract void writeLoginData(Context context, ArrayList<Login> values);

    public abstract ArrayList<Login> getLogin(Context context);
}
