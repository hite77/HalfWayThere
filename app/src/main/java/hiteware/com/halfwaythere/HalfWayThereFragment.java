package hiteware.com.halfwaythere;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HalfWayThereFragment extends Fragment{

    private UILogic logic = null;

    private void ConstructUILogicOnce() {
        if (null == logic) {
            logic = new UILogic(getActivity(), getView());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstructUILogicOnce();
        logic.Setup();
    }

    @Override
    public void onPause() {
        super.onPause();
        ConstructUILogicOnce();
        logic.Pause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}
