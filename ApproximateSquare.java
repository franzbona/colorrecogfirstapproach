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
	String input = "";
	IplImage orgImg;
	IplImage imgHSV;
	IplImage imgThresh;
	IplImage newImg;

	// enum class definition
	public enum ChosenColor {
		orange, yellow, green, lightblue, blue, violet, red
	}

	ChosenColor color;

	public static void main(String args[]) {
		new ApproximateSquare().main();
	}

	public void main() {

		// creates memory storage that will contain all the dynamic data
		CvMemStorage storage = null;
		storage = cvCreateMemStorage(0);

		// asks the user to choose the color
		getChosenColor();

		// filename = "ColorFades.jpg";
		// filename = "ColorWall.jpg";
		filename = "Phone.jpg";
		// filename = "Pixels.jpg";
		// filename = "Points.jpg";
		// filename = "Squares.jpg";

		// loads the image
		orgImg = cvLoadImage(filename);
		imgHSV = cvCreateImage(cvGetSize(orgImg), 8, 3);
		imgThresh = cvCreateImage(cvGetSize(orgImg), 8, 1);

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

	public void getChosenColor() {

		// asks for input
		Scanner in = new Scanner(System.in);

		// loops till gets a valid ENUM input
		do {
			System.out.println("Which colour would you like to detect?");
			input = in.nextLine().toLowerCase();
			// System.out.println("You entered " + input);

			// checks if input is present, then sets the values
			for (ChosenColor c : ChosenColor.values()) {
				if (c.name().equals(input)) {
					switch (input) {

					case "orange":
						iLowH = 0;
						iHighH = 22;
						return;
					case "yellow":
						iLowH = 22;
						iHighH = 38;
						return;
					case "green":
						iLowH = 38;
						iHighH = 75;
						return;
					case "light blue":
						iLowH = 75;
						iHighH = 100;
						return;
					case "blue":
						iLowH = 100;
						iHighH = 130;
						return;
					case "violet":
						iLowH = 130;
						iHighH = 160;
						return;
					case "red":
						iLowH = 160;
						iHighH = 179;
						return;
					default:
						input = "";
						return;
					}
				}
			}

			// otherwise resets the input to empty
			input = "";
		}

		while (input.isEmpty());

		in.close();

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

		// loops around the contours
		for (cont = contours; cont != null; cont = cont.h_next()) {

			// draws the contours of the identified colored shape
			CvScalar color = CvScalar.BLUE;
			cvDrawContours(cpy, cont, color, CV_RGB(0, 0, 0), -1, CV_FILLED, 8,
					cvPoint(0, 0));

			// creates the bounding rectangle around the contours
			CvRect sq = cvBoundingRect(cont, 0);

			// checks if the shape is too small, in that case it is not drawn
			if ((sq.height() > 5) && (sq.width() > 5)) {

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

}