package hiteware.com.halfwaythere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private Handler mHandler;

    private statusReceiver mStatusReceiver = new statusReceiver();
    private QuickDialogUtility quickDialogUtility;

    private float currentSteps;
    private int detectedSteps;
    private float goalSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        quickDialogUtility = new QuickDialogUtility();

        Button startButton = (Button)findViewById(R.id.button);
        Button stopButton = (Button)findViewById(R.id.button2);

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);

        mHandler = new Handler();
    }

    void updateStatus()
    {
            ((TextView) findViewById(R.id.GoodText)).setText(String.format("%.0f", currentSteps));
            ((TextView) findViewById(R.id.DetectedSteps)).setText("Detected Steps:" + detectedSteps);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mStatusReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("halfWayThere.stepsOccurred");
        filter.addAction("halfWayThere.stepDetector");

        registerReceiver(mStatusReceiver, filter);
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

    public void setSteps(float newSteps) {
        this.currentSteps = newSteps;
        detectedSteps = (int)newSteps;
        updateStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Intent startIntent = new Intent(MainActivity.this, LocalService.class);
                startIntent.setAction(LocalService.STARTFOREGROUND_ACTION);
                startIntent.putExtra("goal", goalSteps);
                startIntent.putExtra("currentSteps", currentSteps);
                startService(startIntent);
                break;
            case R.id.button2:
                Intent stopIntent = new Intent(MainActivity.this, LocalService.class);
                stopIntent.setAction(LocalService.STOPFOREGROUND_ACTION);
                startService(stopIntent);
                break;
            default:
                break;
        }
    }

    public void SetGoalSteps(float goalSteps)
    {
        this.goalSteps = goalSteps;
    }

    private class statusReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals("halfWayThere.stepsOccurred")) {
                currentSteps = intent.getFloatExtra("steps", 0);
                updateStatus();
            }
            else if(intent.getAction().equals("halfWayThere.stepDetector")) {
                detectedSteps = intent.getIntExtra("steps", 0);
                updateStatus();
            }
        }
    }
}