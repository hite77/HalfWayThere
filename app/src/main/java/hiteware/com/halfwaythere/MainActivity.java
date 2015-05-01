package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import javax.inject.Inject;


public class MainActivity extends ActionBarActivity {

    @Inject
    StepSensorChange stepSensorChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((InjectableApplication)getApplication()).inject(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_set_goal_steps) {

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);

            new AlertDialog.Builder(this)
                    .setTitle("Set Goal Step Count")
                    .setMessage("Enter Steps:")
                    .setView(input)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Editable value = input.getText();
                            float goalSteps = Float.valueOf(value.toString());
                            stepSensorChange.SetGoalSteps(goalSteps);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            }).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
