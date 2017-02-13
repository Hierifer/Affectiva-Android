package com.isvogue.myapplication;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.Random;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;


public class MainActivity extends AppCompatActivity implements CameraDetector.CameraEventListener, CameraDetector.ImageListener{
    SurfaceView cameraDetectorSurfaceView;
    CameraDetector cameraDetector;
    TextView joytext;
    TextView angertext;
    TextView surprisetext;
    TextView meantext;
    TextView thresholdtext;

    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;

    int maxProcessingRate = 10;
    float headjoytime = 0;
    float threshold = 40;
    float mean;
    Queue joys = new Queue();

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
            headjoytime = headjoytime;
        }

        while((timeStamp-headjoytime)>15){
            joys.dequeue();
            headjoytime = joys.peek();
        }

        joys.enqueue(new Point(joy, timeStamp));

        mean = joys.getMean();

        System.out.println("Joy: "+joy);
        System.out.println("Anger: "+anger);
        System.out.println("Surprise: "+suprise);

        joytext.setText("Joy:"+joy);
        angertext.setText("Anger: "+anger);
        surprisetext.setText("Surprise: "+suprise);

        if(joys.size() > 14) {
            System.out.println("Mean of Joy: " + mean);
            meantext.setText("Mean of Joy: " + mean);
        } else {
            System.out.println("#Data is under 15");
            meantext.setText("#Data is under 15");
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

    GraphView graph;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //text
        joytext = (TextView)findViewById(R.id.joy);
        angertext = (TextView)findViewById(R.id.anger);
        surprisetext = (TextView)findViewById(R.id.surprise);
        meantext = (TextView)findViewById(R.id.mean);
        thresholdtext = (TextView)findViewById(R.id.Threshold);

        // we get graph view instance
        graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(100);
        viewport.setScrollable(true);


        cameraDetectorSurfaceView=(SurfaceView)findViewById(R.id.cameraDetectorSurfaceView);
        cameraDetector=new CameraDetector(this,CameraDetector.CameraType.CAMERA_FRONT,cameraDetectorSurfaceView);

        cameraDetector.setMaxProcessRate(maxProcessingRate);

        cameraDetector.setImageListener(this);
        cameraDetector.setOnCameraEventListener(this);

        cameraDetector.setDetectAllEmotions(true);

        cameraDetector.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                while (true) {
                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(1200);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                        }
                    });
                }
            }
        }).start();
    }

    // add random data to graph
    private void addEntry() {
        double data = mean;
        series.appendData(new DataPoint(lastX++, data), true, 15);
        graph.getViewport().setMaxX(lastX);
        graph.getViewport().setMinX(lastX- 15);
    }
}

