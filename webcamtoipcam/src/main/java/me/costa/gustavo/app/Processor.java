package me.costa.gustavo.app;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class Processor {
	private CascadeClassifier face_cascade;

	private static DeepFaceVariant deep = new DeepFaceVariant(363, 363, 3, 1,
			RandomUtils.nextLong(0, 20000000), 3);
	private static MultiLayerNetwork cnn = deep.init();

	// private static final Double THRESHHOLD = 150d;
	// Create a constructor method
	public Processor() {

		//face_cascade = new CascadeClassifier("/usr/local/Cellar/opencv3/3.2.0/share/OpenCV/haarcascades/haarcascade_profileface.xml");
		 face_cascade=new
		 CascadeClassifier("D:\\Desenvolvimento\\Projetos\\webcam-to-ipcam\\haarcascades\\haarcascade_profileface.xml");
		if (face_cascade.empty()) {
			System.out.println("--(!)Erroring A\n");
			return;
		}
	}

	public Mat detect(Mat inputframe) throws IOException {
		// long startTime = System.nanoTime();
		Mat mRgba = new Mat();
		Mat mGrey = new Mat();
		MatOfRect faces = new MatOfRect();
		inputframe.copyTo(mRgba);
		inputframe.copyTo(mGrey);
		Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(mGrey, mGrey);
		face_cascade.detectMultiScale(mGrey, faces);
		// long endTime = System.nanoTime();
		// System.out.println(String.format("Detect: %.2f ms", (float)(endTime -
		// startTime)/1000000));
		// System.out.println(String.format("Detectedaces",
		// faces.toArray().length));
		for (Rect rect : faces.toArray()) {
			// Point center= new Point(rect.x + rect.width*0.5, rect.y +
			// rect.height*0.5 );
			// Imgproc.ellipse( mRgba, center, new Size( rect.width*0.5,
			// rect.height*0.5), 0, 0, 360, new Scalar( 255, 0, 255 ), 4, 8, 0
			// );
			Mat imageROI = mRgba.submat(rect);
			BufferedImage face = Util.convertMatToBufferedImage(imageROI);
			fit(face);
			predict(face);

			Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(
					rect.x + rect.width, rect.y + rect.height), new Scalar(0,
					255, 0));
		}
		return mRgba;
	}

	private void fit(BufferedImage image) throws IOException {
		if (cnn != null) {
			INDArray labels = Nd4j.create(new double[] { 1 });
			//cnn.pretrain(Util.BufferedImageToINDArray(image));
			cnn.fit(Util.BufferedImageToINDArray(image));
		}
	}

	private void predict(BufferedImage image) throws IOException {
		if (cnn != null) {
			System.out
					.println("Predict: "+cnn.predict(Util.BufferedImageToINDArray(image)));
		}
	}

}
