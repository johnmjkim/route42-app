package com.comp6442.route42.data.model;

import java.sql.Time;
import java.util.ArrayList;

public interface Activity {
    enum Activity_Type { CYCLING, RUNNING, WALKING, MOTORING;
        public static CharSequence[] getValues() {
            ArrayList<String> out = new ArrayList();
            for (Activity_Type a : Activity_Type.values()) {
                 out.add(a.toString());
            }
            return out.toArray(new CharSequence[out.size()]);
        }
    }
//    Time startTime;
//    Time endTime;
    Float getCalories();
    Float getDistance();
    Float getSpeed();
    Long getElapsedTime();
    boolean setLocation();
    Location getLocation();

}
