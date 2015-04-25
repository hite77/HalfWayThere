package hiteware.com.halfwaythere;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

import static hiteware.com.halfwaythere.Conversion.distanceInMiles;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LocationListener{

    private boolean firstLocation = false;
    private double distance = 0;
    private Location current_location;

    @Inject LocationManager locationManager;

    @Module(
            injects = MainActivityFragment.class,
            overrides = false
    )
    static class ProductionModule {

        private FragmentActivity Activity;

        public ProductionModule(FragmentActivity activity) {
            Activity = activity;
        }

        @Provides
        @Singleton
        LocationManager provideLocationManager() {
            return (LocationManager) Activity.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public MainActivityFragment()
    {
    }

    public void initializeListeners()
    {
        //LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> allProviders = locationManager.getAllProviders();
//        locationManager.getAllProviders(); // second call to hopefully cause errors.
        for (String provider : allProviders)
        {
            locationManager.requestLocationUpdates(provider, 6000, 0, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ObjectGraph.create(new ProductionModule(getActivity())).inject(this);

        DemoApplication application = (DemoApplication) getActivity().getApplication();

        if (application.getActiveLocationManager() != null)
            locationManager = application.getActiveLocationManager();
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
