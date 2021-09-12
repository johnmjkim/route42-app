package com.comp6442.route42.utils;

import java.time.LocalDateTime;

import timber.log.Timber;

/**
 * Extension of Timber DebugTree to include method name and line number of invocation.
 */
public class CustomLogger extends Timber.DebugTree {
  @Override
  protected String createStackElementTag(StackTraceElement element) {
    return String.format("[%s] %s.%s:%s",
            LocalDateTime.now(),
            super.createStackElementTag(element),
            element.getMethodName(),
            element.getLineNumber());
  }
}