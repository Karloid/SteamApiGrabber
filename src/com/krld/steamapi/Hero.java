package com.krld.steamapi;

/**
 * Created by Andrey on 9/1/2014.
 */
public class Hero {
    private final int id;
    private final String name;
    private final String localizedName;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public Hero(String name, int id, String localizedName) {
        this.id = id;
        this.name = name;
        this.localizedName = localizedName;
    }

    @Override
    public String toString() {
        return "Hero{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", localizedName='" + localizedName + '\'' +
                '}';
    }
}
