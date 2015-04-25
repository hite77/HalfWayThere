import android.location.LocationManager;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import hiteware.com.halfwaythere.MainActivityFragment;

/**
 * Created by jasonhite on 4/25/15.
 */
@Module(
        injects = {LocationHandlerUnitTest.class, MainActivityFragment.class},
        overrides = true
)
public class TestModule {
    @Provides
    @Singleton
    LocationManager provideLocationManager() {
        return Mockito.mock(LocationManager.class);
    }
}