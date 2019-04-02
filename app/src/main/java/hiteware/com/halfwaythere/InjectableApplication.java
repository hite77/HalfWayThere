package hiteware.com.halfwaythere;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created on 4/23/15.
 */


public class InjectableApplication extends Application {
    @Singleton
    @Component(modules = { ProductionModule.class })
    public interface ApplicationComponent {
        void inject(Object object);
    }
    boolean useMock = false;

    private ApplicationComponent component;
    private ProductionModule module = new ProductionModule(this);

    public void begin(Object object) {
            if (!useMock) {
                component = DaggerInjectableApplication_ApplicationComponent.builder().productionModule(module).build();
                component.inject(this);
            }
    }

}