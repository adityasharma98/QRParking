package com.example.qrparking;

import java.util.ArrayList;

public class User {
    String name;
    String username;
    String password;
    String parkingspot;

    public User(){

    }

    public User(String name,String username, String password){
        this.name=name;
        this.username=username;
        this.password=password;
        this.parkingspot="null";
    }
    public User(String name,String username, String password, String parkingspot){
        this.name=name;
        this.username=username;
        this.password=password;
        this.parkingspot=parkingspot;
    }


    public String getUsername(){
        return this.username;
    }

    public String getName() {
        return name;
    }
}
