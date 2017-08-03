package me.costa.gustavo.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Util {
	public static INDArray BufferedImageToINDArray(BufferedImage image) throws IOException{
    	ByteArrayOutputStream os = new ByteArrayOutputStream();
    	ImageIO.write(image, "jpg", os);
    	InputStream is = new ByteArrayInputStream(os.toByteArray());
        INDArray params = Nd4j.read(is);
        return params;
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