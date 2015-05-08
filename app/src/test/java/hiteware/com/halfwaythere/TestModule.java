package hiteware.com.halfwaythere;

import android.hardware.SensorManager;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jasonhite on 4/25/15.
 */
@Module(
        includes = StepSensorChangeModule.class,
        injects = {MainActivity.class, SensorHandlerUnitTest.class, QuickDialogUtility.class},
        overrides = true
)
public class TestModule{

    @Provides @Singleton
    SensorManager provideSensorManager() { return Mockito.mock(SensorManager.class); }
}