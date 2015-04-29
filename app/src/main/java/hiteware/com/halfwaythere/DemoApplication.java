package hiteware.com.halfwaythere;

import android.app.Application;
import android.support.v4.app.FragmentActivity;

import dagger.ObjectGraph;

/**
 * Created by jasonhite on 4/23/15.
 */
public class DemoApplication extends Application {

    private ProductionModule productionModule = null;
    private ObjectGraph graph = null;
    protected boolean useMockSensorManager = false;

    protected Object mockSensorManagerModule;

    @Override public void onCreate() {
        super.onCreate();
    }

    public void inject(FragmentActivity activity, Object object)
    {
        if (graph == null) {
            if (useMockSensorManager) {
                graph = ObjectGraph.create(mockSensorManagerModule);
            } else {
                if (productionModule == null)
                    productionModule = new ProductionModule(activity);
                graph = ObjectGraph.create(productionModule);
            }
        }
        graph.inject(object);
    }
}

