package hiteware.com.halfwaythere;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created on 4/23/15.
 */
public class TestInjectableApplication extends InjectableApplication {
    @Singleton
    @Component(modules = { TestModule.class})
    public interface TestApplicationComponent extends ApplicationComponent {
        void inject(Object object);
    }
    public TestModule testModule;
    private TestApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        testModule = new TestModule();
    }

    public void setMock() {
        useMock = true;
    }

    public void begin(Object object) {
//        if (useMock) {
            component = DaggerTestInjectableApplication_TestApplicationComponent.builder()
                    .testModule(testModule)
                    .build();
            component.inject(object);
//        }
//        else
//        {
//            DaggerInjectableApplication_ApplicationComponent.builder().productionModule(new ProductionModule(this)).build().inject(object);
//        }
    }
}