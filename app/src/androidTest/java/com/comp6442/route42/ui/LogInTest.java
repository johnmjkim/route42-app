package com.comp6442.route42.ui;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.comp6442.route42.R;
import com.comp6442.route42.ui.activity.LogInActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LogInTest {

  @Rule
  public ActivityScenarioRule<LogInActivity> activityRule = new ActivityScenarioRule<>(LogInActivity.class);

  @Test
  public void logInSucceed() {
    onView(withId(R.id.login_form_email)).perform(typeText("foo@bar.com"), closeSoftKeyboard());
    onView(withId(R.id.login_form_password)).perform(typeText("password"), closeSoftKeyboard());
    onView(withId(R.id.login_button)).perform(click());
    onView(withId(R.id.sign_out_button)).perform(click());
    onView(withId(R.id.login_button)); // check exists
  }

  @Test
  public void logInFail() {
    onView(withId(R.id.login_form_email)).perform(typeText("fake@fake.com"), closeSoftKeyboard());
    onView(withId(R.id.login_form_password)).perform(typeText("password"), closeSoftKeyboard());
    onView(withId(R.id.login_button)).perform(click());
    onView(withId(R.id.login_form_password)).check(matches(withText("")));
  }
}