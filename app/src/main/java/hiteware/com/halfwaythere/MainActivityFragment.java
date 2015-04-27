package hiteware.com.halfwaythere;

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

import javax.inject.Inject;

import static hiteware.com.halfwaythere.Conversion.distanceInMiles;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LocationListener{

    private List<String> locations = new Vector<>();

    private boolean firstLocation = false;
    private double distance = 0;
    private Location current_location;

    @Inject LocationManager locationManager;

    public MainActivityFragment()
    {
    }

    public void initializeListeners()
    {
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 6000, 0, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ((DemoApplication)getActivity().getApplication()).inject(getActivity(), this);

        initializeListeners();

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
                }
            });
        }

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


    @Override
    public void onLocationChanged(Location location) {

        if ((location.getAccuracy() < 20.1) && (location.getSpeed() >= 0.01)) {

            if (firstLocation) {
                distance += distanceInMiles(location.getLatitude(), location.getLongitude(),
                        current_location.getLatitude(), current_location.getLongitude());
                TextView outputView = (TextView) getView().findViewById(R.id.distance_value);
                outputView.setText(String.format("%.2f", distance) + " miles");

                float speed = location.getSpeed();
                TextView speedView = (TextView) getView().findViewById(R.id.SpikeSpeedText);
                speedView.setText(String.format("%.2f", speed));

                String output = new String(location.getLatitude() + "," + location.getLongitude() + " distance:" + distance + " speed:" + speed + "\n");
                locations.add(output);

                ((TextView) getView().findViewById(R.id.SpikeLatText)).setText(String.format("%.6f", location.getLatitude()));
                ((TextView) getView().findViewById(R.id.SpikeLongText)).setText(String.format("%.6f", location.getLongitude()));

            }

            current_location = location;
            firstLocation = true;
        }
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
