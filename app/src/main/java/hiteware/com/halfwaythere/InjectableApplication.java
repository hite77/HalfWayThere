package hiteware.com.halfwaythere;

import android.app.Application;

import dagger.ObjectGraph;

/**
 * Created on 4/23/15.
 */
class InjectableApplication extends Application {

    private final ProductionModule productionModule =new ProductionModule(this);

    private ObjectGraph graph = null;
    boolean useMockSensorManager = false;

    Object mockSensorManagerModule;

    private void buildGraph()
    {
        if (graph == null)
        {
            if (useMockSensorManager) {
                graph = ObjectGraph.create(mockSensorManagerModule);
            } else {
               graph = ObjectGraph.create(productionModule);
            }
        }
    }

    public void inject(Object object)
    {
        buildGraph();
        graph.inject(object);
    }
}