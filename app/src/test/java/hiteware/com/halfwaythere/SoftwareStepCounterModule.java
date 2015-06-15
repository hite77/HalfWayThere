package hiteware.com.halfwaythere;

import dagger.Module;

/**
 * Created on 6/14/15.
 */

@Module(
        injects = {SensorIntegrationTest.class},
        library = true,
        includes = ProductionModule.class,
        overrides = true
)

public class SoftwareStepCounterModule {
    // this should be added to the modules injected so that tests can get a hold of the SoftwareStepCounter
}
