package hiteware.com.halfwaythere;

import android.location.LocationListener;
import android.location.LocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jasonhite on 4/25/15.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class LocationHandlerUnitTest
{

    @Inject
    LocationManager locationManager;
    public MainActivity CreatedActivity;
    public TestDemoApplication application;

    @Before
    public void setUp() {
        application = (TestDemoApplication) RuntimeEnvironment.application;
        application.setMockLocationManager();
        application.inject(null, this);
    }

    @Test
    public void mockProviderIsCalledForRegisterWithOneProviderWithSixSecondDelayZeroDistanceNeeded()
    {
        String provider = new String("Hello");
        when(locationManager.getAllProviders()).thenReturn(Arrays.asList(provider));

        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().get();

        long time = 6000;
        float distance = 0;

        verify(locationManager, times(1)).requestLocationUpdates(eq(provider), eq(time), eq(distance), isA(LocationListener.class));
    }

    @Test
    public void mockProviderIsCalledForRegisterWithMultipleProviders()
    {
        List<String> providers = Arrays.asList("First", "Second");
        when(locationManager.getAllProviders()).thenReturn(providers);

        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().get();

        verify(locationManager, times(1)).requestLocationUpdates(eq(providers.get(0)), anyLong(), anyFloat(), isA(LocationListener.class));
        verify(locationManager, times(1)).requestLocationUpdates(eq(providers.get(1)), anyLong(), anyFloat(), isA(LocationListener.class));
    }
}