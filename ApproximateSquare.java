import java.util.Scanner;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.IplImage;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_highgui.*;

public class ApproximateSquare {

	double iLowH;
	double iHighH;
	String filename = "";
	String color;
	IplImage finImg;
	IplImage orgImg;

	public static void main(String args[]) {
		new ApproximateSquare().main();
	}

	public void main() {

		// creates memory storage that contains all the dynamic data
		CvMemStorage storage = null;
		storage = cvCreateMemStorage(0);

		color = "";
		Scanner in = new Scanner(System.in);

		while (color.isEmpty()) {

			System.out.println("Which colour would you like to detect?");
			color = in.nextLine().toLowerCase();
			System.out.println("You entered " + color);
		}
		in.close();

		switch (color) {

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

		// filename = "ColorFades.jpg";
		// filename = "ColorWall.jpg";
		filename = "Phone.jpg";
		// filename = "Pixels.jpg";
		// filename = "Points.jpg";
		// filename = "Squares.jpg";

		orgImg = cvLoadImage(filename);

		double iLowS = 1;
		double iHighS = 254;

		double iLowV = 1;
		double iHighV = 254;

		IplImage imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
		IplImage imgThresh = cvCreateImage(cvGetSize(orgImg), 8, 1);

		cvCvtColor(orgImg, imgHSV, CV_BGR2HSV);

		cvInRangeS(imgHSV, cvScalar(iLowH, iLowS, iLowV, 0),
				cvScalar(iHighH, iHighS, iHighV, 0), imgThresh);
		cvSmooth(imgThresh, imgThresh, CV_MEDIAN, 15, 0, 0, 0);

		cvSaveImage("DIOCANE.jpg", imgThresh);

		// find and draw the squares

		IplImage newImg = cvLoadImage("DIOCANE.jpg");
		findSquares4(newImg, storage);

		// release images
		cvReleaseImage(orgImg);
		cvReleaseImage(imgThresh);
		cvReleaseImage(imgHSV);
		cvReleaseImage(newImg);
		// clear memory storage - reset free space position
		cvClearMemStorage(storage);

	}

	// returns sequence of squares detected on the image.
	// the sequence is stored in the specified memory storage
	void findSquares4(IplImage img, CvMemStorage storage) {

		IplImage cpy = cvLoadImage(filename);

		CvSize cvSize = cvSize(img.width(), img.height());
		IplImage gry = cvCreateImage(cvSize, img.depth(), 1);
		cvCvtColor(img, gry, CV_BGR2GRAY);
		cvThreshold(gry, gry, 200, 255, CV_THRESH_BINARY);
		cvAdaptiveThreshold(gry, gry, 255, CV_ADAPTIVE_THRESH_MEAN_C,
				CV_THRESH_BINARY_INV, 11, 5);

		CvSeq contours = new CvContour(null);
		int noOfContors = cvFindContours(gry, storage, contours,
				Loader.sizeof(CvContour.class), CV_RETR_CCOMP,
				CV_CHAIN_APPROX_NONE, new CvPoint());
		CvSeq ptr = new CvSeq();

		int count = 1;

		for (ptr = contours; ptr != null; ptr = ptr.h_next()) {

			CvScalar color = CvScalar.BLUE;
			cvDrawContours(cpy, ptr, color, CV_RGB(0, 0, 0), -1, CV_FILLED, 8,
					cvPoint(0, 0));

			CvRect sq = cvBoundingRect(ptr, 0);

			if ((sq.height() > 5) && (sq.width() > 5)) {

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

				cvRectangle(cpy, tl, br, CV_RGB(255, 0, 0), 2, 8, 0);
				count++;
			}
		}

		// saves the resultant image
		finImg = cvCreateImage(cvGetSize(orgImg), 8, 3);
		cvSaveImage("R_" + filename, cpy);

		cvReleaseImage(cpy);
		cvReleaseImage(gry);

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