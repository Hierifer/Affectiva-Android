package com.isvogue.myapplication;

//Basic
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

//Affectiva
import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Face;

//ViewGraph
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;



public class MainActivity extends AppCompatActivity implements CameraDetector.CameraEventListener, CameraDetector.ImageListener{
    SurfaceView cameraDetectorSurfaceView;
    CameraDetector cameraDetector;

    TextView joytext;
    TextView angertext;
    TextView surprisetext;
    TextView meantext;
    TextView thresholdtext;

    private LineGraphSeries<DataPoint> series;   //current joy line
    private LineGraphSeries<DataPoint> series2;  // threshold
    private LineGraphSeries<DataPoint> series3;  // joy mean

    final int dataNumber = 300;  // control # of data on graph. Assignment require 15, I prefer 300 for observe the trend of joy
    private int lastX = 0;
    int maxProcessingRate = 10;
    float headjoytime = 0;  //first timeStamp in Queue
    float threshold = 40;
    float mean;

    Queue joys = new Queue(); //Queue

    @Override
    public void onImageResults(List<Face> faces, Frame frame, float timeStamp){
        if(faces == null)
            return; // frame was not processed

        if(faces.size() == 0)
            return; // no face found

        Face face = faces.get(0);

        float joy = face.emotions.getJoy();
        float anger = face.emotions.getAnger();
        float suprise = face.emotions.getSurprise();


        if(headjoytime == 0){
            // init headjoytime
            headjoytime = timeStamp;
        }

        //dequeue all entries on Queue that timeStamp greater than 15
        while((timeStamp-headjoytime)>15){
            //drop first and see the next
            joys.dequeue();
            headjoytime = joys.peek();
        }

        //enqueue new entry
        joys.enqueue(new Point(joy, timeStamp));

        mean = joys.getMean();

        System.out.println("Joy: "+joy);
        System.out.println("Anger: "+anger);
        System.out.println("Surprise: "+suprise);
        System.out.println("Time: "+timeStamp);


        // Update text
        joytext.setText("Joy:"+joy);
        angertext.setText("Anger: "+anger);
        surprisetext.setText("Surprise: "+suprise);

        if(joys.size() > 14) {
            System.out.println("Mean of Joy: " + mean);
            meantext.setText("Mean of Joy: " + mean);
            // Update Chart
            addjoy(joy, mean);
        } else {
            System.out.println("#Data is under 15");
            meantext.setText("#Data is under 15");
            // Update Chart
            addjoy(joy, 0);
        }

        if(mean >= threshold){
            System.out.println("You have reached the threshold");
            thresholdtext.setText("You have reached the threshold");
        }else {
            thresholdtext.setText("");
        }

    }

    public void onCameraSizeSelected(int cameraHeight, int cameraWidth, Frame.ROTATE rotation){
        ViewGroup.LayoutParams params = cameraDetectorSurfaceView.getLayoutParams();

        params.height = cameraHeight;
        params.width = cameraWidth;

        cameraDetectorSurfaceView.setLayoutParams(params);
    }

    Viewport viewport;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //text
        joytext = (TextView)findViewById(R.id.joy);
        angertext = (TextView)findViewById(R.id.anger);
        surprisetext = (TextView)findViewById(R.id.surprise);
        meantext = (TextView)findViewById(R.id.mean);
        thresholdtext = (TextView)findViewById(R.id.Threshold);

        //viewgraph
        GraphView graph = (GraphView) findViewById(R.id.graph);

        series = new LineGraphSeries<DataPoint>();
        series2 = new LineGraphSeries<DataPoint>();
        series3 = new LineGraphSeries<DataPoint>();

        series.setColor(Color.BLUE);
        series2.setColor(Color.GREEN);
        series3.setColor(Color.BLACK);

        graph.addSeries(series);
        graph.addSeries(series2);
        graph.addSeries(series3);

        viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(dataNumber);
        viewport.setMinY(0);
        viewport.setMaxY(100);
       // viewport.setScrollable(true);

        cameraDetectorSurfaceView=(SurfaceView)findViewById(R.id.cameraDetectorSurfaceView);
        cameraDetector=new CameraDetector(this,CameraDetector.CameraType.CAMERA_FRONT,cameraDetectorSurfaceView);

        cameraDetector.setMaxProcessRate(maxProcessingRate);

        cameraDetector.setImageListener(this);
        cameraDetector.setOnCameraEventListener(this);

        cameraDetector.setDetectAllEmotions(true);

        cameraDetector.start();
    }

    public void addjoy(final float joy, final float mean){
        runOnUiThread(new Runnable(){
            public void run(){
                double data = joy;
                double data2 = mean;
                series.appendData(new DataPoint(lastX++, data), true, dataNumber);
                series2.appendData(new DataPoint(lastX++, 40), true, dataNumber);
                series3.appendData(new DataPoint(lastX++, data2), true, dataNumber);
            }
        });
    }
}

