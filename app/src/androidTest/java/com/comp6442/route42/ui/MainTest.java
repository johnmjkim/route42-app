package com.comp6442.route42.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.comp6442.route42.R;
import com.comp6442.route42.Route42App;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.data.model.Post;
import com.comp6442.route42.ui.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
    private FirebaseAuth mAuth;

    @Before
    public void Login(){
//        ActivityScenario scenario = activityRule.getScenario(); // not sure how can I use this
        mAuth = FirebaseAuthLiveData.getInstance().getAuth();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser==null) {//if not login, do login
            onView(withId(R.id.login_form_email)).perform(typeText("foo@bar.com"), closeSoftKeyboard());
            onView(withId(R.id.login_form_password)).perform(typeText("password"), closeSoftKeyboard());
            onView(withId(R.id.login_button)).perform(click());
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        onView(withId(R.id.sign_out_button)).perform(click());
    }

    @Test
    public void feedToMapCheck() {//check both side page change between feed and map / / currently now working because of map
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.navigation_map)).perform(click()).check(matches(withId(R.id.navigation_map)));
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.navigation_profile)).perform(click()).check(matches(withId(R.id.navigation_profile)));
        onView(withId(R.id.sign_out_button)).perform(click());
    }
    @Test
    public void CreateCyclingPost(){
        createPost("#hash","CYCLING");
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));

    }

    @Test
    public void CreateRunningPost(){
        createPost("#hash","RUNNING");
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void CreateWalkingPost(){
        createPost("#hash","WALKING");
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));

    }

    @Test
    public void CancelPost(){
        onView(withId(R.id.Btn_Create_Activity)).perform(click());
        onView(withText("Choose Activity Type")).check(matches(isDisplayed()));
        onView(withText("CYCLING")).perform(click());
        try {//make delay to get data from active_map_fragment
            Thread.sleep(500);
        if(checkAccess(onView(withText("Allow Route42 to access this device's location")))){
            onView(withText("While using the app")).perform(click());
        }
            onView(withId(R.id.activity_button)).perform(click());
            onView(withText("Start")).perform(click());
            Thread.sleep(10000);//make delay to get data from active_map_fragment
            onView(withId(R.id.activity_button)).perform(click());
            onView(withText("End Activity")).perform(click());
            Thread.sleep(500);//make delay
            onView(withId(R.id.post_description_input)).perform(typeText("CancelTest"), closeSoftKeyboard());
            onView(withId(R.id.cancel_post_button)).perform(click());
            onView(withId(R.id.navigation_feed)).perform(click()).check(matches(isDisplayed()));
            onView(withText("CancelTest")).check(doesNotExist());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void SearchPost(){//have to fix
//        createPost("#hash","CYCLING");
//        onView(withId(R.id.search_view)).perform(typeText("#hash"), closeSoftKeyboard());
//        onView(withText("#hash")).check(matches(isDisplayed())); // not sure for this
//    }
//
//    @Test
//    public void PushLikeUnlike(){//have to fix            v
//        createPost("#hash","CYCLING");
//        onView(withId(R.id.search_view)).perform(typeText("#hash"), closeSoftKeyboard());
//        int num = R.id.like_count_text;
//        onView(withId(R.id.like_button)).perform(click());
//        Assert.assertEquals(R.id.like_count_text,num+1); //check count increasement
//        onView(withId(R.id.unlike_button)).perform(click());
//        Assert.assertEquals(R.id.like_count_text,num); //check count decreasement
//    }
//
//    @Test
//    public void blockUnBlockCheck(){//have to              v
////        createPost("#hash","CYCLING");
//        onView(withId(R.id.search_view)).perform(typeText("#hash"), closeSoftKeyboard());
//        onView(withId(R.id.card_username)).perform(click());
//        onView(withId(R.id.profile_block_switch)).perform(click()).check(matches(isChecked()));//check blocked
//        onView(withId(R.id.profile_block_switch)).perform(click()).check(matches(isNotChecked()));//check not blocked
//    }
//    @Test
//    public void followUnfollowCheck(){//have to fix         v
////        createPost("#hash","CYCLING");
//        onView(withId(R.id.search_view)).perform(typeText("#hash"), closeSoftKeyboard());
//        onView(withId(R.id.card_username)).perform(click());
//        onView(withId(R.id.profile_follow_switch)).perform(click()).check(matches(isChecked()));//check blocked
//        onView(withId(R.id.profile_follow_switch)).perform(click()).check(matches(isNotChecked()));//check not blocked
//    }


    protected boolean checkAccess(ViewInteraction textView) {
        String text = textView.toString();
        return text.matches("Allow Route42 to access this device's location");
    }

    public void createPost(String keyword,String activityType){// create post
        //--------------------------Start to make post-------------------------------------------------

        onView(withId(R.id.Btn_Create_Activity)).perform(click());
        onView(withText("Choose Activity Type")).check(matches(isDisplayed()));//check dialog is on
        onView(withText(activityType)).perform(click());//choose activity type
        try{
            Thread.sleep(300);//make delay to check the dialog
        if(checkAccess(onView(withText("Allow Route42 to access this device's location")))){//if app asks permission, click it
            onView(withText("While using the app")).perform(click());
        }
            Thread.sleep(1000);//make delay to get data from active_map_fragment
            onView(withId(R.id.activity_button)).perform(click());
            onView(withText("Start")).perform(click());
            Thread.sleep(10000); //make delay to start duration
            //--------------------------Active_map_fragment------------------------------------------------
            onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.activity_icon)).check(matches(isDisplayed()));
            onView(withId(R.id.activity_icon)).check(matches(isDisplayed()));
            onView(withId(R.id.linearLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.map_fragment2)).check(matches(isDisplayed())); //until this line, component check
            Thread.sleep(100);
            onView(withId(R.id.activity_button)).perform(click());
            onView(withText("End Activity")).perform(click());
            //--------------------------Create_post_fragment-------------------------------------------------
            Thread.sleep(500);//make delay to get data from active_map_fragment
            //--------------------------Write description and then create post------------------------------- will update when we can make hashtag function
            onView(withId(R.id.post_description_input)).perform(typeText(activityType), closeSoftKeyboard()); //add activitytype to distinguish the post for test
//        onView(withId(R.id.post_description_input)).perform(typeText(keyword), closeSoftKeyboard());//will update it when I can write hashtag
            onView(withId(R.id.create_post_button)).perform(click());
            //--------------------------Create post is finished and check post in feed fragment---------------
            onView(withId(R.id.navigation_feed)).perform(click());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
