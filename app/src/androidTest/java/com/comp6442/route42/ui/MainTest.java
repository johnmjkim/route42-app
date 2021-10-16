package com.comp6442.route42.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.comp6442.route42.R;
import com.comp6442.route42.ui.activity.MainActivity;
import com.comp6442.route42.ui.fragment.MapFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void Login(){
//        ActivityScenario scenario = activityRule.getScenario(); // not sure how can I use this
        onView(withId(R.id.login_form_email)).perform(typeText("foo@bar.com"), closeSoftKeyboard());
        onView(withId(R.id.login_form_password)).perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
    }

    @Test
    public void profileToFeedCheck() {//check both side page change between profile and feed
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.navigation_profile)).perform(click()).check(matches(withId(R.id.navigation_profile)));
    }

    @Test
    public void profileToMapCheck() {//check both side page change between profile and map / currently now working because of map
        onView(withId(R.id.navigation_map)).perform(click()).check(matches(withId(R.id.navigation_map)));
        onView(withId(R.id.navigation_profile)).perform(click()).check(matches(withId(R.id.navigation_profile)));
    }

    @Test
    public void feedToMapCheck() {//check both side page change between feed and map / / currently now working because of map
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.navigation_map)).perform(click()).check(matches(withId(R.id.navigation_map)));
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.navigation_profile)).perform(click()).check(matches(withId(R.id.navigation_profile)));
    }
    @Test
    public void CreatePost(){//make post is okay and currently add checking basic features(struggle with this)
        onView(withId(R.id.Btn_Create_Activity)).perform(click());
        onView(withText("Choose Activity Type")).check(matches(isDisplayed()));
        onView(withText("CYCLING")).perform(click());
        //check other features before making posts
        onView(withId(R.id.activity_button)).perform(click());
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withText("test_user"));
    }

    @Test
    public void CancelPost(){//checking basic features(currently struggle with this)

    }

    @Test
    public void SearchPost(){

    }

    @Test
    public void PushLike(){

    }

    @Test
    public void Block(){

    }

    @Test
    public void BlockedBy(){

    }


}
