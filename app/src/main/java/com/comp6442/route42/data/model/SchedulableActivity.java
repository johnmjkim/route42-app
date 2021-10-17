package com.comp6442.route42.data.model;

import java.util.Date;

public class SchedulableActivity implements  Schedulable<Activity>{
    private final String localDirBasePath = "";
    private final double[] scheduleDurationInMinutes = new double[]{ 1.0, 5.0, 10.0, 30.0};
    @Override
    public void schedule(Date scheduledTime, Activity object) {
        // parse activity to string
        // save string to local

    }
    @Override
    public void cancel() {

    }

    @Override
    public void cancelAll() {

    }

    @Override
    public Activity getAll() {
        return null;
    }

    @Override
    public boolean doAction() {
        //
        return false;
    }
    private Activity[] parseFile() {
        return new Activity[]{};
    }
}
