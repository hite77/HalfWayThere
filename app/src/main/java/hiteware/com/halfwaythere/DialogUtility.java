package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created on 6/3/15.
 */
public class DialogUtility
{
        private void SetSteps(Context context, float value) {
            Intent broadcastSteps = new Intent();
            broadcastSteps.setAction(StepService.ACTION_SET_STEPS);
            broadcastSteps.putExtra(StepService.STEPS_OCCURRED, value);
            context.sendBroadcast(broadcastSteps);
        }

        public void CollectCurrentSteps(final Context context)
    {
        final EditText input = new EditText(context);
        input.setContentDescription("StepsEditText");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.set_current_steps_title))
                .setMessage(context.getString(R.string.set_current_steps_message))
                .setView(input)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        float countOfSteps = Float.valueOf(value.toString());
                        SetSteps(context, countOfSteps);
                }})
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                    .show();
    }
}


//    public float selectedSteps = 0;
//    @Inject
//    StepSensorChange stepSensorChange;
//
//    public QuickDialogUtility(InjectableApplication application)
//    {
//        application.inject(this);
//    }
//
//
//    public void CollectGoalSteps(Context context)
//    {
//        final EditText input = new EditText(context);
//        input.setInputType(InputType.TYPE_CLASS_NUMBER);
//
//        new AlertDialog.Builder(context)
//                .setTitle("Set Goal Step Count")
//                .setMessage("Enter Steps:")
//                .setView(input)
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        Editable value = input.getText();
//                        float goalSteps = Float.valueOf(value.toString());
//                        stepSensorChange.SetGoalSteps(goalSteps);
//                    }
//                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//            }
//        }).show();
//    }
//}
