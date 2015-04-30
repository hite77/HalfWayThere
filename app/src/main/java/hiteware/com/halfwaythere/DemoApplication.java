package hiteware.com.halfwaythere;

import android.app.Application;

import dagger.ObjectGraph;

/**
 * Created by jasonhite on 4/23/15.
 */
public class DemoApplication extends Application {

    private ProductionModule productionModule = null;
    private ObjectGraph graph = null;
    protected boolean useMockSensorManager = false;

    protected Object mockSensorManagerModule;

    private StepSensorChangeModule stepSensorChangeModule = new StepSensorChangeModule();

    @Override public void onCreate() {
        super.onCreate();
    }

    // temporarily build graph and then inject
    private void buildGraph()
    {
        if (graph == null)
        {
            if (useMockSensorManager) {
                graph = ObjectGraph.create(mockSensorManagerModule, stepSensorChangeModule);
            } else {
                if (productionModule == null)
                    productionModule = new ProductionModule(this);
                graph = ObjectGraph.create(productionModule, stepSensorChangeModule);
            }
        }
    }

    public void inject(Object object)
    {
        buildGraph();
        graph.inject(object);
    }

    public void addToGraph(Object module) {
        buildGraph();
        graph = graph.plus(module);
    }
}