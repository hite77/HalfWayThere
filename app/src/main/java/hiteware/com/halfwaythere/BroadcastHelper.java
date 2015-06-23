package hiteware.com.halfwaythere;

import android.content.Context;
import android.content.Intent;

/**
 * Created on 6/19/15.
 */
class BroadcastHelper {
    public static void sendBroadcast(Context context, String action, String extra, int value) {
        Intent broadcast = new Intent();
        broadcast.setAction(action);
        broadcast.putExtra(extra, value);
        context.sendBroadcast(broadcast);
    }

    public static void sendBroadcast(Context context, String action) {
        Intent broadcast = new Intent();
        broadcast.setAction(action);
        context.sendBroadcast(broadcast);
    }
}
