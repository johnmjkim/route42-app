package com.comp6442.route42.utils.tasks.scheduled_tasks;

import android.content.Context;

import java.io.IOException;

public interface Scheduler {
     CharSequence[] delayOptions = new CharSequence[]{"1", "5", "30", "60"};
     void schedule(Context context, int scheduledDelay) throws IOException;
     void cancel();

}

