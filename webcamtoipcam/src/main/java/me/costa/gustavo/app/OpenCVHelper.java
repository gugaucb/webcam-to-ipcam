package me.costa.gustavo.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class OpenCVHelper {
	private static OpenCVHelper instance = new OpenCVHelper();
	private static BufferedImage image;
	private boolean camAvaiable = false;

	public static OpenCVHelper getInstance() {
		return instance;
	}

	private VideoCapture camera = null;

	private OpenCVHelper() {
		try {
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			// System.loadLibrary("libopencv_java320");
			// loadLibraries();
			camera = new VideoCapture(0);
			camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 640);
			camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 480);
			if (!camera.isOpened()) {
				throw new IllegalStateException("Cannot open camera");
			}
			camAvaiable =true;
		} catch (Throwable t) {
			//t.printStackTrace();
			System.out.println("Camera is not avaiable!");
			if (image == null) {
				image = getImage("D:\\Desenvolvimento\\Projetos\\webcam-to-ipcam\\fotos\\foto.jpg");
				camAvaiable =false;
			}
		}

	}

	public BufferedImage getBufferedImage() throws IOException {
		Processor processor = Processor.getInstance();
		if (camAvaiable) {
			Mat frame = new Mat();
			if (camera.read(frame)) {
				return Util.convertMatToBufferedImage(processor.detect(frame));
			}
			return null;
		}else{
			return Util.convertMatToBufferedImage(processor.detect(Util.bufferedImageToMat(image)));
		}
	}

	public String getBase64Image() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedImage image = this.getBufferedImage();
		if (image != null) {
			try {
				ImageIO.write(image, "jpg", baos);
				return Base64.getEncoder().encodeToString(baos.toByteArray());
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return null;
	}

	private BufferedImage getImage(String filename) {
		// This time, you can use an InputStream to load
		try {
			// Grab the InputStream for the image.
			InputStream in = new FileInputStream(new File(filename));

			// Then read it in.
			return ImageIO.read(in);
		} catch (IOException e) {
			System.out.println("The image was not loaded.");
			// System.exit(1);
		}
		return null;
	}

}
