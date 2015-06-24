package hiteware.com.halfwaythere;

import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created on 5/26/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class OnScreenTextTest {

    @Test
    public void whenStartedTheTitleSaysSteps()
    {
        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();

        assertThat(((TextView) createdActivity.findViewById(R.id.steps_title)).getText().toString(), equalTo("Steps"));
    }

    @Test
    public void whenStartedHalfWayThereButtonIsDisplayed()
    {
        HalfWayThereActivity createdActivity = Robolectric.buildActivity(HalfWayThereActivity.class).create().postResume().get();

        assertThat(((TextView) createdActivity.findViewById(R.id.HalfWayToggle)).getText().toString(), equalTo("HalfWayThere"));
        assertThat(((TextView) createdActivity.findViewById(R.id.HalfWayValue)).getText().toString(), equalTo(""));
    }
}