package hiteware.com.halfwaythere;

import android.content.Context;
import android.hardware.SensorManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jasonhite on 4/25/15.
 */
@Module(
        includes = StepServiceModule.class,
        injects = MainActivityFragment.class,
        overrides = false
)
public class ProductionModule {

    private Context Activity;

    public ProductionModule()
    {

    }

    public ProductionModule(Context activity) {
        Activity = activity;
    }

    @Provides @Singleton SensorManager provideSensorManager() { // note to self: should this be a singleton for the real thing?
        return (SensorManager) Activity.getSystemService(Context.SENSOR_SERVICE);
    }
}
