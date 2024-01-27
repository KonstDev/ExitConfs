package com.konstdev.exitconfs;
import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String mode;

    public User() {
    }

    public User(String id, String name, String mode) {
        this.id = id;
        this.name = name;
        this.mode = mode;
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.mode = "student";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMode() {
        return mode;
    }

}
