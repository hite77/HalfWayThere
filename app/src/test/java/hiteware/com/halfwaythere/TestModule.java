package hiteware.com.halfwaythere;

import android.hardware.SensorManager;
import android.os.Vibrator;

import org.mockito.Mockito;

import java.util.Calendar;

import dagger.Module;
import dagger.Provides;

/**
 * Created on 4/25/15.
 */
@Module
class TestModule{
    public TestModule() {
    }

    private final SensorManager sensorManager = Mockito.mock(SensorManager.class);
    private final ProgressUpdateInterface progressUpdateMock = Mockito.mock(ProgressUpdateInterface.class);
    private final Vibrator vibratorMock = Mockito.mock(Vibrator.class);
    private final Calendar calendarMock = Mockito.mock(Calendar.class);

    @Provides
    public SensorManager provideSensorManager() { return sensorManager; }

    @Provides
    public ProgressUpdateInterface provideProgressUpdate() { return progressUpdateMock;}

    @Provides
    public Vibrator provideVibrator() { return vibratorMock;}

    @Provides
    public Calendar provideCalendar() { return calendarMock;}
}