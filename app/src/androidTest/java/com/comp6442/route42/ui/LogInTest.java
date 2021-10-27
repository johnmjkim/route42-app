package com.comp6442.route42.ui;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.comp6442.route42.R;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.ui.activity.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LogInTest {

    @Rule
    public ActivityScenarioRule<LogInActivity> activityRule = new ActivityScenarioRule<>(LogInActivity.class);

    private FirebaseAuth mAuth;

    @Before
    public void Logout() throws InterruptedException {
        mAuth = FirebaseAuthLiveData.getInstance().getAuth();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) //if not login, do logout
            onView(withId(R.id.sign_out_button)).perform(click());

    }
    @Test
    public void logInSucceed() throws InterruptedException {
        onView(withId(R.id.login_form_email)).perform(typeText("foo@bar.com"), closeSoftKeyboard());
        onView(withId(R.id.login_form_password)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
    }

    @Test
    public void logInFail() throws InterruptedException {
        onView(withId(R.id.login_form_email)).perform(typeText("fake@fake.com"), closeSoftKeyboard());
        onView(withId(R.id.login_form_password)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        Thread.sleep(1000);//add delay to pass test everytime
        onView(withId(R.id.login_form_password)).check(matches(withText("")));
    }
}