package hiteware.com.halfwaythere;

import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.ActivityController;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created on 5/31/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class MainActivityFragmentUnitTest {
    private MainActivity CreatedActivity;

    private void sendBroadcast(String action, String extra, int value) {
        Intent broadcast = new Intent();
        broadcast.setAction(action);
        broadcast.putExtra(extra, value);
        CreatedActivity.sendBroadcast(broadcast);
    }

    @Test
    public void whenTheAppIsRunningTheServiceWillBeStarted() {
        ActivityController controller = Robolectric.buildActivity(MainActivity.class).create().start();
        MainActivity createdActivity = (MainActivity) controller.get();
        controller.start();
        ShadowActivity shadowActivity = shadowOf(createdActivity);
        Intent startedIntent = shadowActivity.getNextStartedService();
        assertNotNull(startedIntent);
    }

    @Test
    public void whenBroadcastOfStepsIsReceivedThenStepsAreDisplayed() {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        sendBroadcast(StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, 45);

        assertThat(((TextView) CreatedActivity.findViewById(R.id.step_value)).getText().toString(), equalTo("45"));
    }

    @Test
    public void whenBroadcastOfStepsAndGoalAreReceivedThenProgressIsUpdated() {
        ((TestInjectableApplication) RuntimeEnvironment.application).setMock();

        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        int expected = 45;
        int expectedGoal = 14000;
        sendBroadcast(StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, expected);
        sendBroadcast(StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, expectedGoal);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        View expectedCircularProgress = CreatedActivity.findViewById(R.id.circularProgressWithHalfWay);

        ProgressUpdateInterface progressUpdate = ((TestInjectableApplication) RuntimeEnvironment.application).testModule.provideProgressUpdate();
        verify(progressUpdate, times(1)).SetCircularProgress((CircularProgressWithHalfWay) expectedCircularProgress);
        verify(progressUpdate, atLeastOnce()).SetSteps(expected);
        verify(progressUpdate, atLeastOnce()).SetGoal(expectedGoal);
    }

    @Test
    public void whenBroadcastOfGoalIsReceivedThenGoalIsDisplayed() {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().postResume().get();

        sendBroadcast(StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, 14000);

        assertThat(((TextView) CreatedActivity.findViewById(R.id.goal_value)).getText().toString(), equalTo("14000"));
    }

    @Test
    public void whenHalfWayButtonIsClickedThenBroadcastIsSentToService()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        sendBroadcast(StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, 1000);
        sendBroadcast(StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, 14000);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        StepServiceUnitTestReceiver testReceiver = new StepServiceUnitTestReceiver();
        CreatedActivity.registerReceiver(testReceiver, new IntentFilter(StepService.ACTION_HALF_WAY_SET));

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        assertThat(testReceiver.getActualHalfWay(), equalTo(7500));
    }

    @Test
    public void whenHalfWayButtonIsClickedThenProgressUpdateIsUpdated()
    {
        ((TestInjectableApplication) RuntimeEnvironment.application).setMock();
        ProgressUpdateInterface progressUpdate = ((TestInjectableApplication) RuntimeEnvironment.application).testModule.provideProgressUpdate();

        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        sendBroadcast(StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, 4653);
        sendBroadcast(StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, 5778);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        verify(progressUpdate, atLeastOnce()).SetHalfWay(5215);
    }

    @Test
    public void whenHalfWayButtonIsClickedThenCountIsUpdatedForHalfWay()
    {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().start().resume().postResume().get();

        sendBroadcast(StepService.ACTION_STEPS_OCCURRED, StepService.STEPS_OCCURRED, 1333);
        sendBroadcast(StepService.ACTION_GOAL_CHANGED, StepService.GOAL_SET, 2000);

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        CreatedActivity.findViewById(R.id.HalfWayToggle).performClick();

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();

        TextView halfWayValue = (TextView) CreatedActivity.findViewById(R.id.HalfWayValue);

        assertThat(halfWayValue.getText().toString(), equalTo("1666"));
    }

//    @Test //TODO: whenHalfWayButtonIsClickedAndIsInvalidFromProgressUpdateThenNoBroadcast
//    public void whenHalfWayButtonIsClickedAndIsInvalidFromProgressUpdateThenNoBroadcast()
//    {
//
//    }
//
//    @Test //TODO: whenServiceBroadcastsAClearThenTextIsClearedAndProgressUpdateIsCleared
//    public void whenServiceBroadcastsAClearThenTextIsClearedAndProgressUpdateIsCleared()
//    {
//
//    }

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
}
