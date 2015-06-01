package hiteware.com.halfwaythere;

import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by jasonhite on 5/31/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityFragmentUnitTest
{
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
}
