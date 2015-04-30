package hiteware.com.halfwaythere;

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
    StepSensorChange stepSensorChange;

    public MainActivityFragment()
    {
    }

    public void initializeListeners()
    {
        Sensor defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        TextView stepsText = (TextView) getView().findViewById(R.id.steps_title);
        stepSensorChange.setOutputView((TextView) getView().findViewById(R.id.step_value));

        if (defaultSensor==null)
        {
            stepsText.setText("Your device does not have Hardware Pedometer. Future versions of this software will have software pedometer and work with your device.");
        }
        else
        {
            stepsText.setText("Steps");
            sensorManager.registerListener(stepSensorChange, defaultSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeListeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ((DemoApplication)getActivity().getApplication()).inject(this);

        return view;
    }
}
