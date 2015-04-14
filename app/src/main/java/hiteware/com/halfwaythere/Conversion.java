package hiteware.com.halfwaythere;

/**
 * Created by jasonhite on 4/13/15.
 */
public class Conversion
{
    public static double distanceInMiles(double lat1, double long1, double lat2, double long2) {
        double piDividedBy180 = 0.0174532925199433;
        double deltaLongitudeInRadians = (long2 - long1) * piDividedBy180;
        double deltaLatitudeInRadians = (lat2 - lat1) * piDividedBy180;
        double a = Math.pow(Math.sin(deltaLatitudeInRadians/2.0), 2) + Math.cos(lat1 * piDividedBy180) *
                Math.cos(lat2 * piDividedBy180) * Math.pow(Math.sin(deltaLongitudeInRadians/2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return 3956 * c;
    }
}
