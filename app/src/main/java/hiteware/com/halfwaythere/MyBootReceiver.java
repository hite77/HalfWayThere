package hiteware.com.halfwaythere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by jasonhite on 5/12/15.
 */
public class MyBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.i("MyService", "System Boot Complete broadcast received.");
        Toast.makeText(context, "initialBoot Complete Jason", Toast.LENGTH_LONG).show();
        /**
         * Starts the LocalService service.
         */
              Intent mIntent = new Intent(context, LocalService.class);
              context.startService(mIntent);
    }

}