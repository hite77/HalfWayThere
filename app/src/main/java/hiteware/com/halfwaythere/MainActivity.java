package hiteware.com.halfwaythere;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import javax.inject.Inject;


public class MainActivity extends ActionBarActivity {

    @Inject
    SensorManager sensorManager;

    @Inject
    StepSensorChange stepSensorChange;

    private QuickDialogUtility quickDialogUtility;
    private InjectableApplication Application;

    public void initializeListeners()
    {
        Sensor defaultSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        TextView stepsText = (TextView) findViewById(R.id.steps_title);
        stepSensorChange.setOutputView((TextView) findViewById(R.id.step_value));
        stepSensorChange.setgoalStepsView((TextView) findViewById(R.id.goalStepValue));
        stepSensorChange.setprogressView((CircularProgressBar) findViewById(R.id.circle_status));

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Application = (InjectableApplication) getApplication();
        quickDialogUtility = new QuickDialogUtility(Application);

        Application.inject(this);

        initializeListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_set_goal_steps)
        {
            quickDialogUtility.CollectGoalSteps(this);
            return true;
        }

        if (id == R.id.action_set_current_steps)
        {
            quickDialogUtility.CollectCurrentSteps(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
