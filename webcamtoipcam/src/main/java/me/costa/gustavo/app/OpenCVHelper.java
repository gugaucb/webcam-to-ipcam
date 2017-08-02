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
		} catch (Throwable t) {
			t.printStackTrace();
			if (image == null) {
				image = getImage("D:\\Desenvolvimento\\Projetos\\webcam-to-ipcam\\fotos\\foto.jpg");
			}
		}

	}

	public BufferedImage getBufferedImage() {
		Processor processor = new Processor();
		if (image == null) {
			Mat frame = new Mat();
			if (camera.read(frame)) {
				return convertMatToBufferedImage(processor.detect(frame));
			}
			return null;
		}else{
			return convertMatToBufferedImage(processor.detect(bufferedImageToMat(image)));
		}
	}

	public String getBase64Image() {
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

	private static BufferedImage convertMatToBufferedImage(Mat mat) {
		byte[] data = new byte[mat.width() * mat.height() * (int) mat.elemSize()];
		int type;
		mat.get(0, 0, data);
		switch (mat.channels()) {
		case 1:
			type = BufferedImage.TYPE_BYTE_GRAY;
			break;
		case 3:
			type = BufferedImage.TYPE_3BYTE_BGR;
			byte b;
			for (int i = 0; i < data.length; i = i + 3) {
				b = data[i];
				data[i] = data[i + 2];
				data[i + 2] = b;
			}
			break;
		default:
			throw new IllegalStateException("Unsupported number of channels");
		}
		BufferedImage out = new BufferedImage(mat.width(), mat.height(), type);
		out.getRaster().setDataElements(0, 0, mat.width(), mat.height(), data);
		return out;
	}

	public static Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}
}
