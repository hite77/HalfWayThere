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

import javax.inject.Inject;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements SensorEventListener {

    @Inject
    SensorManager sensorManager;

    public MainActivityFragment()
    {
    }

    public void initializeListeners()
    {
        Sensor defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ((DemoApplication)getActivity().getApplication()).inject(getActivity(), this);

        initializeListeners();
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
