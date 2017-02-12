package com.isvogue.myapplication;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Face;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;


public class MainActivity extends AppCompatActivity implements CameraDetector.CameraEventListener, CameraDetector.ImageListener{
    SurfaceView cameraDetectorSurfaceView;
    CameraDetector cameraDetector;

    int maxProcessingRate = 10;

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

        System.out.println("Joy: "+joy);
        System.out.println("Anger: "+anger);
        System.out.println("Surprise: "+suprise);
    }

    public void onCameraSizeSelected(int cameraHeight, int cameraWidth, Frame.ROTATE rotation){
        ViewGroup.LayoutParams params = cameraDetectorSurfaceView.getLayoutParams();

        params.height = cameraHeight;
        params.width = cameraWidth;

        cameraDetectorSurfaceView.setLayoutParams(params);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);



        cameraDetectorSurfaceView=(SurfaceView)findViewById(R.id.cameraDetectorSurfaceView);
        cameraDetector=new CameraDetector(this,CameraDetector.CameraType.CAMERA_FRONT,cameraDetectorSurfaceView);

        cameraDetector.setMaxProcessRate(maxProcessingRate);

        cameraDetector.setImageListener(this);
        cameraDetector.setOnCameraEventListener(this);

        cameraDetector.setDetectAllEmotions(true);

        cameraDetector.start();
    }
}

