package hiteware.com.halfwaythere;

import android.hardware.SensorManager;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;

/**
 * Created on 4/25/15.
 */
@Module(
        injects = {MainActivityFragment.class, StepService.class},
        overrides = true
)
class TestModule{
    public TestModule() {
    }

    private final SensorManager sensorManager = Mockito.mock(SensorManager.class);

    @Provides
    public SensorManager provideSensorManager() { return sensorManager; }
}