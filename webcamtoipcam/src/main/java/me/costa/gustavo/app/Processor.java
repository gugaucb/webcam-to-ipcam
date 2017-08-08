package me.costa.gustavo.app;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
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

	private static DeepFaceVariant deep;
	public static MultiLayerNetwork cnn;
	private static Map<INDArray, INDArray> lista = new HashMap<INDArray, INDArray>();
	Multimap<INDArray, INDArray> multimap = ArrayListMultimap.create();
	private static int i = 1;
	private static boolean treinado = false;
	private long user = LocalTime.now().toNanoOfDay();
	private static Processor processor;
	private static DataSetIterator dataIter;
	
	public static Processor getInstance() throws IOException{
		if(processor==null){
			processor = new Processor();
		}
		return processor;
		
	}
	
	// private static final Double THRESHHOLD = 150d;
	// Create a constructor method
	private Processor() {

		face_cascade = new CascadeClassifier(
				"/usr/local/Cellar/opencv3/3.2.0/share/OpenCV/haarcascades/haarcascade_profileface.xml");
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
			BufferedImage face = Util.convertMatToBufferedImage(Util
					.resizeMat(imageROI));
			addFaces(face);
			treinar();
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

	public void treinar() throws IOException {
		if (i > 15 && !treinado) {
			dataIter = ImagePipeline.carregarImages();
			deep = new DeepFaceVariant(363, 363, 3, dataIter.totalOutcomes(),
					RandomUtils.nextLong(0, 20000000), 3);
			cnn = deep.init();
			cnn.fit(dataIter);
			treinado = true;
		}
	}

	private void addFaces(BufferedImage image) throws IOException {
		Util.salvarFace(image, user);
		i++;
		
	}

	private void addFace(INDArray bufferedImageToINDArray) {
		lista.put(Nd4j.create(new double[] { i++ }), bufferedImageToINDArray);
	}

	private void addFace(double idPredict, INDArray bufferedImageToINDArray) {
		lista.put(Nd4j.create(new double[] { idPredict }),
				bufferedImageToINDArray);
	}

	private void output(BufferedImage image) throws IOException {
		if (cnn != null) {
			System.out.println(Arrays.toString(cnn
					.output(Util.bufferedImageToINDArray(image)).data()
					.asDouble()));
		}
	}

	private String predict(BufferedImage image) throws IOException {
		if (cnn != null && dataIter!=null) {
			INDArray bufferedImageToINDArray = Util.bufferedImageToINDArray(image);
			int[] predict = cnn.predict(bufferedImageToINDArray);
			System.out.println("Predict: " + dataIter.getLabels().get(predict[0]));
			return dataIter.getLabels().get(predict[0]);
		}
		return "Não identificado";
	}

}
