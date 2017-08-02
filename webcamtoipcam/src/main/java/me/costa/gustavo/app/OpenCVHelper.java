package me.costa.gustavo.app;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class OpenCVHelper {
	private static OpenCVHelper instance = new OpenCVHelper();
	public static OpenCVHelper getInstance() {
		return instance;
	}
	
    private VideoCapture camera = null;  
	private OpenCVHelper() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); 
		//System.loadLibrary("libopencv_java320");
		//loadLibraries();
		camera = new VideoCapture(0);
		camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 640);  
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 480); 
        if(!camera.isOpened()){  
        	throw new IllegalStateException("Cannot open camera");
        }  
	}
	
	public BufferedImage getBufferedImage() {
        Mat frame = new Mat();  
        Processor processor = new Processor();
        if (camera.read(frame)){  
        	return convertMatToBufferedImage(processor.detect(frame));
        }
        return null;
	}
	
	public String getBase64Image() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedImage image = this.getBufferedImage();
		if (image != null) {
	        try {
	        	ImageIO.write( image, "jpg", baos);
	        	return Base64.getEncoder().encodeToString(baos.toByteArray());
	        } catch( IOException ioe ) {
	        	ioe.printStackTrace();
	        }		
		}
		return null;
	}
	
	private static BufferedImage convertMatToBufferedImage(Mat mat) {  
		byte[] data = new byte[mat.width() * mat.height() * (int)mat.elemSize()];  
		int type;  
		mat.get(0, 0, data);  
		switch (mat.channels()) {    
			case 1:    
				type = BufferedImage.TYPE_BYTE_GRAY;    
			break;    
			case 3:    
				type = BufferedImage.TYPE_3BYTE_BGR;    
			byte b;    
				for(int i=0; i<data.length; i=i+3) {    
					b = data[i];    
					data[i] = data[i+2];    
					data[i+2] = b;    
				}    
			break;    
			default:    
				throw new IllegalStateException("Unsupported number of channels");  
		}    
		BufferedImage out = new BufferedImage(mat.width(), mat.height(), type);  
		out.getRaster().setDataElements(0, 0, mat.width(), mat.height(), data);  
		return out;  
	}  
}

