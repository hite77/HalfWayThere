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

    private MainActivity CreatedActivity;

    @Test
    public void whenStartedTheTitleSaysSteps()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        assertThat(((TextView) CreatedActivity.findViewById(R.id.steps_title)).getText().toString(), equalTo("Steps"));
    }
}