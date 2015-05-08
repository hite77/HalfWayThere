package hiteware.com.halfwaythere;

import android.app.Application;

import dagger.ObjectGraph;

/**
 * Created by jasonhite on 4/23/15.
 */
public class InjectableApplication extends Application {

    public ProductionModule productionModule =new ProductionModule(this);
    public StepSensorChangeModule stepSensorChangeModule = new StepSensorChangeModule();

    private ObjectGraph graph = null;
    protected boolean useMockSensorManager = false;

    protected Object mockSensorManagerModule;

    @Override public void onCreate() {
        super.onCreate();
    }

    private void buildGraph()
    {
        if (graph == null)
        {
            if (useMockSensorManager) {
                graph = ObjectGraph.create(mockSensorManagerModule, stepSensorChangeModule);
            } else {
               graph = ObjectGraph.create(productionModule, stepSensorChangeModule);
            }
        }
    }

    public void inject(Object object)
    {
        buildGraph();
        graph.inject(object);
    }
}