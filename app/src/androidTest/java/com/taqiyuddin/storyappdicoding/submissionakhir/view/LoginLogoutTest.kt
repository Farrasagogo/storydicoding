package com.taqiyuddin.storyappdicoding.submissionakhir.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.taqiyuddin.storyappdicoding.submissionakhir.R
import com.taqiyuddin.storyappdicoding.submissionakhir.utils.EspressoIdlingResource
import com.taqiyuddin.storyappdicoding.submissionakhir.view.login.LoginActivity
import com.taqiyuddin.storyappdicoding.submissionakhir.view.main.MainActivity
import com.taqiyuddin.storyappdicoding.submissionakhir.view.story.StoryActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginLogoutTest {

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        init()
    }

    @Test
    fun loginLogoutFlow_Success() {

        onView(withId(R.id.ed_login_email))
            .perform(typeText("sigma@gmail.com"), closeSoftKeyboard())

        onView(withId(R.id.ed_login_password))
            .perform(typeText("sigma123"), closeSoftKeyboard())

        onView(withId(R.id.loginButton))
            .perform(click())

        intended(hasComponent(StoryActivity::class.java.name))

        onView(withText(R.id.action_logout))
            .perform(click())

        onView(withId(R.id.action_logout)).perform(click())

        onView(withText(R.string.logout)).inRoot(isDialog()).check(matches(isDisplayed()))

        onView(withText(R.string.yes)).inRoot(isDialog()).perform(click())

        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun loginWithInvalidCredentials_ShowsError() {

        onView(withId(R.id.ed_login_email))
            .perform(typeText("invalid@email"), closeSoftKeyboard())

        onView(withId(R.id.ed_login_password))
            .perform(typeText("invalidpass"), closeSoftKeyboard())

        onView(withId(R.id.loginButton))
            .perform(click())

        onView(withId(R.id.ed_login_email))
            .check(matches(isDisplayed()))

        onView(withId(R.id.ed_login_password))
            .check(matches(isDisplayed()))

        onView(withId(R.id.loginButton))
            .check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        release()
    }
}