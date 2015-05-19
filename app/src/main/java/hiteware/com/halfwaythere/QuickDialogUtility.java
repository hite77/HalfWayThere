package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by jasonhite on 4/30/15.
 */
public class QuickDialogUtility
{
    public void CollectCurrentSteps(final MainActivity mainActivity)
    {
        final EditText input = new EditText(mainActivity);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);


        new AlertDialog.Builder(mainActivity)
                .setTitle("Current Step Count")
                .setMessage("Enter Steps:")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        float countOfSteps = Float.valueOf(value.toString());
                        mainActivity.setSteps(countOfSteps);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }

    public void CollectGoalSteps(final MainActivity mainActivity)
    {
        final EditText input = new EditText(mainActivity);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);


        new AlertDialog.Builder(mainActivity)
                .setTitle("Goal Steps")
                .setMessage("Enter Steps:")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        float goalSteps = Float.valueOf(value.toString());
                        mainActivity.SetGoalSteps(goalSteps);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();
    }
}
