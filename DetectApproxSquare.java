/*
 * Using JavaCV to approximate colored shape to a rectangle accoring to the color picked in the control card
 * SOURCE: http://stackoverflow.com/questions/12106307/how-to-get-x-y-coordinates-of-extracted-objects-in-javacv
 * HELP: http://stackoverflow.com/questions/11795691/javacv-warning-sign-detection
 * HELP: http://www.javacodegeeks.com/2012/12/hand-and-finger-detection-using-javacv.html
 * HELP: http://stackoverflow.com/questions/11388683/opencv-javacv-how-to-iterate-over-contours-for-shape-identification
 */

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class DetectApproxSquare {

	String filename = "";
	int size = 0;
	int range = 0;
	CvScalar mean;
	CvScalar min;
	CvScalar max;
	IplImage orgImg;
	IplImage imgThresh;

	public static void main(String args[]) {
		new DetectApproxSquare().main();
	}

	public void main() {

		// creates memory storage that will contain all the dynamic data
		CvMemStorage storage = null;
		storage = cvCreateMemStorage(0);

		// filename = "1_dark.jpg";
		filename = "1_light.jpg";

		// loads the image
		orgImg = cvLoadImage(filename);

		// size of the "control card"
		size = 50;
		range = 40; // range of colors

		// gets the mean color in the "control card"
		mean = mean(orgImg, 0, 0, size, size);
		// mean = mean(orgImg, orgImg.width() - size, 0, size, size);
		// mean = mean(orgImg, 0, orgImg.height()-size, size, size);
		// mean = mean(orgImg, orgImg.width()-size, orgImg.height()-size, size, size);

		// gets the color from the "control card" and recognizes it
		getControlColor(mean);

		imgThresh = cvCreateImage(cvGetSize(orgImg), 8, 1);

		// checks the parts of the image in the range of the color chosen
		cvInRangeS(orgImg, min, max, imgThresh);

		// smooths the image according to the different parameters
		cvSmooth(imgThresh, imgThresh, CV_MEDIAN, 15, 0, 0, 0);

		// temporary saves the image (NO IDEA WHY I HAVE TO)
		cvSaveImage("Temp.jpg", imgThresh);

		// re-loads the image (otherwise "ASSERTION FAILED")
		imgThresh = cvLoadImage("Temp.jpg");

		// finds the squares
		findSquares(imgThresh, storage);

		// releases the images
		cvReleaseImage(orgImg);
		cvReleaseImage(imgThresh);

		// clears the memory storage
		cvClearMemStorage(storage);

	}

	public void getControlColor(CvScalar mean) {

		int r = (int) mean.red();
		int g = (int) mean.green();
		int b = (int) mean.blue();

		System.out.println("RGB: (" + r + ", " + g + ", " + b + ")");

		// red
		double min_r;
		if ((mean.red() - range) < 0)
			min_r = 0;
		else
			min_r = (mean.red() - range);

		double max_r;
		if ((mean.red() + range) > 255)
			max_r = 255;
		else
			max_r = (mean.red() + range);

		// green
		double min_g;
		if ((mean.green() - range) < 0)
			min_g = 0;
		else
			min_g = (mean.green() - range);

		double max_g;
		if ((mean.green() + range) > 255)
			max_g = 255;
		else
			max_g = (mean.green() + range);

		// blue
		double min_b;
		if ((mean.blue() - range) < 0)
			min_b = 0;
		else
			min_b = (mean.blue() - range);

		double max_b;
		if ((mean.blue() + range) > 255)
			max_b = 255;
		else
			max_b = (mean.blue() + range);

		min = cvScalar(min_b, min_g, min_r, 0);
		System.out.println(min);
		max = cvScalar(max_b, max_g, max_r, 0);
		System.out.println(max);
		System.out.println();

	}

	// returns sequence of squares detected on the image and stores it in the
	// memory storage
	public void findSquares(IplImage img, CvMemStorage storage) {

		IplImage gry = cvCreateImage(cvGetSize(orgImg), 8, 1);

		// reloads the original image
		IplImage cpy = cvLoadImage(filename);

		// gets the HSV image passed and converts to grayscale
		cvCvtColor(img, gry, CV_BGR2GRAY);
		cvThreshold(gry, gry, 200, 255, CV_THRESH_BINARY);
		cvAdaptiveThreshold(gry, gry, 255, CV_ADAPTIVE_THRESH_MEAN_C,
				CV_THRESH_BINARY_INV, 11, 5);

		CvSeq contours = new CvContour(null);
		CvSeq cont = new CvSeq();

		// finds the contours of the figures in the grayscale image
		// CV_RETR_EXTERNAL retrieves only the extreme outer contours
		cvFindContours(gry, storage, contours, Loader.sizeof(CvContour.class),
				CV_RETR_EXTERNAL, CV_CHAIN_APPROX_NONE, new CvPoint());

		// counts the contours
		int count = 1;

		// checks if there are rectangles detected
		if (contours.address() == 0)
			System.out.println("No rectangles detected!");

		// in this case, the loop can work
		else {
			// loops around the contours
			for (cont = contours; cont != null; cont = cont.h_next()) {

				// draws the contours of the identified colored shape
				CvScalar color = CvScalar.BLUE;
				cvDrawContours(cpy, cont, color, CV_RGB(0, 0, 0), -1,
						CV_FILLED, 8, cvPoint(0, 0));

				// creates the bounding rectangle around the contours
				CvRect sq = cvBoundingRect(cont, 0);

				// checks if the shape is too small, in that case it is not
				// drawn
				if ((sq.height() > 25) && (sq.width() > 25)) {

					// gets the coordinates of the 4 points of the rectangle
					CvPoint tl = new CvPoint();
					tl.x(sq.x());
					tl.y(sq.y());

					CvPoint tr = new CvPoint();
					tr.x(sq.x() + sq.width());
					tr.y(sq.y());

					CvPoint br = new CvPoint();
					br.x(sq.x() + sq.width());
					br.y(sq.y() + sq.height());

					CvPoint bl = new CvPoint();
					bl.x(sq.x());
					bl.y(sq.y() + sq.height());

					System.out.println("Rectangle" + count);
					System.out.println("Coordinates: " + tl + tr + br + bl);
					System.out.println();

					// draws the rectangle
					cvRectangle(cpy, tl, br, CV_RGB(255, 0, 0), 2, 8, 0);
					count++;
				}
			}

			// saves the resultant image
			cvSaveImage("Rect_" + filename, cpy);

			cvReleaseImage(cpy);
			cvReleaseImage(gry);
		}

		return;
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