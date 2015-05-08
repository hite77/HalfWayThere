package hiteware.com.halfwaythere;

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
    StepSensorChange stepSensorChange = new StepSensorChange();

    @Provides
    public StepSensorChange provideStepSensorChange()
    {
        return stepSensorChange;
    }
}
