import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_highgui.cvLoadImage;
import static org.bytedeco.javacpp.opencv_highgui.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_MEDIAN;
import static org.bytedeco.javacpp.opencv_imgproc.cvSmooth;

import java.util.Scanner;

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
		
		filename = "ColorFades.jpg";
//		filename = "ColorWall.jpg";
//		filename = "Pixels.jpg";
//		filename = "Rainbow.jpg";
//		filename = "RGB.jpg";
//		filename = "Wheel.jpg";	
		
		IplImage orgImg = cvLoadImage(filename);
//		IplImage orgImg = cvLoadImage(filename);
//		IplImage orgImg = cvLoadImage(filename);
//		IplImage orgImg = cvLoadImage(filename);
//		IplImage orgImg = cvLoadImage(filename);
//		IplImage orgImg = cvLoadImage(filename);
		
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
		cvSaveImage(filename + "_thr", imgThreshold);
		System.out.println("DONE");
	}

	public void run() {
		// TODO Auto-generated method stub
		
	}
}