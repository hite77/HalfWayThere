package hiteware.com.halfwaythere;

/**
 * Created by jasonhite on 4/23/15.
 */
public class TestInjectableApplication extends InjectableApplication {

    public TestModule testModule;

    @Override
    public void onCreate() {
        super.onCreate();
        testModule = new TestModule();
    }

    public void setMockSensorManager() {
        useMockSensorManager = true;
        mockSensorManagerModule = testModule;
    }
}