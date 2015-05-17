package hiteware.com.halfwaythere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity  {

    private Handler mHandler;

    private statusReceiver mStatusReceiver = new statusReceiver();
    private QuickDialogUtility quickDialogUtility;

    private float offset;
    private float currentSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        quickDialogUtility = new QuickDialogUtility();

        mHandler = new Handler();
    }

    void updateStatus()
    {
            ((TextView) findViewById(R.id.GoodText)).setText(String.format("%.0f", currentSteps));
            ((TextView) findViewById(R.id.Offset)).setText(String.format("%.0f", offset));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mStatusReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
        offset = prefs.getFloat("offset", 0);

        IntentFilter filter = new IntentFilter();
        filter.addAction("halfWayThere.stepsOccurred");

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
            return true;
        }

        if (id == R.id.action_set_current_steps)
        {
            quickDialogUtility.CollectCurrentSteps(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Intent intent = new Intent(this, LocalService.class);
        startService(intent);
    }

    public void setSteps(float newSteps) {
        this.offset = newSteps - this.currentSteps + this.offset;
        SharedPreferences prefs = getSharedPreferences("hiteware.com.halfwaythere", MODE_PRIVATE);
        prefs.edit().putFloat("offset", this.offset).apply();
        this.currentSteps = newSteps;
        updateStatus();
    }

    private class statusReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals("halfWayThere.stepsOccurred")) {
                currentSteps = intent.getFloatExtra("steps", 0) + offset;
                updateStatus();
            }
        }
    }
}