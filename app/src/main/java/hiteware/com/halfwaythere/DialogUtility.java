package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created on 6/3/15.
 */
class DialogUtility
{
        public void CollectCurrentSteps(final Context context)
    {
        final EditText input = new EditText(context);
        input.setContentDescription("StepsEditText");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(context)
                .setTitle(R.string.set_current_steps_title)
                .setMessage(R.string.set_current_steps_message)
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        int countOfSteps = Integer.parseInt(value.toString());
                        BroadcastHelper.sendBroadcast(context, StepService.ACTION_SET_STEPS, StepService.STEPS_OCCURRED, countOfSteps);
                    }})
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                    .show();
    }

    public void CollectGoalSteps(final Context context) {

        final EditText input = new EditText(context);
        input.setContentDescription("GoalEditText");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(context)
                .setTitle(R.string.set_goal_steps_title)
                .setMessage(R.string.set_goal_steps_message)
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        int goal = Integer.parseInt(value.toString());
                        BroadcastHelper.sendBroadcast(context, StepService.ACTION_GOAL_SET, StepService.GOAL_SET, goal);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }
}