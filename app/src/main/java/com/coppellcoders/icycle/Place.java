package com.coppellcoders.icycle;

import android.support.annotation.NonNull;

/**
 * Created by rkark on 4/21/2018.
 */

public class Place implements Comparable<Place>{
    double x;
    double y;
    double dist;
    String name;

    public Place(double x, double y, double dist, String name){
        this.x = x;
        this.y = y;
        this.name = name;
        this.dist = 0;
    }
    public void findDistance(double newX, double newY){
       this.dist = distance(x, y, newX, newY);

    }
    public double distance(double startLat, double startLong,
                                  double endLat, double endLong) {
        final int EARTH_RADIUS = 6371;
        double dLat  = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat   = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double temp =  EARTH_RADIUS * c * .621371;
        temp = Math.round(temp * 100)/100.0;
        return temp;
    }

    public double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
    @Override
    public int compareTo(@NonNull Place o) {
       if(dist == o.dist){
            return 0;
       }else if(dist > o.dist){
           return 1;
       }else{
           return -1;
       }
    }
}
