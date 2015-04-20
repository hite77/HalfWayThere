import org.junit.Test;

import static hiteware.com.halfwaythere.Conversion.distanceInMiles;
import static org.junit.Assert.assertEquals;

public class ConversionTest
{
    @Test
    public void DistanceCalculationTest()
    {
        double calculatedDistance = distanceInMiles(40.11826553,-83.07609974, 40.11768727,-83.08036353);
        assertEquals("Distance Calculated Correctly", 0.228, calculatedDistance, 0.001);
    }
}