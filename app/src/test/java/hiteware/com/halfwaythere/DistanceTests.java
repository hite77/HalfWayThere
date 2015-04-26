package hiteware.com.halfwaythere;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.robolectric.Shadows.shadowOf;

@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class DistanceTests {
    public Activity CreatedActivity;
    public TextView DistanceValue;
    public int TimeOffset;
    public ShadowLocationManager MyShadowLocationManager;
    @Before
    public void setUp() {
        CreatedActivity = Robolectric.buildActivity(MainActivity.class).create().get();
        DistanceValue = (TextView) CreatedActivity.findViewById(R.id.distance_value);
        LocationManager locationManager = (LocationManager) RuntimeEnvironment.application.getSystemService(Application.LOCATION_SERVICE);
        MyShadowLocationManager = shadowOf(locationManager);

        TimeOffset = 0;
    }

    private Location location(String provider, double latitude, double longitude) {
        Location location = new Location(provider);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(System.currentTimeMillis());
        return location;
    }

    public void simulateLocationWithTimeOffset(Location location)
    {
        location.setTime(location.getTime()+TimeOffset);
        TimeOffset+= 8000;
        MyShadowLocationManager.simulateLocation(location);
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
    public void whenOneLocationHasBeenArrivedAtTheStringIsStillEmptyForDistance()
    {
        simulateLocationWithTimeOffset(location(LocationManager.NETWORK_PROVIDER, 39.9833, -82.9833));
        assertThat(DistanceValue.getText().toString(), equalTo(""));
    }

    @Test
    public void distanceBetweenTwoPointsResultsInCorrectDistanceDisplayed()
    {
        simulateLocationWithTimeOffset(location(LocationManager.NETWORK_PROVIDER, 39.9833, -82.9833));
        simulateLocationWithTimeOffset(location(LocationManager.NETWORK_PROVIDER, 38.2500, -85.7667));
        assertThat(DistanceValue.getText().toString(), equalTo("191.18 miles"));
    }

    @Test
    public void distanceBetweenMultiplePlacesResultsInCorrectDistanceDisplayed()
    {
        simulateLocationWithTimeOffset(location(LocationManager.NETWORK_PROVIDER, 39.9833, -82.9833));
        simulateLocationWithTimeOffset(location(LocationManager.NETWORK_PROVIDER, 38.2500, -85.7667));
        simulateLocationWithTimeOffset(location(LocationManager.NETWORK_PROVIDER, 36.1215, -115.1739));
        assertThat(DistanceValue.getText().toString(), equalTo("1808.62 miles"));
    }
}