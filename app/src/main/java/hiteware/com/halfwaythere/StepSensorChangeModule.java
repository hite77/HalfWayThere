package hiteware.com.halfwaythere;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jasonhite on 4/29/15.
 */
@Module(
        library = true
)
public class StepSensorChangeModule
{
    @Provides
    @Singleton
    StepSensorChange provideStepSensorChange() {
        return new StepSensorChange();
    }
}
