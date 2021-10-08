package com.comp6442.route42.data.model;

import java.sql.Time;

public interface Activity {
    enum Activity_Type { CYCLING, RUNNING, WALKING, MOTORING }
//    Time startTime;
//    Time endTime;
    Float getCalories();
    Float getDistance();
    Float getSpeed();
    Long getElapsedTime();
    boolean setLocation();
    Location getLocation();

}
