package hiteware.com.halfwaythere;

import android.app.Application;

import dagger.ObjectGraph;

/**
 * Created by jasonhite on 4/23/15.
 */
public class InjectableApplication extends Application {

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
                graph = ObjectGraph.create(mockSensorManagerModule);
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