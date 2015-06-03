package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.util.ActivityController;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by jasonhite on 5/31/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityFragmentUnitTest {
    public MainActivity CreatedActivity;


    @Test
    public void whenTheAppIsRunningTheServiceWillBeStarted() {
        ActivityController controller = Robolectric.buildActivity(MainActivity.class).create().start();
        MainActivity createdActivity = (MainActivity) controller.get();
        controller.resume();
        ShadowActivity shadowActivity = shadowOf(createdActivity);
        Intent startedIntent = shadowActivity.getNextStartedService();
        assertNotNull(startedIntent);
    }

    @Test
    public void whenBroadcastOfStepsIsReceivedThenStepsAreDisplayed() {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        Intent broadcastSteps = new Intent();
        broadcastSteps.setAction(StepService.ACTION_STEPS_OCCURRED);
        float expected = 45;
        broadcastSteps.putExtra(StepService.STEPS_OCCURRED, expected);
        CreatedActivity.sendBroadcast(broadcastSteps);

        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("45"));
    }

    @Test
    public void whenActivityIsPausedItUnregistersReceiver() {
        ActivityController controller = Robolectric.buildActivity(MainActivity.class).create().start();
        CreatedActivity = (MainActivity) controller.get();

        controller.resume();

        List<ShadowApplication.Wrapper> registeredReceivers = ShadowApplication.getInstance().getRegisteredReceivers();
        assertThat(registeredReceivers.size(), equalTo(1));

        controller.pause();
        assertThat(ShadowApplication.getInstance().getRegisteredReceivers().size(), equalTo(0));
    }

    @Test
    public void whenMenuItemForStepsIsSelectedThenActionDialogForStepsIsShown() {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        MenuItem item = new RoboMenuItem(R.id.action_set_current_steps);
        CreatedActivity.onOptionsItemSelected(item);

        AlertDialog shadow = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(shadow);
    }

    @Test
    public void whenMenuItemForGoalIsSelectedThenActionDialogForGoalIsShown() {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        MenuItem item = new RoboMenuItem(R.id.action_set_goal_steps);
        CreatedActivity.onOptionsItemSelected(item);

        AlertDialog shadow = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(shadow);
    }

    @Test
    public void whenMenuItemForStepsIsSelectedThenActionDialogForStepsHasCorrectTitle() {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
        MenuItem item = new RoboMenuItem(R.id.action_set_current_steps);
        CreatedActivity.onOptionsItemSelected(item);

        ShadowAlertDialog shadow = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        assertThat(shadow.getTitle().toString(), equalTo(CreatedActivity.getString(R.string.set_current_steps_title)));
    }
}
