package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.ActivityController;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by jasonhite on 6/3/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class DialogUtilityIntegrationTest {

    public MainActivity CreatedActivity;

    @Before
    public void Setup()
    {
        ActivityController controller = Robolectric.buildActivity(MainActivity.class).create().start();
        CreatedActivity = (MainActivity) controller.get();
        controller.resume();
    }

    public void SetCurrentSteps(float steps)
    {
        MenuItem item = new RoboMenuItem(R.id.action_set_current_steps);
        CreatedActivity.onOptionsItemSelected(item);

        ShadowAlertDialog shadow = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        EditText editText = (EditText) shadow.getView();
        editText.setText(Float.toString(steps));

        Button okButton = ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.performClick();
    }

    @Test
    public void WhenStepsAreSetInTheDialogThenTheStepsAreDisplayedOnScreen()
    {
        StepService mStepService = new StepService();
        mStepService.onCreate();

        float expected = 66;
        SetCurrentSteps(expected);
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("66"));
    }
}
