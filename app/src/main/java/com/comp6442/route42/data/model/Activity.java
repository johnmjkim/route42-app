package com.comp6442.route42.data.model;

import com.comp6442.route42.R;

import java.util.ArrayList;
import java.util.HashMap;

public interface Activity {
  int getCalories();

  Float getDistance();

  Float getSpeed();

  /**
   * Returns a post description string for creating posts about the activity.
   *
   * @return post description
   */
  String getPostString();

  enum Activity_Type {
    CYCLING(0), RUNNING(1), WALKING(2);
    private static final HashMap<Integer, Activity_Type> map = new HashMap<>();

    static {
      for (Activity_Type activityType : Activity_Type.values()) {
        map.put(activityType.value, activityType);
      }
    }

    private final int value;

    Activity_Type(int value) {
      this.value = value;
    }

    public static Activity_Type valueOf(int activityType) {
      return map.get(activityType);
    }

    public static int getIconResource(int activityType) {
      switch (activityType) {
        case 1:
          return R.drawable.run;
        case 2:
          return R.drawable.walk;
        default:
          return R.drawable.cycle;
      }
    }

    public static CharSequence[] getValues() {
      ArrayList<String> out = new ArrayList<String>();
      for (Activity_Type a : Activity_Type.values()) {
        out.add(a.toString());
      }
      return out.toArray(new CharSequence[out.size()]);
    }

    public int getValue() {
      return value;
    }
  }


}
