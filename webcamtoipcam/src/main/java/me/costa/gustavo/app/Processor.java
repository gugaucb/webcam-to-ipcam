package me.costa.gustavo.app;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Processor {
	private CascadeClassifier face_cascade;

	private static DeepFaceVariant deep = new DeepFaceVariant(363, 363, 3, 1,
			RandomUtils.nextLong(0, 20000000), 3);
	private static MultiLayerNetwork cnn = deep.init();
	private static Map<INDArray, INDArray> lista = new HashMap<INDArray, INDArray>();
	Multimap<INDArray, INDArray> multimap = ArrayListMultimap.create();
	private static double id = 1d;

	// private static final Double THRESHHOLD = 150d;
	// Create a constructor method
	public Processor() {

		face_cascade = new CascadeClassifier("/usr/local/Cellar/opencv3/3.2.0/share/OpenCV/haarcascades/haarcascade_profileface.xml");
		// face_cascade=new
		// CascadeClassifier("D:\\Desenvolvimento\\Projetos\\webcam-to-ipcam\\haarcascades\\haarcascade_profileface.xml");
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
			BufferedImage face = Util.convertMatToBufferedImage(Util.resizeMat(imageROI));
			fit(face);
			System.out.println("Predict");
			predict(face);
			System.out.println("Output");
			output(face);

			Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(
					rect.x + rect.width, rect.y + rect.height), new Scalar(0,
					255, 0));
		}
		return mRgba;
	}

	private void fit(BufferedImage image) throws IOException {
		if (cnn != null) {
			System.out.println("Treinando");
			INDArray bufferedImageToINDArray = Util.bufferedImageToINDArray(image);
			addFace(bufferedImageToINDArray);
			for (Map.Entry<INDArray, INDArray> entry : lista.entrySet())
			{
			    //System.out.println(entry.getKey() + "/" + entry.getValue());
			    cnn.fit(entry.getValue(), entry.getKey());
			}
			
			/*Evaluation eval = new Evaluation(1);
			eval.eval(labels, bufferedImageToINDArray, cnn);
			System.out.println("Treinado");*/
		}
	}

	private void addFace(INDArray bufferedImageToINDArray) {
		lista.put(Nd4j.create(new double[] { id++ }), bufferedImageToINDArray);
	}
	
	private void addFace(double idPredict, INDArray bufferedImageToINDArray) {
		lista.put(Nd4j.create(new double[] { idPredict }), bufferedImageToINDArray);
	}
	
	
	private void output(BufferedImage image) throws IOException{
		if (cnn != null) {
			System.out.println(Arrays.toString(cnn.output(Util.bufferedImageToINDArray(image)).data().asDouble()));
		}
	}
	
	private int predict(BufferedImage image) throws IOException {
		if (cnn != null) {
			int[] predict = cnn.predict(Util.bufferedImageToINDArray(image));
			System.out
					.println("Predict: "+Arrays.toString(predict));
			return predict[0];
		}
		return 0;
	}

}
