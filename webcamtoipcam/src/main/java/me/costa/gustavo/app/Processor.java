package me.costa.gustavo.app;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Processor {
	private CascadeClassifier face_cascade;  
	private static final Double THRESHHOLD = 150d;
    // Create a constructor method  
    public Processor(){  
    	
        face_cascade=new CascadeClassifier("/usr/local/Cellar/opencv3/3.2.0/share/OpenCV/haarcascades/haarcascade_profileface.xml");  
        if(face_cascade.empty())  
        {  
            System.out.println("--(!)Erroring A\n");  
            return;  
        }  
    }  
    public Mat detect(Mat inputframe){    
        long startTime = System.nanoTime();  
        Mat mRgba=new Mat();  
        Mat mGrey=new Mat();  
        MatOfRect faces = new MatOfRect();  
        inputframe.copyTo(mRgba);  
        inputframe.copyTo(mGrey);  
        Imgproc.cvtColor( mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);  
        Imgproc.equalizeHist( mGrey, mGrey );  
        face_cascade.detectMultiScale(mGrey, faces);  
        long endTime = System.nanoTime();  
      //  System.out.println(String.format("Detect: %.2f ms", (float)(endTime - startTime)/1000000));  
      //  System.out.println(String.format("Detectedaces", faces.toArray().length));  
        for(Rect rect:faces.toArray())  
        {  
            Point center= new Point(rect.x + rect.width*0.5, rect.y + rect.height*0.5 );  
            //Imgproc.ellipse( mRgba, center, new Size( rect.width*0.5, rect.height*0.5), 0, 0, 360, new Scalar( 255, 0, 255 ), 4, 8, 0 );
            Imgproc.rectangle(mRgba,new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(0, 255, 0));
        }  
        return mRgba;  
    }  
    
}
