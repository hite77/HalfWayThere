package com.hitetech.core;

import org.junit.Test;

import static com.hitetech.core.core.distanceInMiles;
import static org.junit.Assert.assertEquals;

public class CoreTest
{
    @Test
    public void DistanceCalculationTest()
    {
        double calculatedDistance = distanceInMiles(40.11826553,-83.07609974, 40.11768727,-83.08036353);
        assertEquals("Distance Calculated Correctly", 0.228, calculatedDistance, 0.001);
    }
}