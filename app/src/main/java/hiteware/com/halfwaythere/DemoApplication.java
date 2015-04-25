package hiteware.com.halfwaythere;

import android.app.Application;
import android.location.LocationManager;

/**
 * Created by jasonhite on 4/23/15.
 */
public class DemoApplication extends Application {

    private LocationManager ActiveLocationManager = null;

    @Override public void onCreate() {
        super.onCreate();
    }

    public void setActiveLocationManager(LocationManager manager)
    {
        ActiveLocationManager = manager;
    }

    public LocationManager getActiveLocationManager()
    {
        return ActiveLocationManager;
    }
}

