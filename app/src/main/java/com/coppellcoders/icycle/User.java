package com.coppellcoders.icycle;

import android.support.annotation.NonNull;

public class User implements Comparable<User>{
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    private String name;
    private int points;

    public User(String name, int points){
        this.name = name;
        this.points = points;



    }

    @Override
    public int compareTo(@NonNull User user) {
        int comparePoint = user.getPoints();
        return user.getPoints()-points;
    }
    @Override
    public String toString() {
        return "Name " + name + "Points: " + points;
    }
}


