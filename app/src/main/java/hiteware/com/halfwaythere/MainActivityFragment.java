package hiteware.com.halfwaythere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment{

    @Inject
    SensorManager sensorManager;

    @Inject
    StepService stepService;

    private statusReceiver mStatusReceiver = new statusReceiver();
    private float currentSteps;

    public MainActivityFragment()
    {
    }

    public void initializeListeners()
    {
        Sensor defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        TextView stepsText = (TextView) getView().findViewById(R.id.steps_title);
        stepService.setOutputView((TextView) getView().findViewById(R.id.step_value));

        if (defaultSensor==null)
        {
            stepsText.setText("Your device does not have Hardware Pedometer. Future versions of this software will have software pedometer and work with your device.");
        } else
        {
            stepsText.setText("Steps");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeListeners();

        IntentFilter filter = new IntentFilter();
        filter.addAction(stepService.ACTION_STEPS_OCCURRED);

        getView().getContext().registerReceiver(mStatusReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ((InjectableApplication)getActivity().getApplication()).inject(this);

        return view;
    }

    void updateStatus()
    {
        ((TextView) getView().findViewById(R.id.step_value)).setText(String.format("%.0f", currentSteps));
    }

    private class statusReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals("halfWayThere.stepsOccurred")) {
                currentSteps = intent.getFloatExtra(StepService.STEPS_OCCURRED, 0);
                updateStatus();
            }
        }
    }
}
