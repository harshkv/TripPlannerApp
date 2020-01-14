package com.example.snowsoultrips;

import java.util.ArrayList;

public class MyTrip {
    String tripid, createrid, location;
    ArrayList<String> allUsers;

    public MyTrip() {
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripid='" + tripid + '\'' +
                ", createrid='" + createrid + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
