/**
 * Created by jasonhite on 4/15/15.
 */

import android.widget.TextView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import hiteware.com.halfwaythere.BuildConfig;
import hiteware.com.halfwaythere.MainActivity;
import hiteware.com.halfwaythere.R;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;


@RunWith(CustomRobolectricRunner.class)
@Config(constants = BuildConfig.class)
public class RobolectricTest {
    @Test
    public void testIt() {
        // failing test gives much better feedback
        // to show that all works correctly ;)
        assertThat(RuntimeEnvironment.application, notNullValue());
    }
    @Test
    public void canFindDistanceText() {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
        TextView distance = (TextView) activity.findViewById(R.id.distance);
        assertThat(distance.getText().toString(), equalTo("Distance"));
    }
}