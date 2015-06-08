/*
 * Using JavaCV to detect the colors in an image
 * SOURCE: http://ganeshtiwaridotcomdotnp.blogspot.de/2011/12/javacv-simple-color-detection-using.html
 */

import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_highgui.cvLoadImage;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MEDIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;
import static org.bytedeco.javacpp.opencv_core.cvGetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvRect;
import static org.bytedeco.javacpp.opencv_core.cvSetImageROI;
import static org.bytedeco.javacpp.opencv_core.cvAvg;

import java.util.Scanner;

import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;

public class ColorDetector implements Runnable {

	String s = "";
	IplImage img;

	public static void main(String[] args) {

		ColorDetector cd = new ColorDetector();
		Thread th = new Thread(cd);
		th.start();

	}

	public ColorDetector() {

		Scanner in = new Scanner(System.in);

		while (!s.equals("red") && !s.equals("blue") && !s.equals("green")) {

			System.out.println("Which colour would you like to detect?");
			s = in.nextLine().toLowerCase();
			System.out.println("You entered " + s);
		}
		in.close();

		getThresholdedImage(s);

	}

	void getThresholdedImage(String s) {

		String filename = "";

		// filename = "ColorFades.jpg";
		// filename = "ColorWall.jpg";
		filename = "Pixels.jpg";
		// filename = "Rainbow.jpg";
		// filename = "RGB.jpg";
		// filename = "Wheel.jpg";

		IplImage orgImg = cvLoadImage(filename);

		//gets the mean of the upper-left corner of the Image
		mean(orgImg, 0, 0, 15, 15);
		
		IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
		// apply thresholding

		if (s.equals("red")) {

			CvScalar red_min = cvScalar(0, 0, 130, 0);
			CvScalar red_max = cvScalar(80, 80, 255, 0);
			cvInRangeS(orgImg, red_min, red_max, imgThreshold);// red

		}

		if (s.equals("blue")) {

			CvScalar blue_min = cvScalar(130, 0, 0, 0);
			CvScalar blue_max = cvScalar(255, 80, 80, 0);
			cvInRangeS(orgImg, blue_min, blue_max, imgThreshold);// blue
		}
		if (s.equals("green")) {

			CvScalar green_min = cvScalar(0, 130, 0, 0);
			CvScalar green_max = cvScalar(80, 255, 80, 0);
			cvInRangeS(orgImg, green_min, green_max, imgThreshold);// green
		}

		cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15, 0, 0, 0);
		cvSaveImage("thr_" + filename, imgThreshold);
		System.out.println("DONE");
	}

	public void run() {
	}

	CvScalar mean(IplImage orgImg, int x, int y, int width, int height) {

		// get current ROI so that the image's roi is not changed by calling
		CvRect old_roi = cvGetImageROI(orgImg);
		cvSetImageROI(orgImg, cvRect(x, y, width, height));
		CvScalar c = cvAvg(orgImg);
		cvSetImageROI(orgImg, old_roi); // reset old roi
		//System.out.println(c);
		return c;

	}
}