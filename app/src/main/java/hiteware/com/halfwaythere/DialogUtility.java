package hiteware.com.halfwaythere;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by jasonhite on 6/3/15.
 */
public class DialogUtility
{
        public void CollectCurrentSteps(Context context)
    {
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.set_current_steps_title))
                .setMessage(context.getString(R.string.set_current_steps_message))
                .setView(input)
                .setPositiveButton(context.getString(R.string.Ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//                        Editable value = input.getText();
//                        float countOfSteps = Float.valueOf(value.toString());
////                        stepSensorChange.setNumberOfSteps(countOfSteps);
//                        selectedSteps = countOfSteps;
//                        mService.setSteps(selectedSteps);
                }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}})
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
