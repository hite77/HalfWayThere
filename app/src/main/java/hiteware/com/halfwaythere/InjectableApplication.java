package hiteware.com.halfwaythere;

import android.app.Application;

import dagger.ObjectGraph;

/**
 * Created on 4/23/15.
 */
class InjectableApplication extends Application {

    private final ProductionModule productionModule =new ProductionModule(this);

    private ObjectGraph graph = null;
    boolean useMock = false;

    Object mockModule;

    private void buildGraph()
    {
        if (graph == null)
        {
            if (useMock) {
                graph = ObjectGraph.create(mockModule);
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