package hiteware.com.halfwaythere;

import android.util.Log;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created on 7/12/15.
 */
public class SoftwareNewStepJustYTest {
    @Test
    public void ReadFileAndFeedValues()
    {
        Scanner inFile1;
        try {

            inFile1 = new Scanner(new File("src/test/java/hiteware/com/halfwaythere/resources/yonly.txt"));
            List<Float> list = new ArrayList<>();
            while(inFile1.hasNext()) {
                float value = Float.parseFloat(inFile1.nextLine());
                list.add(value);
                Log.d("HalfWayThereTest", "yValue = " + Float.toString(value));
                System.out.println("yValue = "+Float.toString(value));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}
