package com.samirthebti.amen_go.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Amen_Go
 * Created by Samir Thebti on 5/17/17.
 * thebtisam@gmail.com
 */

public class MyContact extends RealmObject {
    private String name;
    @PrimaryKey
    private String phone;

    public MyContact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public MyContact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "MyContact{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
