package hiteware.com.halfwaythere;

import dagger.Module;
import dagger.Provides;

/**
 * Created by jasonhite on 4/29/15.
 */
@Module(
        library = true
)
public class StepServiceModule
{
    StepService stepService = new StepService();

    @Provides
    public StepService provideStepService()
    {
        return stepService;
    }
}