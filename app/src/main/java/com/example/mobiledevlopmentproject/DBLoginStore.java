package com.example.mobiledevlopmentproject;

import android.content.Context;
import java.util.ArrayList;

public class DBLoginStore extends LoginStore{
    //Will contain username, email, password.
    //TODO: figure out how to link my data with Ciara's data.

    private static DBHandler loginData;
    public DBLoginStore(Context context){
        super();
        if (loginData == null){
            loginData = new DBHandler(context);
            //simply creates new info for the database
        }
    }

    @Override
    public void writeLoginData(Context context, ArrayList<Login> values){
        if (!values.isEmpty()){
            for (Login x : values){
                loginData.add(x);
            }
        }
    }

    @Override
    public ArrayList<Login> getLogin(Context context) {
        return null;
    }
}
