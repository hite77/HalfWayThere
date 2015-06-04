package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowAlertDialog;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by jasonhite on 6/3/15.
 */
@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class DialogUtilityUnitTest {

    public MainActivity CreatedActivity;

    @Before
    public void Setup()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();
    }

    public void StartSetCurrentSteps()
    {
        MenuItem item = new RoboMenuItem(R.id.action_set_current_steps);
        CreatedActivity.onOptionsItemSelected(item);
    }

    public void StartSetGoalSteps()
    {
        MenuItem item = new RoboMenuItem(R.id.action_set_goal_steps);
        CreatedActivity.onOptionsItemSelected(item);
    }

    @Test
    public void whenMenuItemForStepsIsSelectedThenActionDialogForStepsIsShown() {
        StartSetCurrentSteps();

        assertNotNull(ShadowAlertDialog.getLatestAlertDialog());
    }

    @Test
    public void whenMenuItemForGoalIsSelectedThenActionDialogForGoalIsShown() {
        StartSetGoalSteps();

        assertNotNull(ShadowAlertDialog.getLatestAlertDialog());
    }

    @Test
    public void whenMenuItemForStepsIsSelectedThenActionDialogForStepsHasCorrectTitle() {
        StartSetCurrentSteps();

        ShadowAlertDialog shadow = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        assertThat(shadow.getTitle().toString(), equalTo(CreatedActivity.getString(R.string.set_current_steps_title)));
    }

    @Test
    public void whenMenuItemForStepsIsSelectedThenActionDialogHasCorrectMessage()
    {
        StartSetCurrentSteps();

        ShadowAlertDialog shadow = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        assertThat(shadow.getMessage().toString(), equalTo(CreatedActivity.getString(R.string.set_current_steps_message)));
    }

    @Test
    public void whenMenuItemForStepsIsSelectedThenActionDialogHasEditTextForNumbers()
    {
        StartSetCurrentSteps();

        ShadowAlertDialog shadow = shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        EditText editText = (EditText) shadow.getView();
        assertNotNull(editText);
        assertThat(editText.getInputType(), equalTo(InputType.TYPE_CLASS_NUMBER));
    }

    @Test
    public void whenStepDialogIsDisplayedThenThereIsAPositiveButton()
    {
        StartSetCurrentSteps();

        Button okButton = ShadowAlertDialog.getLatestAlertDialog().getButton(AlertDialog.BUTTON_POSITIVE);
        assertNotNull(okButton);
        assertTrue(okButton.getVisibility() == Button.VISIBLE);
        assertThat(okButton.getText().toString(), equalTo(CreatedActivity.getString(R.string.Ok)));
    }
}
