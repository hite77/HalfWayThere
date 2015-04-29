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
        injects = {MainActivityFragment.class, SensorHandlerUnitTest.class},
        overrides = true
)
public class TestModule {
    public TestModule() {
    }

    @Provides @Singleton
    SensorManager provideSensorManager() { return Mockito.mock(SensorManager.class); }
}