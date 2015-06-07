package hiteware.com.halfwaythere;

import android.app.Application;

import dagger.ObjectGraph;

/**
 * Created on 4/23/15.
 */
public class InjectableApplication extends Application {

    public ProductionModule productionModule =new ProductionModule(this);

    private ObjectGraph graph = null;
    protected boolean useMockSensorManager = false;

    protected Object mockSensorManagerModule;

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