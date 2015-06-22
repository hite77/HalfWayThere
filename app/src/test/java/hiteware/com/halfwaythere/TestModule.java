package hiteware.com.halfwaythere;

import android.hardware.SensorManager;
import android.os.Vibrator;

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
    private final ProgressUpdateInterface progressUpdateMock = Mockito.mock(ProgressUpdateInterface.class);
    private final Vibrator vibratorMock = Mockito.mock(Vibrator.class);

    @Provides
    public SensorManager provideSensorManager() { return sensorManager; }

    @Provides
    public ProgressUpdateInterface provideProgressUpdate() { return progressUpdateMock;}

    @Provides
    public Vibrator provideVibrator() { return vibratorMock;}
}