package hiteware.com.halfwaythere;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LocationListener {

    private Location latestLocation;
    private double distance = 0.0;
    private boolean first_entry = false;
    private List<String> locations = new Vector<>();

    public MainActivityFragment() {
    }

    private static double distanceInMiles(double lat1, double long1, double lat2, double long2) {
        double piDividedBy180 = 0.0174532925199433;
        double deltaLongitudeInRadians = (long2 - long1) * piDividedBy180;
        double deltaLatitudeInRadians = (lat2 - lat1) * piDividedBy180;
        double a = Math.pow(Math.sin(deltaLatitudeInRadians/2.0), 2) + Math.cos(lat1 * piDividedBy180) *
                Math.cos(lat2 * piDividedBy180) * Math.pow(Math.sin(deltaLongitudeInRadians/2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 3956 * c;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        if (view != null && view.findViewById(R.id.email_button) != null) {
            Button button = (Button) view.findViewById(R.id.email_button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Email Clicked", Toast.LENGTH_SHORT).show();
                    emailIntent();
                }
            });
        }

        if (view != null && view.findViewById(R.id.Clear_Button) != null)
        {
            Button button = (Button) view.findViewById(R.id.Clear_Button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Clear Clicked", Toast.LENGTH_SHORT).show();
                    locations.clear();
                    distance = 0.0;
                    first_entry = false;
                }
            });
        }

        initLocationListener();
        return view;
    }

    // spike code -- to email coordinates and estimated distance travelled
    // don't know what format is needed.
    // keep it simple -- get coordinates, store them, also clear them out when clear button pressed.

    private void emailIntent()
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"hite77@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");

        String output = new String();
        for (String line: locations)
        {
            output = output+ line;
        }
        i.putExtra(Intent.EXTRA_TEXT   , output);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initLocationListener() {
        LocationManager locationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> allProviders = locationManager.getAllProviders();
        for (String provider : allProviders) {
            locationManager.requestLocationUpdates(provider, 6000, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getAccuracy() < 20.1) {
            if (first_entry) {
                distance = distance + distanceInMiles(latestLocation.getLatitude(), latestLocation.getLongitude(),
                        location.getLatitude(), location.getLongitude());
                TextView distanceText = (TextView) getView().findViewById(R.id.distance_text);
                distanceText.setText(distance+" miles");
            }
            first_entry = true;
            latestLocation = location;

            String position = new String("Lat: " + location.getLatitude() + " Long: " + location.getLongitude() + " Accuracy: " + location.getAccuracy());
            String output = new String(location.getLatitude() + "," + location.getLongitude() + " distance:"+distance+"\n");
            TextView positionView = (TextView) getView().findViewById(R.id.textView3);
            positionView.setText(position);
            locations.add(output);
        }
    }

    public Location latestLocation() {
        return latestLocation;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}
    @Override
    public void onProviderDisabled(String s) {}
    @Override
    public void onProviderEnabled(String s) {}
}
