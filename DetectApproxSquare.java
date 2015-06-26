/*
 * Using JavaCV to approximate colored shape to a rectangle accoring to the color picked in the control card
 * SOURCE: http://stackoverflow.com/questions/12106307/how-to-get-x-y-coordinates-of-extracted-objects-in-javacv
 * SOURCE: http://stackoverflow.com/questions/11795691/javacv-warning-sign-detection
 */

import java.awt.Color;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class DetectApproxSquare {

	double iLowH;
	double iHighH;
	String filename = "";
	int size = 0;
	IplImage orgImg;
	IplImage imgHSV;
	IplImage imgThresh;
	IplImage newImg;

	public static void main(String args[]) {
		new DetectApproxSquare().main();
	}

	public void main() {

		// creates memory storage that will contain all the dynamic data
		CvMemStorage storage = null;
		storage = cvCreateMemStorage(0);

		// filename = "ColorFades.jpg";
		// filename = "ColorWall.jpg";
		// filename = "Phone.jpg";
		// filename = "Pixels.jpg";
		// filename = "Points.jpg";
		// filename = "Squares.jpg";
		filename = "postit_1.jpg";

		// loads the image
		orgImg = cvLoadImage(filename);

		// size of the "control card"
		size = 15;

		// gets the mean color in the "control card"
		CvScalar detected_mean = mean(orgImg, 0, 0, size, size);
		//CvScalar detected_mean = mean(orgImg, orgImg.width()-size, 0, size, size);
		//CvScalar detected_mean = mean(orgImg, 0, orgImg.height()-size, size, size);
		//CvScalar detected_mean = mean(orgImg, orgImg.width()-size, orgImg.height()-size, size, size);

		// gets the color from the "control card" and recognizes it
		getControlColor(detected_mean);

		imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
		imgThresh = cvCreateImage(cvGetSize(orgImg), 8, 1);

		// sets Saturation and Brightness to the whole spectrum
		double iLowS = 1;
		double iHighS = 254;

		double iLowV = 1;
		double iHighV = 254;

		// converts the color from BGR to HSV
		cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);

		// checks the parts of the image in the range of the color chosen
		cvInRangeS(imgHSV, cvScalar(iLowH, iLowS, iLowV, 0),
				cvScalar(iHighH, iHighS, iHighV, 0), imgThresh);
		// smooths the image according to the different parameters
		cvSmooth(imgThresh, imgThresh, CV_MEDIAN, 15, 0, 0, 0);

		// temporary saves the HSV image (NO IDEA WHY I HAVE TO)
		cvSaveImage("Temp_HSV.jpg", imgThresh);

		// re-loads the image (otherwise "ASSERTION FAILED")
		newImg = cvLoadImage("Temp_HSV.jpg");

		// finds the squares
		findSquares(newImg, storage);

		// releases the images
		cvReleaseImage(orgImg);
		cvReleaseImage(imgThresh);
		cvReleaseImage(imgHSV);
		cvReleaseImage(newImg);

		// clears the memory storage
		cvClearMemStorage(storage);

	}

	public void getControlColor(CvScalar color) {

		int r, g, b, h;

		// System.out.println(color);

		// gets the RGB components from the detected color in the "control card"
		r = (int) color.red();
		g = (int) color.green();
		b = (int) color.blue();

		System.out.println("The RGB color detected is: (" + r + ", " + g + ", " + b + ")");
		System.out.println();

		// converts the RGB color to HSV values - only H matters (apparently)
		float[] hsv = Color.RGBtoHSB(r, g, b, null);
		h = (int) (hsv[0] * 180);
		// s = (int) (hsv[1] * 100);
		// v = (int) (hsv[2] * 100);

		// orange
		if (h > 0 && h <= 22) {
			iLowH = 0;
			iHighH = 22;
		}
		// yellow
		if (h > 22 && h <= 38) {
			iLowH = 22;
			iHighH = 38;
		}
		// green
		if (h > 38 && h <= 75) {
			iLowH = 38;
			iHighH = 75;
		}
		// light blue
		if (h > 75 && h <= 100) {
			iLowH = 75;
			iHighH = 100;
		}
		// blue
		if (h > 100 && h <= 130) {
			iLowH = 100;
			iHighH = 130;
		}
		// violet
		if (h > 130 && h <= 160) {
			iLowH = 130;
			iHighH = 160;
		}
		// red
		if (h > 160 && h <= 180) {
			iLowH = 160;
			iHighH = 180;
		}

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
		cvFindContours(gry, storage, contours, Loader.sizeof(CvContour.class),
				CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE, new CvPoint());

		// counts the contours
		int count = 1;

		//checks if there are rectangles detected
		if (contours.address() == 0)
			System.out.println("No rectangles detected!");

		//in this case, the loop can work
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

					System.out.println("Contour " + count);
					System.out.println("Coordinates: " + tl + tr + br + bl);
					System.out.println("");

					// draws the rectangle
					cvRectangle(cpy, tl, br, CV_RGB(255, 0, 0), 2, 8, 0);

					// increases the contours counter
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