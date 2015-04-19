import android.app.Activity;
import android.app.Application;
import android.location.Location;
import android.location.LocationManager;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import hiteware.com.halfwaythere.BuildConfig;
import hiteware.com.halfwaythere.MainActivity;
import hiteware.com.halfwaythere.R;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.robolectric.Shadows.shadowOf;

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class DistanceTests {
    public Activity CreatedActivity;
    public TextView DistanceValue;
    @Before
    public void setUp() {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().get();
        DistanceValue = (TextView) CreatedActivity.findViewById(R.id.distance_value);
    }

    @Test
    public void canFindDistanceText() {
        TextView distance = (TextView) CreatedActivity.findViewById(R.id.distance);
        assertThat(distance.getText().toString(), equalTo("Distance"));
    }

    @Test
    public void whenNoDistancesReceivedThenTheStringIsEmptyForDistance()
    {
        assertThat(DistanceValue.getText().toString(), equalTo(""));
    }

    @Test
    public void distanceBetweenTwoPointsResultsInCorrectDistanceDisplayed()
    {
        LocationManager locationManager = (LocationManager) RuntimeEnvironment.application.getSystemService(Application.LOCATION_SERVICE);
        ShadowLocationManager shadowLocationManager = shadowOf(locationManager);
        Location firstLocation = location(LocationManager.NETWORK_PROVIDER, 39.9833, -82.9833);
        Location secondLocation = location(LocationManager.NETWORK_PROVIDER, 38.2500, -85.7667);

        shadowLocationManager.simulateLocation(firstLocation);
        shadowLocationManager.simulateLocation(secondLocation);
        assertThat(DistanceValue.getText().toString(), equalTo("191.48 miles"));
    }

    private Location location(String provider, double latitude, double longitude) {
        Location location = new Location(provider);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(System.currentTimeMillis());
        return location;
    }
}