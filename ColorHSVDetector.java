/*
 * Using JavaCV to detect the colors in an image but BASED on their HSV values 
 * SOURCE: http://ganeshtiwaridotcomdotnp.blogspot.de/2011/12/javacv-simple-color-detection-using.html
 */

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_highgui.cvLoadImage;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MEDIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class ColorHSVDetector implements Runnable {

	String s = "";
	int i = 0;
	double iLowH;
	double iHighH;
	IplImage img;

	public static void main(String[] args) {

		ColorHSVDetector cd = new ColorHSVDetector();
		Thread th = new Thread(cd);
		th.start();

	}

	public ColorHSVDetector() {

		// let's detect the 6 basic colors based on the HUE values:

		//for (i = 0; i < 7; i++) {

		//violet
		i = 5;	
		getThresholdedImage(s, i);

		//}
	}

	void getThresholdedImage(String s, int i) {

		String filename = "";

		// filename = "ColorFades.jpg";
		// filename = "ColorWall.jpg";
		// filename = "Pixels.jpg";
		filename = "Phone.jpg";
		// filename = "Squares.jpg";
		// filename = "Points.jpg";
		// filename = "Rainbow.jpg";
		// filename = "RGB.jpg";
		// filename = "Wheel.jpg";

		IplImage orgImg = cvLoadImage(filename);

		switch (i) {

		// orange
		case 0:
			iLowH = 0;
			iHighH = 22;
			break;
		// yellow
		case 1:
			iLowH = 22;
			iHighH = 38;
			break;
		// green
		case 2:
			iLowH = 38;
			iHighH = 75;
			break;
		// light blue
		case 3:
			iLowH = 75;
			iHighH = 100;
			break;
		// blue
		case 4:
			iLowH = 100;
			iHighH = 130;
			break;
		// violet
		case 5:
			iLowH = 130;
			iHighH = 160;
			break;
		// red
		case 6:
			iLowH = 160;
			iHighH = 179;
			break;
		}

		double iLowS = 1;
		double iHighS = 254;

		double iLowV = 1;
		double iHighV = 254;

		IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);

		IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);

		cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);

		cvInRangeS(imgHSV, cvScalar(iLowH, iLowS, iLowV, 0),
				cvScalar(iHighH, iHighS, iHighV, 0), imgThreshold);
		cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15, 0, 0, 0);
		cvSaveImage("thr_" + filename, imgThreshold);
		System.out.println("DONE");
	}

	public void run() {
	}

}