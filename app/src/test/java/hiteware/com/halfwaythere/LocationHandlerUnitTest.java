package hiteware.com.halfwaythere;

import android.location.LocationManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @Before
    public void setUp() {
        TestDemoApplication application = (TestDemoApplication) RuntimeEnvironment.application;
        application.setMockLocationManager();
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().get();
        application.inject(null, this);
    }

    @Test
    public void canMockTheLocationManager()
    {
//        LocationManager locationManager = mock(LocationManager.class);

//        locationManager.getAllProviders();
//        locationManager.getAllProviders();

        verify(locationManager, times(1)).getAllProviders();
    }
}