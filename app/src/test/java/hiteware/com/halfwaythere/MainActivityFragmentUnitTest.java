package hiteware.com.halfwaythere;

import android.content.Intent;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jasonhite on 5/31/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityFragmentUnitTest
{
    public MainActivity CreatedActivity;


    @Test
    public void whenTheAppIsRunningTheServiceWillBeStarted()
    {
        ActivityController controller = Robolectric.buildActivity(MainActivity.class).create().start();
        MainActivity createdActivity = (MainActivity) controller.get();
        controller.resume();
        ShadowActivity shadowActivity = Shadows.shadowOf(createdActivity);
        Intent startedIntent = shadowActivity.getNextStartedService();
        assertNotNull(startedIntent);
    }

    @Test
    public void whenBroadcastOfStepsIsReceivedThenStepsAreDisplayed()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        Intent broadcastSteps = new Intent();
        broadcastSteps.setAction(StepService.ACTION_STEPS_OCCURRED);
        float expected = 45;
        broadcastSteps.putExtra(StepService.STEPS_OCCURRED, expected);
        CreatedActivity.sendBroadcast(broadcastSteps);

        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("45"));
    }
}
