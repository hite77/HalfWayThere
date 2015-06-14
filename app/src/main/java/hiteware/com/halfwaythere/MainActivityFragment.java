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

public class MainActivityFragment extends Fragment{

    private final statusReceiver mStatusReceiver = new statusReceiver();
    private int currentSteps;
    private int goal;

    public MainActivityFragment()
    {
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(StepService.ACTION_STEPS_OCCURRED);
        filter.addAction(StepService.ACTION_GOAL_CHANGED);

        getActivity().registerReceiver(mStatusReceiver, filter);
        requestStepsFromService();
        requestGoalFromService();
    }

    private void requestStepsFromService() {
        Intent requestSteps = new Intent();
        requestSteps.setAction(StepService.ACTION_REQUEST_STEPS);
        getActivity().sendBroadcast(requestSteps);
    }

    private void requestGoalFromService() {
        Intent requestGoal = new Intent();
        requestGoal.setAction(StepService.ACTION_GOAL_REQUEST);
        getActivity().sendBroadcast(requestGoal);
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    private void updateStatus()
    {
        ((TextView) getActivity().findViewById(R.id.step_value)).setText(Integer.toString(currentSteps));
        ((TextView) getActivity().findViewById(R.id.goal_value)).setText(Integer.toString(goal));
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
        }
    }
}
