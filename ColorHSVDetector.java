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

import java.util.Scanner;

import org.bytedeco.javacpp.opencv_core.IplImage;

public class ColorHSVDetector implements Runnable {

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

		String s = "";
		Scanner in = new Scanner(System.in);

		while (s.isEmpty()) {

			System.out.println("Which colour would you like to detect?");
			s = in.nextLine().toLowerCase();
			System.out.println("You entered " + s);
		}
		in.close();

		switch (s) {

		case "orange":
			iLowH = 0;
			iHighH = 22;
			break;
		case "yellow":
			iLowH = 22;
			iHighH = 38;
			break;
		case "green":
			iLowH = 38;
			iHighH = 75;
			break;
		case "light blue":
			iLowH = 75;
			iHighH = 100;
			break;
		case "blue":
			iLowH = 100;
			iHighH = 130;
			break;
		case "violet":
			iLowH = 130;
			iHighH = 160;
			break;
		case "red":
			iLowH = 160;
			iHighH = 179;
			break;
		default:
			iLowH = 0;
			iHighH = 0;
		}

		getThresholdedImage(iLowH, iHighH);

		// }
	}

	void getThresholdedImage(double iLowH, double iHighH) {

		String filename = "";
		
		// filename = "ColorFades.jpg";
		// filename = "ColorWall.jpg";
		filename = "Phone.jpg";
		// filename = "Pixels.jpg";
		// filename = "Points.jpg";
		// filename = "Squares.jpg";

		IplImage orgImg = cvLoadImage(filename);

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
		cvSaveImage("HSV_" + filename, imgThreshold);
		System.out.println("DONE");
	}

	public void run() {
	}

}