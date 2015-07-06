package hiteware.com.halfwaythere;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Vibrator;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created on 4/25/15.
 */
@Module(
        injects = {StepService.class, UILogic.class},
        overrides = false
)
class ProductionModule {

    private final Context Activity;

    public ProductionModule(Context activity) {
        Activity = activity;
    }

    @Provides @Singleton SensorManager provideSensorManager() {
        return (SensorManager) Activity.getSystemService(Context.SENSOR_SERVICE);
    }

    @Provides @Singleton ProgressUpdateInterface provideProgressUpdate() {
        return new ProgressUpdate();
    }

    @Provides @Singleton Vibrator provideVibrator() {
        return (Vibrator) Activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Provides @Singleton Calendar provideCalendar() {
        return new GregorianCalendar();
    }
}
