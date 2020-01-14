package com.example.snowsoultrips;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Trip {
    String tripid, createrid, location;
    ArrayList<String> allUsers;
    ArrayList<String> allUsersNames;

    public Trip() {
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
