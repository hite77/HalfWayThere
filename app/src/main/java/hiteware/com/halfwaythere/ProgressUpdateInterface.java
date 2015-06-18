package hiteware.com.halfwaythere;

/**
 * Created on 6/15/15.
 */
public interface ProgressUpdateInterface {
    void SetCircularProgress(CircularProgressWithHalfWay indicator);
    void SetSteps(int steps);
    void SetGoal(int goal);
    boolean SetHalfWay(int halfway);
    void ClearHalfWay();
}
