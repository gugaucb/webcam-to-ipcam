package me.costa.gustavo.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;

import javax.imageio.ImageIO;

import org.datavec.image.loader.ImageLoader;
import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Util {
	public static INDArray bufferedImageToINDArray(BufferedImage image) throws IOException {
		try {
			NativeImageLoader loader = new NativeImageLoader(image.getHeight(), image.getWidth(), 3);
			 
			INDArray params = loader.asMatrix(image);//imgLoader.toBgr(image);//Nd4j.read(bufferedToImg(image));
			return params;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}
	public static void salvarFace(BufferedImage image, long user) throws IOException{
		//https://gitter.im/deeplearning4j/deeplearning4j
		new File("/Users/gustavoluisdacosta/Desenvolvimento/fotos/"+user+"//").mkdir();
		File outputfile = new File("/Users/gustavoluisdacosta/Desenvolvimento/fotos/"+user+"//"+LocalTime.now().toNanoOfDay()+".jpg");
		ImageIO.write(image, "jpg", outputfile);
	}

	public static Mat resizeMat(Mat mat){
		Size size = new Size(363, 363);
		Mat resized = new Mat();
		Imgproc.resize(mat, resized, size);
		return resized;
	}
	
	public static BufferedImage convertMatToBufferedImage(Mat mat) {
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