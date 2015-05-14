package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.widget.EditText;

import javax.inject.Inject;

/**
 * Created by jasonhite on 4/30/15.
 */
public class QuickDialogUtility
{
    public float selectedSteps = 0;
    @Inject
    StepSensorChange stepSensorChange;

    public QuickDialogUtility(InjectableApplication application)
    {
        application.inject(this);
    }

    public void CollectCurrentSteps(Context context, final LocalService mService)
    {
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);


        new AlertDialog.Builder(context)
                .setTitle("Current Step Count")
                .setMessage("Enter Steps:")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        float countOfSteps = Float.valueOf(value.toString());
                        stepSensorChange.setNumberOfSteps(countOfSteps);
                        selectedSteps = countOfSteps;
                        mService.setSteps(selectedSteps);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }

    public void CollectGoalSteps(Context context)
    {
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(context)
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
    }
}
