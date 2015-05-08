package hiteware.com.halfwaythere;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

/**
 * Created by jasonhite on 5/8/15.
 */
public class SoftwareStepSensor implements SensorEventListener
{
    TextView softWareSteps;

    public SoftwareStepSensor(TextView softWareSteps)
    {
        this.softWareSteps = softWareSteps;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        You can estimate the gravity force on the phone using the x,y,z values...
//
//        float g = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
//        ...here a value of 1 = normal (1g is normal)
//
//        The a crude pedometer can be built fairly easily by just counting how many peaks above a specified g value over a given
// sample period (eg 6 seconds and multiple by 10 for paces per minute)
//
//        Say for example record the time in ms that a g of >2 is recorded... then the peak will continue going up.... and come back
// down below 2.. probably to 0.5 or something.. then it'll go up again >2... at this point stop the clock.
//
//        ...then you have a complete cycle timed!
//
//                To stabilise the result it's better to count a few cycles.
//

//        Also .... need to use the z value -- which should be values[2] and how to register the listener.
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER){
//                return;
//            }
//            final float z = smooth(event.values[2]); // scalar kalman filter
//            if (Math.abs(z - mLastZ) > LEG_THRSHOLD_AMPLITUDE)
//            {
//                mInactivityCount = 0;
//                int currentActivity = (z > mLastZ) ? LEG_MOVEMENT_FORWARD : LEG_MOVEMENT_BACKWARD;
//                if (currentActivity != mLastActivity){
//                    mLastActivity = currentActivity;
//                    notifyListeners(currentActivity);
//                }
//            } else {
//                if (mInactivityCount > LEG_THRSHOLD_INACTIVITY) {
//                    if (mLastActivity != LEG_MOVEMENT_NONE){
//                        mLastActivity = LEG_MOVEMENT_NONE;
//                        notifyListeners(LEG_MOVEMENT_NONE);
//                    }
//                } else {
//                    mInactivityCount++;
//                }
//            }
//            mLastZ = z;
//        }


        float x = event.values[0];

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
