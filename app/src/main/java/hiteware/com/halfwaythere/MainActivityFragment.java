package hiteware.com.halfwaythere;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import static hiteware.com.halfwaythere.Conversion.distanceInMiles;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LocationListener{

    private boolean firstLocation = false;
    private double distance = 0;
    private Location current_location;

    @Inject
    SensorManager sensorManager;

    @Inject LocationManager locationManager;

    public MainActivityFragment()
    {
    }

    public void initializeListeners()
    {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        List<String> allProviders = locationManager.getAllProviders();
        for (String provider : allProviders)
        {
            long minimumTimeBeforeNavigationUpdate = 6000;
            float minimumDistanceBeforeNavigationUpdate = 0;
            locationManager.requestLocationUpdates(provider, minimumTimeBeforeNavigationUpdate, minimumDistanceBeforeNavigationUpdate, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ((DemoApplication)getActivity().getApplication()).inject(getActivity(), this);

        initializeListeners();
        return view;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (firstLocation) {
            distance += distanceInMiles(location.getLatitude(), location.getLongitude(),
                                        current_location.getLatitude(), current_location.getLongitude());
            TextView outputView = (TextView) getView().findViewById(R.id.distance_value);
            outputView.setText(String.format( "%.2f", distance )+" miles");
        }
        current_location = location;
        firstLocation = true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
