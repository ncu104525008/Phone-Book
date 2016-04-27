package com.example.sango.ghw4;

/**
 * Created by sango on 2016/4/27.
 */
public class PhoneCard {
    private int id;
    private String name;
    private String phone;
    private int status;

    public PhoneCard(int id, String name, String phone, int status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.status = status;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public int getStatus() {
        return this.status;
    }
}
