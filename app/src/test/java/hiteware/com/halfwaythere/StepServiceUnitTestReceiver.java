package hiteware.com.halfwaythere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowIntent;

/**
 * Created on 6/18/15.
 */
public class StepServiceUnitTestReceiver extends BroadcastReceiver {
    private int actualSteps = -1;
    private int actualGoal = -1;
    private int actualHalfWay = -1;
    private boolean clearHalfWay = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        ShadowIntent shadowIntent = Shadows.shadowOf(intent);
        if (shadowIntent.hasExtra(StepService.STEPS_OCCURRED))
        {
            actualSteps = shadowIntent.getIntExtra(
                    StepService.STEPS_OCCURRED, 0);
        }
        if (shadowIntent.hasExtra(StepService.GOAL_SET))
        {
            actualGoal = shadowIntent.getIntExtra(StepService.GOAL_SET, 0);
        }
        if (shadowIntent.hasExtra(StepService.HALF_WAY_VALUE))
        {
            actualHalfWay = shadowIntent.getIntExtra(StepService.HALF_WAY_VALUE, 0);
        }
        if (shadowIntent.getAction().equals(StepService.ACTION_CLEAR_HALF_WAY))
        {
            clearHalfWay = true;
        }
    }

    public int getActualSteps() { return actualSteps; }
    public int getActualGoal() { return actualGoal; }
    public int getActualHalfWay() { return actualHalfWay; }
    public boolean getClearedHalfWay() {return clearHalfWay; }
}