package com.comp6442.route42.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.core.StringContains.containsString;
import android.Manifest;
import android.content.res.Resources;
import android.view.View;
import android.widget.Checkable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;
import com.comp6442.route42.R;
import com.comp6442.route42.data.FirebaseAuthLiveData;
import com.comp6442.route42.ui.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);
    private FirebaseAuth mAuth;

    @Before
    public void Login() throws InterruptedException {
//        ActivityScenario scenario = activityRule.getScenario(); // not sure how can I use this
        mAuth = FirebaseAuthLiveData.getInstance().getAuth();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {//if not login, do login
            onView(withId(R.id.login_form_email)).perform(typeText("foo@bar.com"), closeSoftKeyboard());
            onView(withId(R.id.login_form_password)).perform(typeText("password"), closeSoftKeyboard());
            onView(withId(R.id.login_button)).perform(click());
        }
        Thread.sleep(500);
    }

    @Test
    public void profileToFeedCheck() throws InterruptedException {//check both side page change between profile and feed
        Thread.sleep(500);
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.navigation_profile)).perform(click()).check(matches(withId(R.id.navigation_profile)));
    }

    @Test
    public void profileToMapCheck() throws InterruptedException {//check both side page change between profile and map
        Thread.sleep(500);
        onView(withId(R.id.navigation_map)).perform(click()).check(matches(withId(R.id.navigation_map)));
        onView(withId(R.id.navigation_profile)).perform(click()).check(matches(withId(R.id.navigation_profile)));
        onView(withId(R.id.sign_out_button)).perform(click());
    }

    @Test
    public void feedToMapCheck() throws InterruptedException {//check both side page change between feed and map
        Thread.sleep(500);
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.navigation_map)).perform(click()).check(matches(withId(R.id.navigation_map)));
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.navigation_profile)).perform(click()).check(matches(withId(R.id.navigation_profile)));
        onView(withId(R.id.sign_out_button)).perform(click());
    }

    @Test
    public void createCyclingPost() throws InterruptedException {//make cyclingpost and check hashtags
        Thread.sleep(500);
        createPost("#cycle #bicycle","CYCLING");
        onView(new RecyclerViewMatcher(R.id.profile_recycler_view).atPosition(0)).check(matches(hasDescendant(withText(containsString("cycle")))));
        onView(new RecyclerViewMatcher(R.id.profile_recycler_view).atPosition(0)).check(matches(hasDescendant(withText(containsString("bicycle")))));
    }

    @Test
    public void createRunningPost() throws InterruptedException {//make runningpost and check hashtags
        Thread.sleep(500);
        createPost("#run #course","RUNNING");
        onView(new RecyclerViewMatcher(R.id.profile_recycler_view).atPosition(0)).check(matches(hasDescendant(withText(containsString("run")))));
        onView(new RecyclerViewMatcher(R.id.profile_recycler_view).atPosition(0)).check(matches(hasDescendant(withText(containsString("course")))));
    }

    @Test
    public void createWalkingPost() throws InterruptedException {////make walkingpost and check hashtags
        Thread.sleep(500);
        createPost("#walk #join","WALKING");
        onView(new RecyclerViewMatcher(R.id.profile_recycler_view).atPosition(0)).check(matches(hasDescendant(withText(containsString("walk")))));
        onView(new RecyclerViewMatcher(R.id.profile_recycler_view).atPosition(0)).check(matches(hasDescendant(withText(containsString("join")))));
    }

    @Test
    public void cancelPost() throws InterruptedException {//cancel making post and check the post is exist
        Thread.sleep(500);
        onView(withId(R.id.Btn_Create_Activity)).perform(click());
        onView(withText("Choose Activity Type")).check(matches(isDisplayed()));
        onView(withText("CYCLING")).perform(click());
        //make delay to get data
        Thread.sleep(500);
        onView(withId(R.id.activity_button)).perform(click());
        Thread.sleep(10000);//make delay to get data from map
        onView(withId(R.id.activity_button)).perform(click());
        onView(withText("End Activity")).perform(click());
        Thread.sleep(500);//make delay to get data
        onView(withId(R.id.post_description_input)).perform(typeText("CancelTest"), closeSoftKeyboard());
        onView(withId(R.id.cancel_post_button)).perform(click());
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(isDisplayed()));
        onView(withText("CancelTest")).check(doesNotExist());

    }

    @Test
    public void schedulePost() throws InterruptedException {//make scheduled post(1min delay) and check the post after 1 min
        Thread.sleep(500);
        onView(withId(R.id.Btn_Create_Activity)).perform(click());
        onView(withText("Choose Activity Type")).check(matches(isDisplayed()));
        onView(withText("CYCLING")).perform(click());
        Thread.sleep(500);//make delay to get data
        onView(withId(R.id.activity_button)).perform(click());
        Thread.sleep(10000);//make delay to get data from map
        onView(withId(R.id.activity_button)).perform(click());
        onView(withText("End Activity")).perform(click());
        Thread.sleep(500);//make delay to get data
        onView(withId(R.id.post_description_input)).perform(typeText("#delayTest"), closeSoftKeyboard());
        onView(withId(R.id.create_post_schedule_switch)).perform(click());
        onView(withText("Select Delay (Minutes)")).check(matches(isDisplayed()));
        onView(withText("1")).perform(click());
        onView(withId(R.id.create_post_button)).perform(click());
        Thread.sleep(60000);//make delay one minute
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.navigation_profile)).perform(click()).check(matches(withId(R.id.navigation_profile)));
        onView(new RecyclerViewMatcher(R.id.profile_recycler_view).atPosition(0)).check(matches(hasDescendant(withText(containsString("delayTest")))));
    }

    @Test
    public void pushLikeUnlike() throws InterruptedException {//check like,unlike buttons are active
        Thread.sleep(500);
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.like_button)));
        Thread.sleep(500);
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.unlike_button)));
    }

    @Test
    public void blockUnBlockCheck() throws InterruptedException {//check block,unblock buttons are active
        Thread.sleep(500);
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(10, MyViewAction.clickChildViewWithId(R.id.card_username)));
        onView(withId(R.id.profile_block_switch)).perform(click(),setChecked(true)).check(matches(isChecked()));//check blocked
        Thread.sleep(500);
        onView(withId(R.id.profile_block_switch)).perform(click(),setChecked(false)).check(matches(isNotChecked()));//check not blocked
    }

    @Test
    public void followUnfollowCheck() throws InterruptedException {//check follow,unfollow buttons are active
        Thread.sleep(500);
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(10, MyViewAction.clickChildViewWithId(R.id.card_username)));
        onView(withId(R.id.profile_follow_switch)).perform(click(), setChecked(true)).check(matches(isChecked()));//check followed
        Thread.sleep(500);
        onView(withId(R.id.profile_follow_switch)).perform(click(),setChecked(false)).check(matches(isNotChecked()));//check followed

    }

    @Test
    public void followBlockCheck() throws InterruptedException {//check follow user is translated to unfollow whey blocked
        Thread.sleep(500);
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(10, MyViewAction.clickChildViewWithId(R.id.card_username)));
        onView(withId(R.id.profile_follow_switch)).perform(click(),setChecked(true)).check(matches(isChecked()));//check followed
        Thread.sleep(500);
        onView(withId(R.id.profile_block_switch)).perform(click(),setChecked(true)).check(matches(isChecked()));//blocked
        onView(withId(R.id.profile_follow_switch)).check(matches(isNotChecked()));//check not followed
    }

    @Test
    public void blockFollowCheck() throws InterruptedException {//check cannot follow blocked user
        Thread.sleep(500);
        onView(withId(R.id.navigation_feed)).perform(click()).check(matches(withId(R.id.navigation_feed)));
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(10, MyViewAction.clickChildViewWithId(R.id.card_username)));
        onView(withId(R.id.profile_block_switch)).perform(click(),setChecked(true)).check(matches(isChecked()));//check followed
        Thread.sleep(500);
        onView(withId(R.id.profile_follow_switch)).perform(click()).check(matches(isNotChecked()));//check not followed
    }


    public void createPost(String keyword, String activityType) throws InterruptedException {
        //--------------------------Start to make post-------------------------------------------------
        Thread.sleep(500);
        onView(withId(R.id.Btn_Create_Activity)).perform(click());
        onView(withText("Choose Activity Type")).check(matches(isDisplayed()));//check dialog is on
        onView(withText(activityType)).perform(click());//choose activity type
        Thread.sleep(4000);//make delay to get data from active_map_fragment
        onView(withId(R.id.activity_button)).perform(click());
        Thread.sleep(4000); //make delay to start duration
        //--------------------------Active_map_fragment------------------------------------------------
        onView(withId(R.id.constraintLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_icon)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_icon)).check(matches(isDisplayed()));
        onView(withId(R.id.linearLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.map_fragment2)).check(matches(isDisplayed())); //until this line, component check
        onView(withId(R.id.activity_button)).perform(click());
        onView(withText("End Activity")).perform(click());
        //--------------------------Create_post_fragment-------------------------------------------------
        Thread.sleep(3000);//make delay to get data from active_map_fragment
        onView(withId(R.id.post_description_input)).perform(typeText(keyword), closeSoftKeyboard()); //add hashtags to distinguish the post for test
        onView(withId(R.id.create_post_button)).perform(click());
    }

    public static class MyViewAction {//reference1 https://stackoverflow.com/questions/28476507/using-espresso-to-click-view-inside-recyclerview-item

        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }
    }

    public class RecyclerViewMatcher {//reference2 https://github.com/levibostian/RecyclerViewMatcher

        private final int recyclerViewId;

        public RecyclerViewMatcher(int recyclerViewId) {
            this.recyclerViewId = recyclerViewId;
        }

        public Matcher<View> atPosition(final int position) {
            return atPositionOnView(position, -1);
        }

        public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

            return new TypeSafeMatcher<View>() {
                Resources resources = null;
                View childView;

                public void describeTo(Description description) {
                    String idDescription = Integer.toString(recyclerViewId);
                    if (this.resources != null) {
                        try {
                            idDescription = this.resources.getResourceName(recyclerViewId);
                        } catch (Resources.NotFoundException var4) {
                            idDescription = String.format("%s (resource name not found)", recyclerViewId);
                        }
                    }

                    description.appendText("RecyclerView with id: " + idDescription + " at position: " + position);
                }

                public boolean matchesSafely(View view) {

                    this.resources = view.getResources();

                    if (childView == null) {
                        RecyclerView recyclerView =
                                (RecyclerView) view.getRootView().findViewById(recyclerViewId);
                        if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
                            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                            if (viewHolder != null) {
                                childView = viewHolder.itemView;
                            }
                        } else {
                            return false;
                        }
                    }

                    if (targetViewId == -1) {
                        return view == childView;
                    } else {
                        View targetView = childView.findViewById(targetViewId);
                        return view == targetView;
                    }
                }
            };
        }
    }
    public static ViewAction setChecked(final boolean checked) {//reference3 https://stackoverflow.com/questions/37819278/android-espresso-click-checkbox-if-not-checked
        return new ViewAction() {
            @Override
            public BaseMatcher<View> getConstraints() {
                return new BaseMatcher<View>() {
                    @Override
                    public boolean matches(Object item) {
                        return isA(Checkable.class).matches(item);
                    }

                    @Override
                    public void describeMismatch(Object item, Description mismatchDescription) {
                    }

                    @Override
                    public void describeTo(Description description) {
                    }
                };
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                Checkable checkableView = (Checkable) view;
                checkableView.setChecked(checked);
            }
        };
    }
}