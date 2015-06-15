package hiteware.com.halfwaythere;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created on 4/23/15.
 */
public class InjectableApplication extends Application {

    private final ProductionModule productionModule =new ProductionModule(this);

    final List<Object> objectsToCreate = new ArrayList<>();

    private ObjectGraph graph = null;
    boolean useMock = false;

    private void buildGraph()
    {
        if (graph == null)
        {
            if (!useMock) {
                objectsToCreate.add(productionModule);
            }
                graph = ObjectGraph.create(objectsToCreate.toArray());
        }
    }

    public void inject(Object object)
    {
        buildGraph();
        graph.inject(object);
    }
}