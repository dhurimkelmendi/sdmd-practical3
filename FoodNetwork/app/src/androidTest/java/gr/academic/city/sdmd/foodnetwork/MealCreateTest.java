package gr.academic.city.sdmd.foodnetwork;

import android.app.TimePickerDialog;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.ui.activity.MealsActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MealCreateTest {

    @Rule
    public ActivityTestRule<MealsActivity> activityTestRule = new ActivityTestRule<>(MealsActivity.class);

    @Test
    public void addMeal() {
        onView(withId(R.id.tv_meal_title)).perform(typeText("broccoli"),
                closeSoftKeyboard());
        onView(withId(R.id.txt_recipe)).perform(typeText("broccoli,water"),
                closeSoftKeyboard());
        onView(withId(R.id.txt_number_of_servings)).perform(typeText("4"),
                closeSoftKeyboard());
        onView(withId(R.id.btn_add_meal)).perform(click());
    }
}