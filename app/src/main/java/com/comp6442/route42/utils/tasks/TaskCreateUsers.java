package com.comp6442.route42.utils.tasks;

import android.content.Context;

import com.comp6442.route42.data.model.User;
import com.comp6442.route42.data.repository.UserRepository;

public class TaskCreateUsers extends DataTask<User> {
  public TaskCreateUsers(Context context, int inputRawResourceId) {
    super(
            context,
            inputRawResourceId,
            "users",
            User.class,
            UserRepository.getInstance(),
            UserRepository.getJsonDeserializer()
    );
  }
}
