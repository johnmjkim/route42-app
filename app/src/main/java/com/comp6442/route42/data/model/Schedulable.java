package com.comp6442.route42.data.model;

import android.content.Context;

import java.io.IOException;

public interface Schedulable {
     CharSequence[] delayOptions = new CharSequence[]{"1", "5", "30", "60"};
     void schedule(Context context, int scheduledDelay) throws IOException;
     void cancel();

}

