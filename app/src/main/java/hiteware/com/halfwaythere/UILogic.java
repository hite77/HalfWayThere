package hiteware.com.halfwaythere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * Created on 6/24/15.
 */
class UILogic {
    private final statusReceiver mStatusReceiver = new statusReceiver();
    private int currentSteps;
    private int goal;
    private final FragmentActivity Activity;
    private final View MyView;

    @Inject
    ProgressUpdateInterface mProgressUpdate;

    public UILogic(FragmentActivity activity, View view)
    {
        Activity = activity;
        MyView = view;
    }

    public void Setup()
    {
        ((InjectableApplication)Activity.getApplication()).inject(this);

        if (null != MyView && null != MyView.findViewById(R.id.circularProgressWithHalfWay)) {
            mProgressUpdate.SetCircularProgress((CircularProgressWithHalfWay) MyView.findViewById(R.id.circularProgressWithHalfWay));
        }

        SetupActionsOnReceiver();

        requestStepsFromService();
        requestGoalFromService();

        SetupHalfWayValueOnClick();
    }

    public void Pause() {
        Activity.unregisterReceiver(mStatusReceiver);
    }

    private void SetupActionsOnReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(StepService.ACTION_STEPS_OCCURRED);
        filter.addAction(StepService.ACTION_GOAL_CHANGED);
        filter.addAction(StepService.ACTION_CLEAR_HALF_WAY);
        filter.addAction(StepService.ACTION_HALF_WAY_SET);
        Activity.registerReceiver(mStatusReceiver, filter);
    }

    private void SetupHalfWayValueOnClick() {
        MyView.findViewById(R.id.HalfWayToggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int halfWayValue = currentSteps + (goal - currentSteps) / 2;
                boolean isOK = mProgressUpdate.SetHalfWay(halfWayValue);

                if (isOK) {
                    BroadcastHelper.sendBroadcast(Activity, StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, halfWayValue);
                    ((TextView) Activity.findViewById(R.id.HalfWayValue)).setText(Integer.toString(halfWayValue));
                } else {
                    ((TextView) Activity.findViewById(R.id.HalfWayValue)).setText("");
                    mProgressUpdate.ClearHalfWay();
                }
            }
        });
    }

    private void requestStepsFromService() {
        BroadcastHelper.sendBroadcast(Activity, StepService.ACTION_REQUEST_STEPS);
    }

    private void requestGoalFromService() {
        BroadcastHelper.sendBroadcast(Activity, StepService.ACTION_GOAL_REQUEST);
    }

    private void updateStatus()
    {
        ((TextView) Activity.findViewById(R.id.step_value)).setText(Integer.toString(currentSteps));
        mProgressUpdate.SetSteps(currentSteps);
        ((TextView) Activity.findViewById(R.id.goal_value)).setText(Integer.toString(goal));
        mProgressUpdate.SetGoal(goal);
    }

    private class statusReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (intent.getAction())
            {
                case StepService.ACTION_STEPS_OCCURRED:
                    currentSteps = intent.getIntExtra(StepService.STEPS_OCCURRED, 0);
                    updateStatus();
                    break;
                case StepService.ACTION_GOAL_CHANGED:
                    goal = intent.getIntExtra(StepService.GOAL_SET, 0);
                    updateStatus();
                    break;
                case StepService.ACTION_CLEAR_HALF_WAY:
                    mProgressUpdate.ClearHalfWay();
                    ((TextView) Activity.findViewById(R.id.HalfWayValue)).setText("");
                    break;
                case StepService.ACTION_HALF_WAY_SET:
                    int halfWayValue = intent.getIntExtra(StepService.HALF_WAY_VALUE, -1);
                    ((TextView) Activity.findViewById(R.id.HalfWayValue)).setText(Integer.toString(halfWayValue));
                    mProgressUpdate.SetHalfWay(halfWayValue);
                    break;
            }
        }
    }
}
