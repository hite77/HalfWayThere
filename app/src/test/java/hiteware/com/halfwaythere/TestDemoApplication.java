package hiteware.com.halfwaythere;

import android.support.v4.app.FragmentActivity;

/**
 * Created by jasonhite on 4/23/15.
 */
public class TestDemoApplication extends DemoApplication{

    private TestModule testModule;

    @Override
    public void onCreate() {
        super.onCreate();
        testModule = new TestModule();
    }

    public void setMockLocationManager() {
        useMockLocationManager = true;
        mockLocationManagerModule = testModule;
    }

    public void inject(FragmentActivity activity, Object object) {
        super.inject(activity, object);
    }
}