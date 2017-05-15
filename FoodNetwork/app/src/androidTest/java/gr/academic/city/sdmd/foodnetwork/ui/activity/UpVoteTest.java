package gr.academic.city.sdmd.foodnetwork.ui.activity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import gr.academic.city.sdmd.foodnetwork.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;



@RunWith(AndroidJUnit4.class)
@LargeTest

public class AppVoteTest {

    public ActivityTestRule<MealDetailsActivity> activityTestRule = new ActivityTestRule<>(MealDetailsActivity.class);

    @Test
    public void upVote() {
        onView((withId(R.id.btn_upvote_meal)).perf(click()));

    } }
