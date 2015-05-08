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
        includes = StepSensorChangeModule.class,
        injects = {MainActivity.class, QuickDialogUtility.class},
        overrides = false
)
public class ProductionModule {

    private Context Activity;

    public ProductionModule(Context activity) {
        Activity = activity;
    }

    @Provides @Singleton SensorManager provideSensorManager() {
        return (SensorManager) Activity.getSystemService(Context.SENSOR_SERVICE);
    }
}
