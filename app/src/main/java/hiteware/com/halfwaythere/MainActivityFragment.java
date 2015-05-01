package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
        promptForSteps();
    }

    private void promptForSteps()
    {
        final EditText input = new EditText(getView().getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);


        new AlertDialog.Builder(getView().getContext())
                .setTitle("Current Step Count")
                .setMessage("Enter Steps:")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        float countOfSteps = Float.valueOf(value.toString());
                        stepSensorChange.setNumberOfSteps(countOfSteps);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ((InjectableApplication)getActivity().getApplication()).inject(this);

        return view;
    }
}
