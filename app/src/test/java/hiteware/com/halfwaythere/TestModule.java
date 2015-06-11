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
public class TestModule{
    public TestModule() {
    }

    private final SensorManager sensorManager = Mockito.mock(SensorManager.class);
    private final SoftwareStepCounterInterface softwareStepCounter = Mockito.mock(SoftwareStepCounterInterface.class);

    @Provides
    public SensorManager provideSensorManager() { return sensorManager; }

    @Provides
    public SoftwareStepCounterInterface provideSoftwareStepCounter() { return softwareStepCounter;}
}