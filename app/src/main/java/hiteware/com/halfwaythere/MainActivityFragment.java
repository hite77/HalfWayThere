package hiteware.com.halfwaythere;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
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
public class MainActivityFragment extends Fragment implements SensorEventListener {

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
        stepSensorChange.setOutputView((TextView) getView().findViewById(R.id.distance_value));
        sensorManager.registerListener(stepSensorChange, defaultSensor, SensorManager.SENSOR_DELAY_UI);
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

        ((DemoApplication)getActivity().getApplication()).buildGraph();
        ((DemoApplication)getActivity().getApplication()).inject(this);

        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
