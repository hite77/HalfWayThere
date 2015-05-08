package hiteware.com.halfwaythere;

import android.hardware.SensorManager;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jasonhite on 4/25/15.
 */
@Module(
        includes = StepSensorChangeModule.class,
        injects = MainActivityFragment.class,
        overrides = true
)
public class TestModule{
    public TestModule() {
    }

    private SensorManager sensorManager = Mockito.mock(SensorManager.class);

    @Provides
    public SensorManager provideSensorManager() { return sensorManager; }
}