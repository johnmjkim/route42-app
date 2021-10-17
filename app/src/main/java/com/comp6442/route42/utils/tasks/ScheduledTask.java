package com.comp6442.route42.utils.tasks;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.comp6442.route42.data.model.Schedulable;
import com.comp6442.route42.data.repository.FirebaseStorageRepository;
import com.comp6442.route42.data.repository.PostRepository;

public class ScheduledTask extends Worker {
    private Schedulable schedulable ;
    public ScheduledTask(@NonNull Context context, @NonNull WorkerParameters workerParams, Schedulable schedulableObj) {
        super(context, workerParams);
        this.schedulable = schedulableObj;
    }

    @NonNull
    @Override
    public Result doWork() {
        schedulable.doWork();

        return Result.success();
    }
}
