package hiteware.com.halfwaythere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

public class MainActivityFragment extends Fragment{

    private final statusReceiver mStatusReceiver = new statusReceiver();
    private int currentSteps;
    private int goal;

    @Inject
    ProgressUpdateInterface mProgressUpdate;

    public MainActivityFragment()
    {
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(StepService.ACTION_STEPS_OCCURRED);
        filter.addAction(StepService.ACTION_GOAL_CHANGED);
        filter.addAction(StepService.ACTION_CLEAR_HALF_WAY);

        ((InjectableApplication)getActivity().getApplication()).inject(this);

        if (null != getView() && null != getView().findViewById(R.id.circularProgressWithHalfWay)) {
            mProgressUpdate.SetCircularProgress((CircularProgressWithHalfWay) getView().findViewById(R.id.circularProgressWithHalfWay));
        }

        getActivity().registerReceiver(mStatusReceiver, filter);
        requestStepsFromService();
        requestGoalFromService();

        getView().findViewById(R.id.HalfWayToggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int halfWayValue = currentSteps + (goal - currentSteps) / 2;
                boolean isOK = mProgressUpdate.SetHalfWay(halfWayValue);

                if (isOK) {
                    BroadcastHelper.sendBroadcast(getActivity(), StepService.ACTION_HALF_WAY_SET, StepService.HALF_WAY_VALUE, halfWayValue);
                    ((TextView)getActivity().findViewById(R.id.HalfWayValue)).setText(Integer.toString(halfWayValue));
                }
                else
                {
                    ((TextView)getActivity().findViewById(R.id.HalfWayValue)).setText("");
                    mProgressUpdate.ClearHalfWay();
                }
            }
        });

    }

    private void requestStepsFromService() {
        BroadcastHelper.sendBroadcast(getActivity(), StepService.ACTION_REQUEST_STEPS);
    }

    private void requestGoalFromService() {
        BroadcastHelper.sendBroadcast(getActivity(), StepService.ACTION_GOAL_REQUEST);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mStatusReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    private void updateStatus()
    {
        ((TextView) getActivity().findViewById(R.id.step_value)).setText(Integer.toString(currentSteps));
        mProgressUpdate.SetSteps(currentSteps);
        ((TextView) getActivity().findViewById(R.id.goal_value)).setText(Integer.toString(goal));
        mProgressUpdate.SetGoal(goal);
    }

    private class statusReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(StepService.ACTION_STEPS_OCCURRED)) {
                currentSteps = intent.getIntExtra(StepService.STEPS_OCCURRED, 0);
                updateStatus();
            }
            else if(intent.getAction().equals(StepService.ACTION_GOAL_CHANGED)) {
                goal = intent.getIntExtra(StepService.GOAL_SET, 0);
                updateStatus();
            }
            else if(intent.getAction().equals(StepService.ACTION_CLEAR_HALF_WAY)) {
                mProgressUpdate.ClearHalfWay();
            }
        }
    }
}
