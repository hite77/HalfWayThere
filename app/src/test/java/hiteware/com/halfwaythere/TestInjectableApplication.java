package hiteware.com.halfwaythere;

/**
 * Created on 4/23/15.
 */
public class TestInjectableApplication extends InjectableApplication {

    public TestModule testModule;

    @Override
    public void onCreate() {
        super.onCreate();
        testModule = new TestModule();
    }

    public void setMock() {
        useMock = true;
        objectsToCreate.add(testModule);
    }

    public void setRealSoftwareStepCounter() {
        objectsToCreate.add(new SoftwareStepCounterModule());
    }
}