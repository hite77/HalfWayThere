package hiteware.com.halfwaythere;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private QuickDialogUtility quickDialogUtility;
    private InjectableApplication Application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Application = (InjectableApplication) getApplication();
        quickDialogUtility = new QuickDialogUtility(Application);
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
