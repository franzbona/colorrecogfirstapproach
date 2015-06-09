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

import java.awt.Color;

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
		int range = 40; // range of colors

		// filename = "ColorFades.jpg";
		// filename = "ColorWall.jpg";
		filename = "Pixels.jpg";
		// filename = "Points.jpg";
		// filename = "Rainbow.jpg";
		// filename = "RGB.jpg";
		// filename = "Wheel.jpg";

		IplImage orgImg = cvLoadImage(filename);

		// gets the mean of the upper-left corner of the Image
		CvScalar mean = mean(orgImg, 0, 0, size, size);

		Color c_min = minColor(range, mean);
		Color c_max = maxColor(range, mean);

		System.out.println(c_min + " " + mean + " " + c_max);

		IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);

		CvScalar min = cvScalar(c_min.getBlue(), c_min.getGreen(),
				c_min.getRed(), 0);
		CvScalar max = cvScalar(c_max.getBlue(), c_max.getGreen(),
				c_max.getRed(), 0);

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

	Color minColor(int range, CvScalar c) {
		int min_r = (int) (c.red() + (int) (Math.random() / 2 * range - range));
		min_r = Math.min(255, Math.max(0, min_r));

		int min_g = (int) (c.green() + (int) (Math.random() / 2 * range - range));
		min_g = Math.min(255, Math.max(0, min_g));

		int min_b = (int) (c.blue() + (int) (Math.random() / 2 * range - range));
		min_b = Math.min(255, Math.max(0, min_b));

		return new Color(min_r, min_g, min_b);
	}

	Color maxColor(int range, CvScalar c) {

		int max_r = (int) (c.red() + (int) (Math.random() * 2 * range + range));
		max_r = Math.min(255, Math.max(0, max_r));

		int max_g = (int) (c.green() + (int) (Math.random() * 2 * range + range));
		max_g = Math.min(255, Math.max(0, max_g));

		int max_b = (int) (c.blue() + (int) (Math.random() * 2 * range + range));
		max_b = Math.min(255, Math.max(0, max_b));

		return new Color(max_r, max_g, max_b);
	}
}