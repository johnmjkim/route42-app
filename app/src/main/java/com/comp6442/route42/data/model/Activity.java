package com.comp6442.route42.data.model;

import android.graphics.drawable.Drawable;

import com.comp6442.route42.R;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface Activity {
    enum Activity_Type { CYCLING(0), RUNNING(1), WALKING(2);
        private final int value;
        private static Map map = new HashMap<>();
        Activity_Type(int value) {
            this.value = value;
        }
        public static Activity_Type valueOf(int activityType) {
            return (Activity_Type) map.get(activityType);
        }
        static {
            for (Activity_Type activityType : Activity_Type.values()) {
                map.put(activityType.value, activityType);
            }
        }
        public static int getIconResource (int activityType) {
            switch (activityType) {
                case 1:
                    return R.drawable.cycle;
                case 2:
                    return R.drawable.run;
                default:
                    return R.drawable.walk;
            }
        }

        public static CharSequence[] getValues() {
            ArrayList<String> out = new ArrayList();
            for (Activity_Type a : Activity_Type.values()) {
                 out.add(a.toString());
            }
            return out.toArray(new CharSequence[out.size()]);
        }
    }
   int getCalories();
    Float getDistance();
    Float getSpeed();
    Long getElapsedTime();


}
