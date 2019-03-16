package com.piyush052.weatherapp;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.piyush052.weatherapp.activities.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mActivity;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
    }


    @Test
    public void testPlaceTexViewWhenApiFetchedTheData() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.place))
                .check(matches(withText("Kakarmatha")));
    }

    @Test
    public void testTempTexViewWhenApiFetchedTheData() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.tempValue))
                .check(matches(withText("32")));
    }

    @Test
    public void testDay1TexViewWhenApiFetchedTheData() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.day1))
                .check(matches(withText("Sunday")));
    }

    @Test
    public void testDay2TexViewWhenApiFetchedTheData() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.day2))
                .check(matches(withText("Monday")));
    }

    @Test
    public void testDay3TexViewWhenApiFetchedTheData() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.day3))
                .check(matches(withText("Tuesday")));
    }

    @Test
    public void testDay4TexViewWhenApiFetchedTheData() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.day4))
                .check(matches(withText("Wednesday")));
    }


    @Test
    public void testDay1ValueTexViewWhenApiFetchedTheData() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.day1Value))
                .check(matches(withText("26 C")));
    }

    @Test
    public void testDay2ValueTexViewWhenApiFetchedTheData() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.day2Value))
                .check(matches(withText("28 C")));
    }

    @Test
    public void testDay3ValueTexViewWhenApiFetchedTheData() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.day3Value))
                .check(matches(withText("29 C")));
    }

    @Test
    public void testDay4ValueTexViewWhenApiFetchedTheData() {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.day4Value))
                .check(matches(withText("31 C")));
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}