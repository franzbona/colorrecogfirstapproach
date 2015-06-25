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

		getThresholdedImage(s);

	}

	void getThresholdedImage(String s) {

		String filename = "";
		int size = 15; // size of the rectangle
		int range = 5; // range of colors

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

		// gets the mean of the upper-left corner of the Image
		CvScalar mean = mean(orgImg, 0, 0, size, size);

		IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);

		CvScalar min = cvScalar(mean.blue() - range, mean.green() - range,
				mean.red() - range, 0);
		CvScalar max = cvScalar(mean.blue() + range, mean.green() + range,
				mean.red() + range, 0);

		cvInRangeS(orgImg, min, max, imgThreshold);
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
		// System.out.println(c);
		return c;

	}

}